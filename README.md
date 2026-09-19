# Hybrid Selenium + Playwright Automation Framework

A Java 8 Maven framework where the same test flow can run with **Selenium** or **Playwright**, and test data can come from either a **Cucumber feature file** or **Excel**.

The repository now contains two sample applications:

1. **Practice Test Automation** — public training site used for the normal feature/Excel examples.
2. **OrangeHRM Open Source Demo** — second application used to prove that the framework layer is reusable across websites. Its credentials are supplied outside source control.

## What is included

- Selenium / Playwright selection at runtime
- Chrome, Edge, Firefox, Chromium, WebKit and Safari where supported by the selected engine
- Cucumber + TestNG
- Feature-file test data
- Excel test data (`.xlsx` / `.xls` reader through Apache POI)
- Page Object Model
- Separate application-specific page/flow layers on top of one reusable framework
- Parallel execution
- Retry logic
- Failure screenshots
- Log4j2 execution logs
- Allure results/reporting
- Detailed PDF execution report with step and assertion evidence
- Controlled self-healing with declared fallback locators
- Environment-variable / CI-secret handling for sensitive credentials

## Important folders

```text
src/main/java/com/hybrid/framework/                 reusable framework code
src/test/java/com/hybrid/tests/                     Practice Test Automation sample
src/test/java/com/hybrid/tests/orangehrm/           OrangeHRM sample
src/test/resources/features/                        normal feature files
src/test/resources/secure-features/orangehrm/       OrangeHRM feature without credentials
docs/SECURITY.md                                    secret-handling guidance
```

The important separation is:

```text
Reusable framework
    UiDriver / Selenium / Playwright / retry / logging / reports / healing
                         |
                         +---- Practice Test Automation pages + flows
                         |
                         +---- OrangeHRM pages + flows
```

That is intentional. A new website adds its own page objects and business flows without rewriting the core driver/reporting infrastructure.

## Prerequisites

- JDK 8+
- Maven 3.9+
- Chrome / Edge / Firefox installed for Selenium runs
- Playwright browser binaries installed before Playwright runs

Check your local setup:

```bash
java -version
mvn -version
```

## First run: Practice Test Automation

Use `headless=false` so you can watch the browser.

```powershell
mvn clean test -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

The normal suite is `testng.xml`.

## OrangeHRM secure sample

Target:

```text
https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
```

The OrangeHRM feature contains **no username or password value**. The flow reads these values at runtime:

```text
ORANGEHRM_USERNAME
ORANGEHRM_PASSWORD
```

Windows PowerShell:

```powershell
$env:ORANGEHRM_USERNAME="your-username"
$env:ORANGEHRM_PASSWORD="your-password"

mvn clean test -DsuiteXmlFile=orangehrm-testng.xml -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

macOS/Linux:

```bash
export ORANGEHRM_USERNAME="your-username"
export ORANGEHRM_PASSWORD="your-password"

mvn clean test \
  -DsuiteXmlFile=orangehrm-testng.xml \
  -Ddata.source=feature \
  -Dengine=selenium \
  -Dbrowser=chrome \
  -Dheadless=false \
  -Dparallel.mode=none
```

The secured suite is kept separate from `testng.xml`, so a fresh clone can still run the normal demo without OrangeHRM credentials.

For GitHub Actions, create repository secrets named:

```text
ORANGEHRM_USERNAME
ORANGEHRM_PASSWORD
```

Then manually select `orangehrm-testng.xml` from the workflow-dispatch inputs. The workflow exposes the secret values only to the test process.

See `docs/SECURITY.md` for the full security setup.

## Why credentials are not passed with `-Dpassword=...`

A password passed on the command line can end up in shell history, process listings, CI logs or copied command examples. Environment variables / CI secret stores are a better default for test credentials.

The framework also masks password values in normal logging and in PDF evidence by default.

Do **not** enable:

```text
-Dreport.showSensitiveData=true
```

when real credentials are being used.

## Playwright setup and run

Install the Playwright browser binaries once:

```bash
mvn -q -DskipTests compile
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

Then run the normal sample:

```bash
mvn clean test \
  -Ddata.source=feature \
  -Dengine=playwright \
  -Dbrowser=chromium \
  -Dheadless=false \
  -Dparallel.mode=none
```

The OrangeHRM secure suite can use Playwright as well:

```bash
mvn clean test \
  -DsuiteXmlFile=orangehrm-testng.xml \
  -Ddata.source=feature \
  -Dengine=playwright \
  -Dbrowser=chromium \
  -Dheadless=false \
  -Dparallel.mode=none
```

## Run with Excel data

The workbook is:

`src/test/resources/testdata/login-data.xlsx`

Selenium:

```bash
mvn clean test -Ddata.source=excel -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

Playwright:

```bash
mvn clean test -Ddata.source=excel -Dengine=playwright -Dbrowser=chromium -Dheadless=false -Dparallel.mode=none
```

## Run both normal data sources

```bash
mvn clean test -Ddata.source=both -Dengine=selenium -Dbrowser=chrome
```

Feature scenarios and Excel rows are separate test invocations while reusing the same Practice Test Automation page objects and `LoginScenarioExecutor`.

## Use another environment URL

The Practice Test Automation sample URL can be overridden:

```bash
mvn clean test -DbaseUrl="https://your-test-environment.example/login" ...
```

The OrangeHRM sample URL can be overridden separately:

```bash
-Dorangehrm.baseUrl="https://your-orangehrm-environment.example/auth/login"
```

Changing only a URL does **not** make arbitrary website locators compatible. Each application owns its page objects and business flow while the framework layer stays unchanged.

## Parallel execution

```bash
mvn clean test -Dparallel.mode=methods -Dthread.count=4 -Ddataprovider.thread.count=4
```

For a first visual run, use `-Dparallel.mode=none`.

## Retry

By default, transient browser/automation failures get one additional attempt. Assertion failures are not retried unless explicitly enabled.

```bash
mvn clean test -Dretry.count=2
mvn clean test -Dretry.count=1 -Dretry.assertions=true
mvn clean test -Dretry.count=0
```

## Self-healing

The framework does not invent locators or edit source code. It only tries fallback locators deliberately declared in the page object.

```bash
mvn clean test -Dself.healing.enabled=true
mvn clean test -Dself.healing.enabled=false
```

When a fallback is used, the event is written to logs and report evidence.

## Reports and evidence

After execution:

- `artifacts/logs/` — detailed Log4j2 logs
- `artifacts/screenshots/` — failure screenshots
- `artifacts/self-healing/` — healing evidence
- `target/allure-results/` — Allure raw results
- `target/reports/hybrid-automation-report.pdf` — detailed PDF report

The PDF includes:

- overall test status and duration
- engine, browser, thread and attempt number
- ordered business actions
- action PASS / FAIL status
- assertions routed through `ReportAssert`
- expected and actual values
- failure messages
- failure screenshots when available
- self-healing evidence
- retry / attempt history
- operating system and Java version

For OrangeHRM, a secured execution can read like:

```text
Step 1 | ACTION | PASSED | User opens the OrangeHRM login page
Step 2 | ACTION | PASSED | User enters the configured OrangeHRM username
Step 3 | ACTION | PASSED | User enters the OrangeHRM password as '<masked>'
Step 4 | ACTION | PASSED | User clicks the OrangeHRM Login button
Step 5 | ACTION | PASSED | User waits for the OrangeHRM dashboard to load
Step 6 | ASSERTION | PASSED | Verify OrangeHRM dashboard URL
Step 7 | ASSERTION | PASSED | Verify OrangeHRM dashboard heading
Step 8 | ASSERTION | PASSED | Verify OrangeHRM dashboard is visible
```

### Recording actions in future tests

```java
ReportEvidenceContext.action(
        "User clicks the Save button",
        new Runnable() {
            @Override
            public void run() {
                page.clickSave();
            }
        });
```

### Recording assertions in future tests

```java
ReportAssert.assertEquals(
        "Verify confirmation message",
        actualMessage,
        expectedMessage);
```

or:

```java
ReportAssert.assertTrue(
        "Verify dashboard is visible",
        dashboardVisible,
        "Dashboard visible = true",
        "Dashboard visible = " + dashboardVisible);
```

Generate Allure HTML:

```bash
mvn allure:report
```

Open Allure locally:

```bash
mvn allure:serve
```

## Useful runtime properties

| Property | Default |
|---|---|
| `suiteXmlFile` | `testng.xml` |
| `engine` | `selenium` |
| `browser` | `chrome` |
| `data.source` | `feature` |
| `baseUrl` | Practice Test Automation login URL |
| `orangehrm.baseUrl` | OrangeHRM Open Source Demo login URL |
| `headless` | `true` |
| `parallel.mode` | `methods` |
| `thread.count` | `4` |
| `dataprovider.thread.count` | `4` |
| `retry.count` | `1` |
| `self.healing.enabled` | `true` |
| `report.showSensitiveData` | `false` |

## Security note

The Practice Test Automation project is a public training website whose demo credentials are intentionally published by that site. That is different from a real application credential.

For OrangeHRM and any real project, keep credentials outside the repository. Use environment variables locally and your CI/CD platform's secret store in pipelines.

## Java 8 compatibility

This repository targets JDK 1.8. Selenium is intentionally pinned to 4.13.0, the final Selenium release that supports Java 8. TestNG is pinned to 7.5.1 for the same reason. Newer Java syntax such as records, switch expressions, `String.isBlank()`, `List.of()` and `Stream.toList()` is not used in the source.
