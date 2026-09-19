package com.hybrid.tests.orangehrm.pages;

import com.hybrid.framework.core.UiDriver;
import com.hybrid.framework.core.UiLocator;
import com.hybrid.framework.enums.LocatorType;

/**
 * Page object for the OrangeHRM login screen.
 *
 * Nothing in this class calls Selenium or Playwright directly. That is the
 * point of the sample: the same page object runs through the common UiDriver.
 */
public final class OrangeHrmLoginPage {

    // OrangeHRM currently exposes stable name attributes for the two fields.
    // CSS/XPath alternatives are kept as controlled healing candidates.
    private static final UiLocator USERNAME = UiLocator.builder("OrangeHrmLoginPage.username")
            .primary(LocatorType.NAME, "username")
            .fallback(LocatorType.CSS, "input[name='username']")
            .fallback(LocatorType.XPATH, "//input[@name='username']")
            .build();

    private static final UiLocator PASSWORD = UiLocator.builder("OrangeHrmLoginPage.password")
            .primary(LocatorType.NAME, "password")
            .fallback(LocatorType.CSS, "input[name='password']")
            .fallback(LocatorType.XPATH, "//input[@name='password']")
            .build();

    private static final UiLocator LOGIN_BUTTON = UiLocator.builder("OrangeHrmLoginPage.loginButton")
            .primary(LocatorType.CSS, "button[type='submit']")
            .fallback(LocatorType.XPATH, "//button[@type='submit']")
            .fallbackRole("button", "Login")
            .build();

    private static final UiLocator LOGIN_HEADING = UiLocator.builder("OrangeHrmLoginPage.loginHeading")
            .primary(LocatorType.XPATH, "//h5[normalize-space()='Login']")
            .fallback(LocatorType.TEXT, "Login")
            .build();

    private final UiDriver driver;

    public OrangeHrmLoginPage(UiDriver driver) {
        this.driver = driver;
    }

    public OrangeHrmLoginPage open(String url) {
        driver.navigate(url);
        driver.waitForVisible(LOGIN_HEADING);
        driver.waitForVisible(USERNAME);
        return this;
    }

    public OrangeHrmLoginPage enterUsername(String username) {
        driver.fill(USERNAME, username);
        return this;
    }

    public OrangeHrmLoginPage enterPassword(String password) {
        driver.fill(PASSWORD, password);
        return this;
    }

    public void clickLogin() {
        driver.click(LOGIN_BUTTON);
    }
}
