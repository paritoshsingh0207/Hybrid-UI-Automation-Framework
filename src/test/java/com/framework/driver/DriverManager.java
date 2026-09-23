package com.framework.driver;

import com.framework.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<WebDriver>();

    private DriverManager() { }

    public static void startDriver() {
        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--start-maximized");
        DRIVER.set(new ChromeDriver(options));
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been started. Check Hooks.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
