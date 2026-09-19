# Hybrid Selenium + Playwright UI Automation Framework

A Java 8 Maven automation framework designed to demonstrate reusable framework engineering rather than a single website script.

The same application-facing automation layer can run through **Selenium** or **Playwright** at runtime, with Cucumber/TestNG, feature/Excel data, parallel execution, retries, controlled self-healing, screenshots, logging, Allure, and a detailed PDF evidence report.

Two applications are included to demonstrate reusability:

1. **Practice Test Automation** - feature-file and Excel-driven demo flows.
2. **OrangeHRM Open Source Demo** - second application plus secure runtime credential handling.

## Key capabilities

- Java 8 + Maven
- Runtime Selenium / Playwright selection
- Multi-browser support with compatibility validation
- Common `UiDriver` abstraction
- Page Object Model
- Cucumber BDD + TestNG
- Feature, Excel, or combined test-data execution
- Thread-local browser isolation for parallel tests
- Retry policy
- Failure screenshots
- Log4j2 logging
- Controlled fallback-locator self-healing
- Allure reporting
- Detailed PDF report with ordered steps and assertions
- Expected vs actual assertion evidence
- Secure local credential file support
- GitHub Actions secret support

## Architecture

```text
Cucumber / Excel / TestNG
          |
          v
Steps -> Scenario Executor -> Page Objects
          |
          v
       UiDriver
          |
    UiDriverFactory
      /       \
Selenium   Playwright
      \       /
    HealingUiDriver
          |
    LoggingUiDriver
          |
        Browser

Execution evidence
  -> screenshots
  -> retries
  -> self-healing events
  -> PDF report
  -> Allure
```

Framework infrastructure lives under `src/main/java/com/hybrid/framework`. Application-specific automation lives under `src/test/java/com/hybrid/tests`.

## Prerequisites

- JDK 8
- Maven 3.x
- Git
- Chrome / Firefox / Edge for Selenium runs as required
- Playwright browser binaries for Playwright runs

```powershell
java -version
mvn -version
git --version
```

## Clone

```powershell
git clone https://github.com/paritoshsingh0207/Hybrid-UI-Automation-Framework.git
cd Hybrid-UI-Automation-Framework
```

## Practice Test Automation - Selenium

Feature data:

```powershell
mvn clean test -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

Excel data:

```powershell
mvn clean test -Ddata.source=excel -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

Both:

```powershell
mvn clean test -Ddata.source=both -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

## Playwright

Install browser binaries once:

```powershell
mvn -q -DskipTests compile
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

Run:

```powershell
mvn clean test -Ddata.source=feature -Dengine=playwright -Dbrowser=chromium -Dheadless=false -Dparallel.mode=none
```

## OrangeHRM - secure local credentials

Do **not** store the OrangeHRM username/password in this repository.

Create a properties file outside the cloned project, for example:

```text
C:\AutomationSecrets\orangehrm-credentials.properties
```

Contents:

```properties
orangehrm.username=your-username
orangehrm.password=your-password
```

Run:

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

The framework resolves OrangeHRM credentials in this order:

```text
-Dcredentials.file supplied?
       |
       +-- yes --> local properties file
       |
       +-- no  --> ORANGEHRM_USERNAME / ORANGEHRM_PASSWORD
```

If a credential file is explicitly supplied but missing/incomplete, execution fails clearly instead of silently falling back.

`credentials.example.properties` is an empty safe template only. Never put a real password into that tracked file.

## OrangeHRM - environment / CI credentials

Without `-Dcredentials.file`, set:

```text
ORANGEHRM_USERNAME
ORANGEHRM_PASSWORD
```

PowerShell example:

```powershell
$env:ORANGEHRM_USERNAME="your-username"
$env:ORANGEHRM_PASSWORD="your-password"

mvn clean test -DsuiteXmlFile=orangehrm-testng.xml -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

In GitHub Actions, store the same names under:

```text
Repository -> Settings -> Secrets and variables -> Actions
```

The workflow reads the GitHub secrets as environment variables; secret values are not committed in YAML.

See `docs/SECURITY.md` for the complete credential design.

## Reporting

After execution, important outputs include:

```text
artifacts/logs/
artifacts/screenshots/
artifacts/self-healing/
target/allure-results/
target/reports/hybrid-automation-report.pdf
```

The custom PDF records:

- final test status
- engine/browser/thread/attempt
- ordered business actions
- action PASS/FAIL
- assertions recorded through `ReportAssert`
- expected and actual values
- assertion PASS/FAIL
- retry history
- screenshots when available
- self-healing evidence
- environment information

Passwords remain masked by default. Do not use:

```text
-Dreport.showSensitiveData=true
```

with real credentials.

Generate Allure HTML:

```powershell
mvn allure:report
```

Serve it locally:

```powershell
mvn allure:serve
```

## Retry

```powershell
mvn clean test -Dretry.count=2
mvn clean test -Dretry.count=0
mvn clean test -Dretry.count=1 -Dretry.assertions=true
```

Assertion failures are not retried by default.

## Parallel execution

```powershell
mvn clean test -Dparallel.mode=methods -Dthread.count=4 -Ddataprovider.thread.count=4
```

For debugging, start with:

```text
-Dparallel.mode=none
```

## Controlled self-healing

Self-healing uses only fallback locators intentionally declared in a page object. It does not invent selectors, use an LLM, or modify source code automatically.

```powershell
-Dself.healing.enabled=true
-Dself.healing.enabled=false
```

A successful fallback is recorded in logs/report evidence.

## Adding another website

Add the new application's pages, flows, steps and runner under `src/test/java/com/hybrid/tests/<application>` and reuse the core framework:

- `UiDriver`
- Selenium / Playwright implementations
- logging
- healing
- retry
- screenshots
- PDF evidence
- Allure
- secure configuration

This separation is the central design goal of the project.

## Full documentation

- `docs/FRAMEWORK_GUIDE.md` - architecture, class responsibilities, security setup and complete run instructions
- `docs/SECURITY.md` - local credential file, environment variables, GitHub Actions secrets and masking rules
- `docs/HOW_IT_WORKS.md` - concise execution-flow overview
- `VALIDATION.md` - validation notes

## Interview summary

> I designed a Java 8 hybrid UI automation framework where tests are not directly coupled to Selenium. A common UiDriver abstraction allows runtime Selenium or Playwright execution. The framework adds Cucumber/TestNG, Excel data, retry handling, parallel isolation, controlled self-healing, screenshots, Allure and detailed PDF evidence. I validated the reusable design against two applications and implemented secure credential resolution through local files or CI secret stores rather than source-controlled passwords.
