# Secret handling

The framework keeps passwords, tokens and similar values outside source control.

## Rule

Do not put real credentials in:

- Java constants
- Cucumber feature files
- Excel test data committed to Git
- `pom.xml`
- `testng.xml`
- GitHub workflow YAML
- README examples

The OrangeHRM sample reads credentials from these environment variables at runtime:

```text
ORANGEHRM_USERNAME
ORANGEHRM_PASSWORD
```

`SecretConfig` fails with a clear message when a required value is missing. It never prints the secret itself.

## Local Windows / PowerShell

Set the values only for the current terminal session:

```powershell
$env:ORANGEHRM_USERNAME="your-username"
$env:ORANGEHRM_PASSWORD="your-password"

mvn clean test -DsuiteXmlFile=orangehrm-testng.xml -Ddata.source=feature -Dengine=selenium -Dbrowser=chrome -Dheadless=false -Dparallel.mode=none
```

Closing that terminal removes the session-scoped values.

To clear them manually:

```powershell
Remove-Item Env:ORANGEHRM_USERNAME
Remove-Item Env:ORANGEHRM_PASSWORD
```

## macOS / Linux

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

Clear them with:

```bash
unset ORANGEHRM_USERNAME
unset ORANGEHRM_PASSWORD
```

## GitHub Actions

Store the values as repository secrets rather than writing them into YAML:

1. Open the repository on GitHub.
2. Go to **Settings -> Secrets and variables -> Actions**.
3. Choose **New repository secret**.
4. Add `ORANGEHRM_USERNAME`.
5. Add `ORANGEHRM_PASSWORD`.

The workflow can then expose them to the test process through `env:` without putting their values in the repository.

## Reports and logs

The framework already treats password locators as sensitive in logging, and PDF evidence masks sensitive values by default.

Do not enable:

```text
-Dreport.showSensitiveData=true
```

when real credentials are being used. That option exists only for controlled demo data where showing the exact value is intentional.

## Local secret files

`.env`, `secrets.properties` and `*.secrets.properties` are ignored by Git as an extra safety net. The framework itself uses operating-system environment variables, so a `.env` file is not required.

`.env.example` contains variable names only and is safe to keep in the repository.
