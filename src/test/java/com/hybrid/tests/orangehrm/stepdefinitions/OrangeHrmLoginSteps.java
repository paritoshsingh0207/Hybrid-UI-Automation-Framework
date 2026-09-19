package com.hybrid.tests.orangehrm.stepdefinitions;

import com.hybrid.framework.core.DriverContext;
import com.hybrid.tests.orangehrm.scenarios.OrangeHrmLoginScenarioExecutor;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Cucumber glue for the secured OrangeHRM sample.
 *
 * The steps stay readable and thin; browser and locator details remain in the
 * scenario/page layers where they are easier to maintain.
 */
public final class OrangeHrmLoginSteps {
    private OrangeHrmLoginScenarioExecutor login;

    @Given("the user opens the OrangeHRM login page")
    public void openLoginPage() {
        login = new OrangeHrmLoginScenarioExecutor(DriverContext.get());
        login.openLoginPage();
    }

    @When("the user enters the configured OrangeHRM username")
    public void enterConfiguredUsername() {
        scenario().enterConfiguredUsername();
    }

    @When("the user enters the configured OrangeHRM password")
    public void enterConfiguredPassword() {
        scenario().enterConfiguredPassword();
    }

    @When("the user clicks the OrangeHRM Login button")
    public void clickLogin() {
        scenario().submit();
    }

    @Then("the OrangeHRM dashboard should be displayed")
    public void verifyDashboard() {
        scenario().verifyDashboard();
    }

    private OrangeHrmLoginScenarioExecutor scenario() {
        if (login == null) {
            throw new IllegalStateException(
                    "The OrangeHRM scenario was used before the Given step opened the page");
        }
        return login;
    }
}
