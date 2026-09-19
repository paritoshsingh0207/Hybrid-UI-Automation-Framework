# Hybrid UI Automation Framework - Architecture and Run Guide

## 1. Purpose

This repository is a reusable Java 8 UI automation framework designed to demonstrate framework-level QA automation capability rather than a single website script.

The same framework infrastructure supports multiple applications. The repository currently contains two sample applications:

- Practice Test Automation - used for feature-file and Excel-driven examples.
- OrangeHRM Open Source Demo - used to prove the framework can support another application without rewriting the core framework and to demonstrate secure credential handling.

## 2. Core capabilities

- Java 8 + Maven
- Selenium and Playwright selected at runtime
- Browser compatibility validation
- Page Object Model
- Cucumber BDD + TestNG
- Feature-file data, Excel data, or both
- Thread-local driver isolation for parallel execution
- Retry handling
- Failure screenshots
- Log4j2 logging
- Controlled locator self-healing using declared fallback locators
- Allure result generation and HTML reporting
- Detailed PDF reporting with ordered business actions and assertions
- Expected vs actual assertion evidence
- Retry and self-healing evidence
- Secure credentials from a local properties file or CI environment secrets
- GitHub Actions pipeline

## 3. High-level architecture

```text
Test Definition
(Cucumber Feature / Excel / TestNG)
              |
              v
Application Layer
(Steps -> Scenario Executor -> Page Objects)
              |
              v
           UiDriver
              |
      UiDriverFactory
              |
      +-------+-------+
      |               |
      v               v
 SeleniumUiDriver   PlaywrightUiDriver
      \               /
       \             /
        v           v
      HealingUiDriver
              |
              v
      LoggingUiDriver
              |
              v
            Browser

Execution events
      |
      +--> Screenshots / artifacts
      +--> Retry tracking
      +--> ExecutionResultStore
      +--> Detailed PDF report
      +--> Allure results/report
```

`UiDriverFactory` selects Selenium or Playwright and then wraps the raw driver with the healing and logging decorators. Page objects therefore do not need separate Selenium and Playwright code paths.

## 4. Main project structure

```text
src/main/java/com/hybrid/framework/
  artifacts/          reusable screenshot infrastructure
  config/             runtime and secret configuration
  core/               UiDriver, factory, locator model, driver context
  data/               reusable Excel reader
  enums/              engine/browser/locator enums
  healing/            fallback-locator self-healing
  logging/            action logging and sanitization
  playwright/         Playwright UiDriver implementation
  reporting/          reusable Allure helper
  selenium/           Selenium UiDriver implementation

src/test/java/com/hybrid/framework/
  base/               TestNG lifecycle base
  data/               test-side Excel provider
  listeners/          suite/test/report listeners
  reporting/          PDF evidence model and generator
  retry/              retry policy and analyzer

src/test/java/com/hybrid/tests/
  pages/               Practice Test Automation pages
  scenarios/           shared Practice login flow
  stepdefinitions/     Cucumber steps
  runners/             Cucumber runner
  orangehrm/           second application sample

src/test/resources/
  features/            normal Cucumber features
  secure-features/     OrangeHRM feature without credentials
  testdata/            Excel test data
```

## 5. Why `src/main` and `src/test` are separate

`src/main/java` contains reusable framework infrastructure. It should not know the details of a particular application.

`src/test/java` contains test execution code, page objects, business flows, runners, listeners and report evidence associated with test runs.

The separation supports the portfolio claim that this is a reusable automation framework rather than one website-specific codebase.

## 6. Runtime configuration

Important system properties include:

| Property | Typical value | Purpose |
|---|---|---|
| `engine` | `selenium` / `playwright` | automation engine |
| `browser` | `chrome`, `firefox`, `edge`, `chromium`, `webkit` | browser selection |
| `data.source` | `feature`, `excel`, `both` | test data source |
| `headless` | `true` / `false` | browser visibility |
| `parallel.mode` | `none`, `methods`, `classes`, `tests`, `instances` | TestNG parallel mode |
| `thread.count` | `4` | execution threads |
| `dataprovider.thread.count` | `4` | data-provider threads |
| `retry.count` | `1` | extra attempts for retryable failures |
| `self.healing.enabled` | `true` / `false` | fallback locator handling |
| `pdf.report.enabled` | `true` / `false` | custom PDF report |
| `report.showSensitiveData` | `false` | whether sensitive demo values may appear in PDF evidence |
| `credentials.file` | local file path | optional local secret file |

## 7. Prerequisites

- JDK 8
- Maven 3.9 or compatible Maven 3.x
- Chrome / Firefox / Edge for Selenium as required
- Playwright browser binaries for Playwright runs
- Git for cloning the repository

Check:

```powershell
java -version
mvn -version
git --version
```

## 8. Clone and open the framework

```powershell
git clone https://github.com/paritoshsingh0207/Hybrid-UI-Automation-Framework.git
cd Hybrid-UI-Automation-Framework
```

Import the root `pom.xml` as a Maven project in IntelliJ IDEA.

## 9. Run the Practice Test Automation sample

### Selenium + Cucumber feature

```powershell
mvn clean test -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

### Selenium + Excel

```powershell
mvn clean test -Ddata.source=excel -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

### Selenium + both feature and Excel

```powershell
mvn clean test -Ddata.source=both -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

## 10. Playwright setup and execution

Install Playwright browsers once:

```powershell
mvn -q -DskipTests compile
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

Run:

```powershell
mvn clean test -Ddata.source=feature -Dengine=playwright -Dbrowser=chromium -Dheadless=false -Dparallel.mode=none
```

## 11. OrangeHRM secure local credential setup

Do not store the real username or password in Git.

Create a directory outside the repository, for example:

```text
C:\AutomationSecrets\
```

Create:

```text
C:\AutomationSecrets\orangehrm-credentials.properties
```

Contents:

```properties
orangehrm.username=your-username
orangehrm.password=your-password
```

The repository contains `credentials.example.properties` with blank values only. It is a template, not a place to store real credentials.

### Run OrangeHRM with the local file

```powershell
mvn clean test `
  -DsuiteXmlFile=orangehrm-testng.xml `
  -Dcredentials.file="C:\AutomationSecrets\orangehrm-credentials.properties" `
  -Ddata.source=feature `
  -Dengine=selenium `
  -Dbrowser=chrome `
  -Dheadless=false `
  -Dparallel.mode=none
```

Credential resolution is:

```text
-Dcredentials.file supplied?
        |
        +-- yes --> local properties file
        |
        +-- no  --> ORANGEHRM_USERNAME / ORANGEHRM_PASSWORD
```

If an explicit credential file is missing or incomplete, the run fails instead of silently falling back. This makes configuration errors visible.

## 12. OrangeHRM with environment variables

If `-Dcredentials.file` is not provided, use:

```powershell
$env:ORANGEHRM_USERNAME="your-username"
$env:ORANGEHRM_PASSWORD="your-password"

mvn clean test -DsuiteXmlFile=orangehrm-testng.xml -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

This is also the model used by CI/CD.

## 13. GitHub Actions credentials

Create repository secrets:

```text
ORANGEHRM_USERNAME
ORANGEHRM_PASSWORD
```

Path in GitHub:

```text
Repository -> Settings -> Secrets and variables -> Actions
```

The workflow exposes those secrets as environment variables to Maven. The values are not written into workflow YAML.

## 14. Credential security rules

Never commit a real credential into:

- Java source
- `.feature` files
- Excel files
- `pom.xml`
- TestNG XML
- workflow YAML
- README / documentation examples

The `.gitignore` protects common local secret-file names including `*-credentials.properties`.

The local properties file is still plain text on the local machine. For enterprise production use, replace the provider with a secret manager such as Azure Key Vault, AWS Secrets Manager or HashiCorp Vault.

Do not run real credentials with:

```text
-Dreport.showSensitiveData=true
```

Keep the default `false` so password evidence stays masked.

## 15. Detailed PDF reporting

The custom PDF report is generated at:

```text
target/reports/hybrid-automation-report.pdf
```

It records:

- test name and overall status
- engine and browser
- thread and attempt number
- start/end time and duration
- ordered user/business actions
- PASS/FAIL for each recorded action
- assertion name
- expected value
- actual value
- PASS/FAIL for each assertion
- failure message
- screenshot path/image where available
- retry history
- self-healing events
- environment details

Example evidence:

```text
ACTION | PASSED
User enters the configured OrangeHRM username

ACTION | PASSED
User enters the OrangeHRM password as '<masked>'

ASSERTION | PASSED
Verify OrangeHRM dashboard heading
Expected: Dashboard
Actual: Dashboard
```

Use `ReportEvidenceContext` for business actions and `ReportAssert` for assertions that should appear individually in the PDF.

## 16. Allure reporting

Raw results:

```text
target/allure-results/
```

Generate HTML:

```powershell
mvn allure:report
```

Generated report:

```text
target/site/allure-maven-plugin/index.html
```

Serve locally:

```powershell
mvn allure:serve
```

## 17. Screenshots and logs

Typical evidence paths:

```text
artifacts/logs/
artifacts/screenshots/
artifacts/self-healing/
target/allure-results/
target/reports/
```

Passwords are intentionally masked in normal logging/reporting paths.

## 18. Retry strategy

Default:

```text
retry.count=1
retry.assertions=false
```

Examples:

```powershell
mvn clean test -Dretry.count=2
mvn clean test -Dretry.count=0
mvn clean test -Dretry.count=1 -Dretry.assertions=true
```

The framework is designed to retry transient automation failures without automatically hiding deterministic assertion failures.

## 19. Controlled self-healing

Self-healing is deterministic, not AI-generated locator mutation.

A `UiLocator` may declare a primary locator and approved fallback candidates. If the primary locator fails with a locator-related failure, `HealingUiDriver` can try those approved alternatives and record which one worked.

The framework does not edit source code and does not invent selectors.

Toggle:

```powershell
mvn clean test -Dself.healing.enabled=true
mvn clean test -Dself.healing.enabled=false
```

## 20. Parallel execution

First visual/debug run:

```powershell
-Dparallel.mode=none
```

Parallel run example:

```powershell
mvn clean test -Dparallel.mode=methods -Dthread.count=4 -Ddataprovider.thread.count=4
```

Driver and report contexts are isolated per execution/thread so one test should not reuse another test's browser/evidence.

## 21. Adding a new application

Do not modify Selenium/Playwright core code just to add another website.

Add application-specific code such as:

```text
src/test/java/com/hybrid/tests/newapp/
  config/
  pages/
  scenarios/
  stepdefinitions/
  runners/
```

Then reuse:

- `UiDriver`
- `UiDriverFactory`
- logging
- healing
- retry
- screenshot management
- PDF evidence
- Allure
- secret configuration

This is the central proof that the framework is application-independent at the infrastructure layer.

## 22. Interview explanation

A concise way to present the project:

> I designed a Java 8 hybrid UI automation framework where page and test code are not directly coupled to Selenium. A common UiDriver abstraction allows runtime Selenium or Playwright execution. The framework adds controlled self-healing, logging, screenshots, retries, parallel execution, Cucumber/TestNG, Excel data, Allure and detailed PDF evidence. I validated reusability with two separate applications, and the OrangeHRM sample demonstrates secure runtime credential handling through local files or CI secrets instead of source-controlled passwords.

## 23. Troubleshooting

### `Unsupported browser 'selenium'`

Use separate properties:

```text
-Dengine=selenium -Dbrowser=chrome
```

### Allure report says no results directory

Confirm tests created:

```text
target/allure-results/
```

Then run:

```powershell
mvn allure:report
```

### OrangeHRM credential file error

Check:

- `-Dcredentials.file` path is correct;
- the file is readable;
- the file contains `orangehrm.username` and `orangehrm.password`;
- the file is not empty;
- the file is not being committed to Git.

### Real password appears in a report

Do not use:

```text
-Dreport.showSensitiveData=true
```

with real credentials. Keep the default value `false`.

## 24. Recommended portfolio demonstration sequence

1. Explain the `UiDriver` abstraction and factory.
2. Run the Practice Test Automation scenario with Selenium.
3. Run the same framework using Playwright.
4. Show Excel/feature data-source selection.
5. Show detailed PDF assertion evidence and Allure.
6. Show a controlled self-healing event.
7. Run OrangeHRM using a credential file outside the repository.
8. Show `.gitignore` and `SecretConfig` to explain security.
9. Show GitHub Actions using repository secrets rather than committed credentials.
10. Explain how a third application would reuse the same infrastructure.
