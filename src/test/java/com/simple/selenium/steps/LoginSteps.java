package com.simple.selenium.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LoginSteps {

    private WebDriver driver;

    @Before
    public void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    @Given("I open the practice test login page")
    public void openLoginPage() {
        driver.get("https://practicetestautomation.com/practice-test-login/");
    }

    @When("I login with username {string} and password {string}")
    public void login(String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("submit")).click();
    }

    @Then("I should see the successful login page")
    public void verifySuccessfulLogin() {
        Assert.assertTrue(
                "Expected successful login URL, but was: " + driver.getCurrentUrl(),
                driver.getCurrentUrl().contains("logged-in-successfully")
        );
    }

    @After
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
