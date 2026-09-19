package com.hybrid.tests.orangehrm.pages;

import com.hybrid.framework.core.UiDriver;
import com.hybrid.framework.core.UiLocator;
import com.hybrid.framework.enums.LocatorType;

/**
 * Small page object used only to prove that login reached the application.
 * We check both a dashboard element and the current URL instead of relying on
 * a redirect alone.
 */
public final class OrangeHrmDashboardPage {

    private static final UiLocator DASHBOARD_HEADING = UiLocator.builder("OrangeHrmDashboardPage.heading")
            .primary(LocatorType.XPATH, "//h6[normalize-space()='Dashboard']")
            .fallback(LocatorType.CSS, ".oxd-topbar-header-breadcrumb h6")
            .fallback(LocatorType.TEXT, "Dashboard")
            .build();

    private final UiDriver driver;

    public OrangeHrmDashboardPage(UiDriver driver) {
        this.driver = driver;
    }

    public OrangeHrmDashboardPage waitUntilLoaded() {
        driver.waitForVisible(DASHBOARD_HEADING);
        return this;
    }

    public boolean isDashboardVisible() {
        return driver.isVisible(DASHBOARD_HEADING);
    }

    public String headingText() {
        return driver.getText(DASHBOARD_HEADING).trim();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
