Feature: Practice login

  Scenario: Login with valid credentials
    Given I open the practice test login page
    When I login with username "student" and password "Password123"
    Then I should see the successful login page
