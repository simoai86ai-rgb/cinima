# Runtime fix: Thymeleaf home template

## Symptom

The application starts successfully, but requesting `/en`, `/fr`, or `/ar` fails while rendering `templates/public/home.html` with:

```text
EL1004E: Method call: Method padLeft(...) cannot be found on type org.thymeleaf.expression.Strings
```

## Root cause

`#strings.padLeft(...)` is not a Thymeleaf 3.1 `#strings` expression utility method.

## Fix

The hero slide counter now uses Thymeleaf's numeric formatter:

```html
th:text="${#numbers.formatInteger(stat.index + 1, 2) + ' / ' + #numbers.formatInteger(heroMovies.size(), 2)}"
```

This renders values such as `01 / 05` without relying on a non-existent string helper.

## Regression protection

`PublicHomeTemplateTest` was added to render the `/en` home page with a hero movie and assert that `01 / 01` is present.

The test profile also disables demo-content bootstrapping so the generic context test does not depend on seeded genres.

## Run locally

```bash
mvn clean test
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/en
http://localhost:8080/fr
http://localhost:8080/ar
```
