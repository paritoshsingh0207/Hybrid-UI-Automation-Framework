package com.framework.hooks;

import com.framework.driver.BrowserManager;
import com.framework.utils.CommonActions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    private final CommonActions actions = new CommonActions();

    @Before
    public void setUp() {
        BrowserManager.startBrowser();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && BrowserManager.hasPage()) {
                scenario.attach(actions.takeScreenshot(), "image/png", "Failure Screenshot");
            }
        } finally {
            BrowserManager.closeBrowser();
        }
    }
}
