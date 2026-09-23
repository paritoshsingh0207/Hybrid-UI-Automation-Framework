package com.framework.hooks;

import com.framework.driver.DriverManager;
import com.framework.utils.CommonActions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    private final CommonActions actions = new CommonActions();

    @Before
    public void setUp() {
        DriverManager.startDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverManager.hasDriver()) {
                scenario.attach(actions.takeScreenshot(), "image/png", "Failure Screenshot");
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}
