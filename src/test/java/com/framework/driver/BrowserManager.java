package com.framework.driver;

import com.framework.config.ConfigReader;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public final class BrowserManager {
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<Playwright>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<Browser>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<BrowserContext>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<Page>();

    private BrowserManager() { }

    public static void startBrowser() {
        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);

        BrowserType browserType;
        String browserName = ConfigReader.get("browser").toLowerCase();
        if ("firefox".equals(browserName)) {
            browserType = playwright.firefox();
        } else if ("webkit".equals(browserName)) {
            browserType = playwright.webkit();
        } else {
            browserType = playwright.chromium();
        }

        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));
        Browser browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(headless));
        BROWSER.set(browser);

        BrowserContext context = browser.newContext();
        CONTEXT.set(context);

        Page page = context.newPage();
        page.setDefaultTimeout(ConfigReader.getInt("explicitWaitSeconds") * 1000.0);
        PAGE.set(page);
    }

    public static boolean hasPage() {
        return PAGE.get() != null;
    }

    public static Page getPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("Playwright Page has not been started. Check Hooks.");
        }
        return page;
    }

    public static void closeBrowser() {
        BrowserContext context = CONTEXT.get();
        Browser browser = BROWSER.get();
        Playwright playwright = PLAYWRIGHT.get();

        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();

        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}
