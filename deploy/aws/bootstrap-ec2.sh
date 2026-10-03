#!/usr/bin/env bash
set -Eeuo pipefail

if [[ "$(id -u)" -ne 0 ]]; then
  echo "Run this script as root, for example: sudo bash deploy/aws/bootstrap-ec2.sh" >&2
  exit 1
fi

install_aws_cli_v2() {
  if command -v aws >/dev/null 2>&1; then
    return 0
  fi

  local machine aws_arch
  machine="$(uname -m)"
  case "$machine" in
    x86_64) aws_arch="x86_64" ;;
    aarch64|arm64) aws_arch="aarch64" ;;
    *)
      echo "Unsupported CPU architecture for automatic AWS CLI install: $machine" >&2
      exit 1
      ;;
  esac

  command -v curl >/dev/null 2>&1 || { echo "curl is required" >&2; exit 1; }
  command -v unzip >/dev/null 2>&1 || { echo "unzip is required" >&2; exit 1; }

  rm -rf /tmp/aws /tmp/awscliv2.zip
  curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-${aws_arch}.zip" -o /tmp/awscliv2.zip
  unzip -q /tmp/awscliv2.zip -d /tmp
  /tmp/aws/install --update
  rm -rf /tmp/aws /tmp/awscliv2.zip
}

install_ubuntu_debian() {
  apt-get update
  DEBIAN_FRONTEND=noninteractive apt-get install -y docker.io ca-certificates curl unzip
  systemctl enable --now docker
  install_aws_cli_v2
}

install_amazon_linux() {
  dnf install -y docker curl unzip
  systemctl enable --now docker
  install_aws_cli_v2
}

if [[ -f /etc/os-release ]]; then
  # shellcheck disable=SC1091
  . /etc/os-release
else
  echo "Unsupported OS: /etc/os-release is missing." >&2
  exit 1
fi

case "${ID:-}" in
  ubuntu|debian)
    install_ubuntu_debian
    ;;
  amzn)
    install_amazon_linux
    ;;
  *)
    echo "Unsupported Linux distribution '${ID:-unknown}'. Install Docker Engine and AWS CLI v2 manually." >&2
    exit 1
    ;;
esac

mkdir -p /opt/cinema-one
chmod 755 /opt/cinema-one

if systemctl list-unit-files amazon-ssm-agent.service >/dev/null 2>&1; then
  systemctl enable --now amazon-ssm-agent || true
fi

echo "Docker: $(docker --version)"
echo "AWS CLI: $(aws --version 2>&1)"
if systemctl is-active --quiet amazon-ssm-agent 2>/dev/null; then
  echo "SSM Agent: active"
else
  echo "WARNING: amazon-ssm-agent is not active. The instance must appear as a managed node in Systems Manager before CI/CD can deploy." >&2
fi

echo "EC2 bootstrap complete."
