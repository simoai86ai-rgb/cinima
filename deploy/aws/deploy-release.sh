#!/usr/bin/env bash
set -Eeuo pipefail

: "${AWS_REGION:?AWS_REGION is required}"
: "${ECR_REGISTRY:?ECR_REGISTRY is required}"
: "${IMAGE_URI:?IMAGE_URI is required}"
: "${RUNTIME_ENV_PARAMETER:?RUNTIME_ENV_PARAMETER is required}"

APP_NAME="${APP_NAME:-cinema-one-app}"
APP_DIR="${APP_DIR:-/opt/cinema-one}"
HOST_BIND_ADDRESS="${HOST_BIND_ADDRESS:-127.0.0.1}"
HOST_PORT="${HOST_PORT:-8080}"
CONTAINER_PORT="${CONTAINER_PORT:-8080}"
HEALTH_TIMEOUT_SECONDS="${HEALTH_TIMEOUT_SECONDS:-120}"
RUNTIME_ENV_FILE="${APP_DIR}/runtime.env"
PREVIOUS_ENV_FILE="${APP_DIR}/runtime.env.previous"

log() {
  printf '[cinema-one-deploy] %s\n' "$*"
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    log "ERROR: required command '$1' is not installed. Run deploy/aws/bootstrap-ec2.sh once on the instance."
    exit 1
  fi
}

run_container() {
  local image="$1"
  docker run -d \
    --name "$APP_NAME" \
    --restart unless-stopped \
    --env-file "$RUNTIME_ENV_FILE" \
    -p "${HOST_BIND_ADDRESS}:${HOST_PORT}:${CONTAINER_PORT}" \
    --label "com.cinemaone.managed-by=github-actions" \
    --label "com.cinemaone.image=${image}" \
    "$image"
}

wait_until_healthy() {
  local deadline=$((SECONDS + HEALTH_TIMEOUT_SECONDS))
  local status
  while (( SECONDS < deadline )); do
    status="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$APP_NAME" 2>/dev/null || true)"
    case "$status" in
      healthy)
        return 0
        ;;
      unhealthy|exited|dead)
        return 1
        ;;
    esac
    sleep 4
  done
  return 1
}

require_command aws
require_command docker
mkdir -p "$APP_DIR"
umask 077

if [[ -f "$RUNTIME_ENV_FILE" ]]; then
  cp "$RUNTIME_ENV_FILE" "$PREVIOUS_ENV_FILE"
fi

log "Loading production runtime configuration from AWS Systems Manager Parameter Store."
aws ssm get-parameter \
  --region "$AWS_REGION" \
  --name "$RUNTIME_ENV_PARAMETER" \
  --with-decryption \
  --query 'Parameter.Value' \
  --output text > "${RUNTIME_ENV_FILE}.new"

if [[ ! -s "${RUNTIME_ENV_FILE}.new" ]]; then
  log "ERROR: runtime environment parameter returned an empty value."
  rm -f "${RUNTIME_ENV_FILE}.new"
  exit 1
fi
mv "${RUNTIME_ENV_FILE}.new" "$RUNTIME_ENV_FILE"
chmod 600 "$RUNTIME_ENV_FILE"

log "Authenticating this EC2 instance to Amazon ECR using the instance IAM role."
aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "$ECR_REGISTRY" >/dev/null

log "Pulling release image ${IMAGE_URI}."
docker pull "$IMAGE_URI"

PREVIOUS_IMAGE=""
if docker inspect "$APP_NAME" >/dev/null 2>&1; then
  PREVIOUS_IMAGE="$(docker inspect --format '{{.Config.Image}}' "$APP_NAME")"
  log "Current release is ${PREVIOUS_IMAGE}."
  docker rm -f "$APP_NAME" >/dev/null
fi

log "Starting new release."
run_container "$IMAGE_URI" >/dev/null

if wait_until_healthy; then
  log "Deployment succeeded; container health is UP."
  rm -f "$PREVIOUS_ENV_FILE"
  docker image prune -f >/dev/null 2>&1 || true
  exit 0
fi

log "ERROR: new release did not become healthy. Showing recent application logs."
docker logs --tail 160 "$APP_NAME" 2>&1 || true
docker rm -f "$APP_NAME" >/dev/null 2>&1 || true

if [[ -n "$PREVIOUS_IMAGE" ]]; then
  log "Rolling back to ${PREVIOUS_IMAGE}."
  if ! docker image inspect "$PREVIOUS_IMAGE" >/dev/null 2>&1; then
    docker pull "$PREVIOUS_IMAGE" || true
  fi
  if [[ -f "$PREVIOUS_ENV_FILE" ]]; then
    mv "$PREVIOUS_ENV_FILE" "$RUNTIME_ENV_FILE"
    chmod 600 "$RUNTIME_ENV_FILE"
  fi
  run_container "$PREVIOUS_IMAGE" >/dev/null
  if wait_until_healthy; then
    log "Rollback succeeded."
  else
    log "CRITICAL: rollback container also failed health checks."
    docker logs --tail 160 "$APP_NAME" 2>&1 || true
  fi
else
  log "No previous release exists; automatic rollback is not possible on the first deployment."
fi

exit 1
