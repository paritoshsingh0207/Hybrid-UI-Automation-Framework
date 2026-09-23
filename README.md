# Simple Playwright Java BDD Framework

A deliberately small Playwright + Java + Cucumber framework for learning, interviews, and portfolio demonstrations.

## Stack
- Java 8+
- Maven
- Playwright for Java
- Cucumber BDD
- JUnit 4 runner
- Allure Report

## Project structure
```text
src/test/java/com/simple/playwright/
  runner/RunCucumberTest.java
  steps/LoginSteps.java
src/test/resources/
  features/login.feature
  allure.properties
pom.xml
```

## First-time browser installation
Install Chromium once:
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

## Run
```bash
mvn clean test
```

The browser opens visibly by default. Run headless with:
```bash
mvn clean test -Dheadless=true
```

## Allure report
After the test finishes:
```bash
mvn allure:serve
```

The example scenario logs in to https://practicetestautomation.com/practice-test-login/ using the public demo credentials shown by that practice site.

## Why this branch is simple
There is no Selenium dependency, hybrid driver abstraction, Excel layer, custom retry engine, PDF report, or self-healing. Cucumber describes the scenario, Playwright performs the browser actions, JUnit performs the assertion, and Allure generates the report.
