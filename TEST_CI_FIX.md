# CI test-classpath fix — Spring Boot 4.1.1

## Root cause

Spring Boot 4 split feature-specific test support into focused `-test` modules. The project uses:

- `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`

Those classes are provided by `spring-boot-webmvc-test`, which is brought in by the `spring-boot-starter-webmvc-test` starter. `spring-boot-starter-test` by itself is not enough for these MVC test annotations in this project.

## POM changes

The test classpath now explicitly includes:

- `spring-boot-starter-test`
- `spring-boot-starter-webmvc-test`
- `spring-boot-starter-thymeleaf-test`
- `spring-boot-starter-security-test`
- `h2`

The direct `spring-security-test` dependency was replaced by Spring Boot's focused security test starter.

## CI database isolation

`src/test/resources/application-test.yml` configures a unique in-memory H2 database for full-context tests. Flyway is disabled only for the test profile and Hibernate uses `create-drop`. Production remains PostgreSQL + Flyway + `ddl-auto=validate`.

`CinemaOneApplicationTests` and `AdminSecuritySmokeTest` explicitly activate the `test` profile. `FullApplicationRouteSmokeTest` already has its own isolated H2 configuration.

## Required local verification

Run:

```bash
mvn -B -ntp clean verify
```

The GitHub PR and production workflows run the same command. Deployment is not reached when verification fails.
