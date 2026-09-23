package com.framework.utils;

import com.framework.base.BaseTest;
import com.framework.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CommonActions extends BaseTest {

    private WebDriverWait wait() {
        return new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds"))
        );
    }

    public void open(String url) {
        getDriver().get(url);
    }

    public void click(By locator) {
        wait().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    public void sendText(By locator, String text) {
        WebElement element = wait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        return wait().until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    public boolean isDisplayed(By locator) {
        return wait().until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
    }

    public void selectDropdownByVisibleText(By locator, String visibleText) {
        WebElement element = wait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByVisibleText(visibleText);
    }

    public void selectDropdownByValue(By locator, String value) {
        WebElement element = wait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByValue(value);
    }

    public void selectRadioButton(By locator) {
        WebElement element = wait().until(ExpectedConditions.elementToBeClickable(locator));
        if (!element.isSelected()) {
            element.click();
        }
    }

    public void selectCheckbox(By locator) {
        WebElement element = wait().until(ExpectedConditions.elementToBeClickable(locator));
        if (!element.isSelected()) {
            element.click();
        }
    }

    public void hover(By locator) {
        WebElement element = wait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Actions(getDriver()).moveToElement(element).perform();
    }

    public byte[] takeScreenshot() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
