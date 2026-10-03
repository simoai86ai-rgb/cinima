# AWS + GitHub CI/CD required values

This is the one file to complete before enabling automatic production deployment.
The pipeline is `.github/workflows/deploy-main-to-aws.yml`.

## Deployment architecture

`merge/push to main -> GitHub Actions -> mvn clean verify -> Docker build -> Amazon ECR -> AWS Systems Manager -> EC2 -> Docker health check -> automatic rollback on failure`

AWS authentication from GitHub uses **OIDC and short-lived credentials**. Do not create or store `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` in GitHub for this pipeline.

## 1. Values to collect

| GitHub Environment variable | Example | Where to get it in AWS | Secret? |
|---|---|---|---|
| `AWS_REGION` | `eu-west-3` | The Region containing ECR, EC2 and SSM resources | No |
| `AWS_ROLE_ARN` | `arn:aws:iam::123456789012:role/cinema-one-github-deploy` | IAM -> Roles -> GitHub deployment role -> ARN | No |
| `ECR_REPOSITORY` | `cinema-one` | Amazon ECR -> Repositories -> repository name | No |
| `EC2_INSTANCE_ID` | `i-0123456789abcdef0` | EC2 -> Instances -> Instance ID | No |
| `SSM_RUNTIME_ENV_PARAMETER` | `/cinema-one/prod/runtime-env` | Systems Manager -> Parameter Store -> parameter name | No; the parameter value is secret |
| `PUBLIC_HEALTH_URL` | `https://cinema.example.com/actuator/health` | Your public DNS/HTTPS endpoint | No, optional |

`DEPLOY_DIR` is intentionally fixed by the workflow to `/opt/cinema-one`.

Add the values in GitHub at:

`Repository -> Settings -> Environments -> production -> Environment variables`

Also configure the `production` environment so only branch `main` is allowed to deploy. Optional required reviewers can be enabled if you want a human approval gate after merging.

## 2. AWS account values needed only while creating IAM resources

These do **not** need to be GitHub variables after setup:

- AWS account ID: IAM console/account menu or `aws sts get-caller-identity --query Account --output text`
- GitHub owner/organization name
- GitHub repository name
- ECR repository ARN
- EC2 instance ID

Use the templates under `deploy/aws/iam/` and replace every `__PLACEHOLDER__`.

## 3. ECR repository

Create one private ECR repository, recommended name: `cinema-one`.
Enable image scanning and add a lifecycle policy that keeps a reasonable number of old SHA-tagged images for rollback/history.

Example CLI:

```bash
aws ecr create-repository \
  --repository-name cinema-one \
  --image-scanning-configuration scanOnPush=true \
  --region YOUR_REGION
```

The GitHub pipeline pushes two tags:

- immutable commit tag: `<git-sha>` -- this is what is actually deployed
- convenience tag: `latest` -- never used as the deployment identity

## 4. EC2 instance requirements

The instance must be a Systems Manager managed node and must have Docker and AWS CLI installed.
Run `deploy/aws/bootstrap-ec2.sh` once as root, or install the same dependencies yourself.

Recommended EC2 IAM instance role attachments:

1. AWS managed policy `AmazonSSMManagedInstanceCore`
2. AWS managed policy `AmazonEC2ContainerRegistryReadOnly` (or a stricter ECR pull-only policy)
3. Inline policy based on `deploy/aws/iam/ec2-runtime-parameter-policy.json.example`

If the Parameter Store SecureString uses a **customer-managed KMS key**, also grant the EC2 instance role `kms:Decrypt` on that key. With the default AWS-managed SSM key, follow your account's normal SSM permissions model.

You do not need to open SSH port 22 for this CI/CD design. Deployment commands arrive through Systems Manager.

## 5. Production database (recommended: Amazon RDS PostgreSQL)

Collect these values for the runtime environment:

- RDS endpoint, e.g. `mydb.xxxxx.eu-west-3.rds.amazonaws.com`
- database port, normally `5432`
- database name, e.g. `cinema_one`
- database username
- database password (AWS does not display an existing password later; keep it in Secrets Manager/Parameter Store or reset it if lost)

Security group recommendation: allow PostgreSQL `5432` to RDS **from the EC2 security group**, not from `0.0.0.0/0`.

## 6. Runtime application configuration in Parameter Store

Copy `deploy/aws/runtime.env.example`, replace all placeholders, then store the **whole file** as one SSM Parameter Store `SecureString` named:

`/cinema-one/prod/runtime-env`

Important production values include:

```dotenv
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://RDS_ENDPOINT:5432/cinema_one
DB_USERNAME=cinema_one
DB_PASSWORD=STRONG_PASSWORD
SITE_BASE_URL=https://YOUR_DOMAIN
APP_DEMO_CONTENT_ENABLED=false
APP_BOOTSTRAP_ADMIN_ENABLED=false
APP_ADMIN_RESET_PASSWORD=false
```

### First production admin

For the very first production deployment only, set:

```dotenv
APP_BOOTSTRAP_ADMIN_ENABLED=true
APP_ADMIN_EMAIL=YOUR_ADMIN_EMAIL
APP_ADMIN_PASSWORD=A_LONG_UNIQUE_PASSWORD
APP_ADMIN_RESET_PASSWORD=false
```

Deploy once, verify that the account exists, then edit the SecureString and set `APP_BOOTSTRAP_ADMIN_ENABLED=false`; deploy again. Do **not** use the development password `ChangeMe123!` in production.

## 7. GitHub OIDC provider in AWS

If your AWS account does not already have GitHub as an OIDC provider, create:

- Provider URL: `https://token.actions.githubusercontent.com`
- Audience: `sts.amazonaws.com`

Then create the GitHub deployment IAM role using:

- trust policy template: `deploy/aws/iam/github-oidc-trust-policy.json.example`
- permission policy template: `deploy/aws/iam/github-deploy-policy.json.example`

The trust policy is intentionally scoped to the GitHub **production environment** for this repository. The GitHub environment should itself be restricted to the `main` branch.

After creating the role, copy its ARN into GitHub variable `AWS_ROLE_ARN`.

## 8. Network / reverse proxy

The deployment script binds the application to `127.0.0.1:8080` by default. Put Nginx, Caddy, or an AWS load balancer in front of it and terminate HTTPS there.

If you intentionally want direct port 8080 exposure, change `HOST_BIND_ADDRESS` in the remote deployment invocation/script only after adjusting the EC2 security group. The safer production default is loopback + HTTPS reverse proxy.

## 9. What happens after a merge to main

1. GitHub checks out the exact commit merged to `main`.
2. Java 21 is installed and Maven cache restored.
3. `mvn -B -ntp clean verify` must succeed.
4. GitHub obtains temporary AWS credentials through OIDC.
5. The JAR is packaged into the Docker image.
6. Image is pushed to ECR with the Git SHA tag.
7. Systems Manager runs `deploy/aws/deploy-release.sh` on the selected EC2 instance.
8. EC2 reads the encrypted runtime environment from Parameter Store.
9. EC2 pulls the exact ECR image, starts it, and waits for `/actuator/health` to become `UP`.
10. If health fails, the script automatically starts the previous Docker image and restores the previous runtime env file.

No deployment occurs if Maven verification fails.

## 10. Final checklist

- [ ] ECR repository exists
- [ ] EC2 is online in Systems Manager Managed nodes
- [ ] Docker works on EC2
- [ ] EC2 instance IAM role has SSM + ECR pull + Parameter Store access
- [ ] RDS/production PostgreSQL is reachable from EC2
- [ ] `/cinema-one/prod/runtime-env` SecureString exists
- [ ] GitHub OIDC provider exists in IAM
- [ ] GitHub deployment IAM role trust is restricted to this repository/environment
- [ ] GitHub Environment `production` exists and permits `main`
- [ ] GitHub Environment variables are filled in
- [ ] HTTPS/reverse proxy is configured if the site is public
- [ ] First admin bootstrap has been disabled after first successful production setup
