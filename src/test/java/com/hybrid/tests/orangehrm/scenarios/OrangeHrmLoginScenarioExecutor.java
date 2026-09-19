package com.hybrid.tests.orangehrm.scenarios;

import com.hybrid.framework.core.UiDriver;
import com.hybrid.framework.reporting.ReportAssert;
import com.hybrid.framework.reporting.ReportEvidenceContext;
import com.hybrid.tests.orangehrm.config.OrangeHrmConfig;
import com.hybrid.tests.orangehrm.pages.OrangeHrmDashboardPage;
import com.hybrid.tests.orangehrm.pages.OrangeHrmLoginPage;
import io.qameta.allure.Allure;

/**
 * Business flow for the OrangeHRM sample.
 *
 * Credentials are resolved only when the test runs. They are never constants,
 * feature-file values or committed configuration.
 */
public final class OrangeHrmLoginScenarioExecutor {
    private final UiDriver driver;
    private OrangeHrmLoginPage loginPage;

    public OrangeHrmLoginScenarioExecutor(UiDriver driver) {
        this.driver = driver;
    }

    public void openLoginPage() {
        final String url = OrangeHrmConfig.loginUrl();
        ReportEvidenceContext.action(
                "User opens the OrangeHRM login page: " + url,
                new Runnable() {
                    @Override
                    public void run() {
                        loginPage = new OrangeHrmLoginPage(driver).open(url);
                    }
                });
    }

    public void enterConfiguredUsername() {
        final String username = OrangeHrmConfig.username();
        Allure.parameter("OrangeHRM username source", "ORANGEHRM_USERNAME environment variable");
        ReportEvidenceContext.action(
                "User enters the configured OrangeHRM username",
                new Runnable() {
                    @Override
                    public void run() {
                        page().enterUsername(username);
                    }
                });
    }

    public void enterConfiguredPassword() {
        final String password = OrangeHrmConfig.password();
        Allure.parameter("OrangeHRM password source", "ORANGEHRM_PASSWORD environment variable");
        ReportEvidenceContext.action(
                "User enters the OrangeHRM password as '" + ReportEvidenceContext.sensitiveValue(password) + "'",
                new Runnable() {
                    @Override
                    public void run() {
                        page().enterPassword(password);
                    }
                });
    }

    public void submit() {
        ReportEvidenceContext.action(
                "User clicks the OrangeHRM Login button",
                new Runnable() {
                    @Override
                    public void run() {
                        page().clickLogin();
                    }
                });
    }

    public void verifyDashboard() {
        final OrangeHrmDashboardPage dashboard = new OrangeHrmDashboardPage(driver);

        ReportEvidenceContext.action(
                "User waits for the OrangeHRM dashboard to load",
                new Runnable() {
                    @Override
                    public void run() {
                        dashboard.waitUntilLoaded();
                    }
                });

        String currentUrl = dashboard.currentUrl();
        ReportAssert.assertTrue(
                "Verify OrangeHRM dashboard URL",
                currentUrl.contains("/dashboard/index"),
                "URL contains '/dashboard/index'",
                currentUrl);

        String heading = dashboard.headingText();
        ReportAssert.assertEquals(
                "Verify OrangeHRM dashboard heading",
                heading,
                "Dashboard");

        boolean visible = dashboard.isDashboardVisible();
        ReportAssert.assertTrue(
                "Verify OrangeHRM dashboard is visible",
                visible,
                "Dashboard visible = true",
                "Dashboard visible = " + visible);
    }

    private OrangeHrmLoginPage page() {
        if (loginPage == null) {
            throw new IllegalStateException(
                    "OrangeHRM login page has not been opened. Run the Given step first.");
        }
        return loginPage;
    }
}
