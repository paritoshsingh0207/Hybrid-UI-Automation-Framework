package com.simple.playwright.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class LoginSteps {

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @Before
    public void openBrowser() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
        );
        page = browser.newPage();
    }

    @Given("I open the practice test login page")
    public void openLoginPage() {
        page.navigate("https://practicetestautomation.com/practice-test-login/");
    }

    @When("I login with username {string} and password {string}")
    public void login(String username, String password) {
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("#submit").click();
        page.waitForURL("**/logged-in-successfully/**");
    }

    @Then("I should see the successful login page")
    public void verifySuccessfulLogin() {
        Assert.assertTrue(
                "Expected successful login URL, but was: " + page.url(),
                page.url().contains("logged-in-successfully")
        );
    }

    @After
    public void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
