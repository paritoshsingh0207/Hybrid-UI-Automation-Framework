package com.hybrid.tests.orangehrm.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Dedicated runner for the secured OrangeHRM sample.
 *
 * Keeping it separate means the normal sample suite does not suddenly require
 * credentials just because this second application exists in the repository.
 */
@CucumberOptions(
        features = "src/test/resources/secure-features/orangehrm",
        glue = {
                "com.hybrid.tests.orangehrm.stepdefinitions",
                "com.hybrid.tests.hooks"
        },
        tags = "@orangehrm",
        plugin = {
                "pretty",
                "summary",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true)
public final class OrangeHrmCucumberTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
