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

For OrangeHRM, the framework supports two secure runtime sources:

1. **Local credential properties file** for developer machines.
2. **Environment variables / CI secret store** when no local file is supplied.

The credential value itself is never committed to this repository.

## Recommended local setup

Create the credential file outside the cloned repository. Example Windows location:

```text
C:\AutomationSecrets\orangehrm-credentials.properties
```

Example file contents:

```properties
orangehrm.username=your-username
orangehrm.password=your-password
```

A safe, empty template is tracked as `credentials.example.properties`. Do not put real values into that tracked example file.

Run OrangeHRM with the local file:

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

The path can point anywhere readable by the account running Maven. The path itself is configuration; the secret values stay in the local file.

### Important behavior

If `-Dcredentials.file=...` is supplied, the framework treats that file as authoritative. It will fail clearly when:

- the file does not exist;
- the path points to something that is not a file;
- `orangehrm.username` is missing/blank; or
- `orangehrm.password` is missing/blank.

It does not silently fall back to another credential source after an explicitly supplied local file fails validation. This helps catch typos and prevents confusing executions.

## Environment-variable fallback

When `-Dcredentials.file` is not supplied, OrangeHRM reads:

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

Clear them with:

```powershell
Remove-Item Env:ORANGEHRM_USERNAME
Remove-Item Env:ORANGEHRM_PASSWORD
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

Clear them with:

```bash
unset ORANGEHRM_USERNAME
unset ORANGEHRM_PASSWORD
```

## Credential resolution order

```text
-Dcredentials.file supplied?
        |
        +-- yes --> read orangehrm.username / orangehrm.password from local file
        |
        +-- no  --> read ORANGEHRM_USERNAME / ORANGEHRM_PASSWORD from environment
```

This lets a developer keep a local file on their machine while the same code uses the CI/CD secret store in GitHub Actions.

## GitHub Actions

Do not upload a local credential file to GitHub Actions. Store the values as repository secrets:

1. Open the repository on GitHub.
2. Go to **Settings -> Secrets and variables -> Actions**.
3. Choose **New repository secret**.
4. Add `ORANGEHRM_USERNAME`.
5. Add `ORANGEHRM_PASSWORD`.

The existing workflow exposes those secret values as environment variables only to the test process. Because the workflow does not supply `-Dcredentials.file`, the framework automatically uses the environment-variable fallback.

## Git protection

The repository ignores common local secret files, including:

```text
.env
.env.*
secrets.properties
*.secrets.properties
credentials.properties
*-credentials.properties
*.credentials.properties
```

The recommended file name `orangehrm-credentials.properties` is therefore ignored if someone accidentally creates it inside the repository. Keeping the real file outside the repository is still preferred.

## Reports and logs

The framework treats password locators as sensitive in logging, and PDF evidence masks sensitive values by default.

Do not enable:

```text
-Dreport.showSensitiveData=true
```

when real credentials are being used. That option exists only for controlled demo data where showing the exact value is intentional.

A secure OrangeHRM report step should look like:

```text
User enters the OrangeHRM password as '<masked>'
```

not the real password.

## Security level of a local properties file

A local properties file prevents accidental source-control exposure, which is appropriate for this portfolio/demo framework. It is still plain-text storage on the machine.

For production enterprise environments, prefer a dedicated secret manager such as Azure Key Vault, AWS Secrets Manager or HashiCorp Vault. The framework's `SecretConfig` boundary is intentionally small so another provider can be added without changing page objects or test scenarios.
