# Playwright Java BDD Framework

A lean but production-style Playwright + Java + Cucumber BDD framework.

## Structure

- BaseTest: shared page access
- BrowserManager: creates and manages Playwright, Browser, Context and Page
- Hooks: scenario lifecycle and failure screenshots
- CommonActions: reusable click, sendText, dropdown, radio/checkbox, hover and screenshots
- Page Objects: selectors and business-level page actions
- Step Definitions: readable BDD glue only
- Allure: standard Cucumber Allure reporting

## Run

Install Playwright Chromium once if required:

```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

Run tests:

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
