# CI/CD QA Report

## Scope

This report covers the GitHub Actions -> AWS EC2 delivery path added to the Phase 2 QA-fixed project.

## Pipeline behavior

- Pull requests targeting `main` run `mvn -B -ntp clean verify`.
- Every push/merge to `main` runs `mvn -B -ntp clean verify` again before any deployment.
- AWS authentication uses GitHub OIDC and an IAM role; long-lived AWS access keys are not required.
- The application image is pushed to private Amazon ECR with both the immutable Git SHA and `latest` tags.
- Deployment uses AWS Systems Manager Run Command instead of SSH.
- The EC2 instance loads runtime secrets/config from one SSM Parameter Store `SecureString`.
- Production deploys the exact SHA-tagged image.
- Docker health checks `/actuator/health`.
- Failed health checks trigger rollback to the previous Docker image and previous runtime env file.
- Deployments are serialized by GitHub Actions concurrency to avoid overlapping production releases.

## Static validation performed in this environment

- `pom.xml` XML parse: PASS
- `application.yml`: YAML parse PASS
- `application-prod.yml`: YAML parse PASS
- `.github/workflows/deploy-main-to-aws.yml`: YAML parse PASS
- IAM JSON templates: JSON parse PASS
- `deploy/aws/bootstrap-ec2.sh`: `bash -n` PASS
- `deploy/aws/deploy-release.sh`: `bash -n` PASS
- JavaScript syntax for all application JS files: PASS
- 22 Thymeleaf templates scanned
- 105 i18n keys confirmed identical across EN / FR / AR
- Flyway migrations confirmed sequential V1 -> V4
- Unsafe same-element `th:each` + `th:replace` / `th:insert`: 0
- SSM Run Command parameters JSON generation with representative values: PASS

## Deployment script behavior tests

Because Docker and AWS CLI are not available as real services in this execution environment, the remote deployment script was exercised with command mocks.

- Healthy new release path: PASS
- Failed new release -> previous image rollback path: PASS
- Previous runtime env restoration during rollback: PASS

## Runtime verification added to the project

The existing Spring Boot integration suite already renders the public/admin routes. The full application route smoke test now also requests `/actuator/health` and requires `status=UP`.

The GitHub workflows execute the real Maven suite on a GitHub-hosted runner before image build/deployment. A failed Maven verification stops the pipeline before AWS deployment.

## What cannot be proven without the target AWS account

A live end-to-end AWS deployment cannot be executed until the repository has the real AWS values described in `docs/AWS_CICD_REQUIRED_VALUES.md` and the IAM/ECR/EC2/SSM resources exist. The pipeline intentionally fails early when required GitHub environment variables are absent.

## 2026-10-03 test classpath correction

- Added `spring-boot-starter-webmvc-test` for Spring Boot 4 MVC test annotations.
- Added `spring-boot-starter-thymeleaf-test` for template-oriented tests.
- Replaced direct `spring-security-test` with `spring-boot-starter-security-test`.
- Added isolated H2 `application-test.yml` for full-context CI tests.
- `CinemaOneApplicationTests` and `AdminSecuritySmokeTest` activate the `test` profile.
- Production deployment remains gated by `mvn -B -ntp clean verify`.
