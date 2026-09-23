package com.framework.utils;

import com.framework.base.BaseTest;
import com.microsoft.playwright.options.SelectOption;

public class CommonActions extends BaseTest {

    public void open(String url) {
        getPage().navigate(url);
    }

    public void click(String selector) {
        getPage().locator(selector).click();
    }

    public void sendText(String selector, String text) {
        getPage().locator(selector).fill(text);
    }

    public String getText(String selector) {
        return getPage().locator(selector).innerText();
    }

    public boolean isDisplayed(String selector) {
        return getPage().locator(selector).isVisible();
    }

    public void selectDropdownByVisibleText(String selector, String visibleText) {
        getPage().locator(selector).selectOption(new SelectOption().setLabel(visibleText));
    }

    public void selectDropdownByValue(String selector, String value) {
        getPage().locator(selector).selectOption(value);
    }

    public void selectRadioButton(String selector) {
        getPage().locator(selector).check();
    }

    public void selectCheckbox(String selector) {
        getPage().locator(selector).check();
    }

    public void hover(String selector) {
        getPage().locator(selector).hover();
    }

    public byte[] takeScreenshot() {
        return getPage().screenshot();
    }
}
