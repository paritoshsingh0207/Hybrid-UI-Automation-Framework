# Selenium Java BDD Framework

A lean but production-style Selenium + Java + Cucumber BDD framework.

## Structure

- BaseTest: shared test setup access
- DriverManager: creates and manages WebDriver
- Hooks: scenario lifecycle and failure screenshots
- CommonActions: reusable click, sendText, dropdown, radio/checkbox, waits and screenshots
- Page Objects: locators and business-level page actions
- Step Definitions: readable BDD glue only
- Allure: standard Cucumber Allure reporting

## Run

```bash
mvn clean test
```

Headless:

```bash
mvn clean test -Dheadless=true
```

Allure:

```bash
mvn allure:serve
```

Default URL is configured in `src/test/resources/config.properties`.
