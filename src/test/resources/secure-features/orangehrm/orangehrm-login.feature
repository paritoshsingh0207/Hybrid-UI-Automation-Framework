@orangehrm @secure-sample
Feature: OrangeHRM login with credentials supplied outside source control
  The repository contains the test flow, but it does not contain the username or password.
  Credentials are read from ORANGEHRM_USERNAME and ORANGEHRM_PASSWORD at runtime.

  Scenario: Authorized user can reach the OrangeHRM dashboard
    Given the user opens the OrangeHRM login page
    When the user enters the configured OrangeHRM username
    And the user enters the configured OrangeHRM password
    And the user clicks the OrangeHRM Login button
    Then the OrangeHRM dashboard should be displayed
