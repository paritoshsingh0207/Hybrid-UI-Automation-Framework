package com.hybrid.tests.orangehrm.config;

import com.hybrid.framework.config.SecretConfig;

/**
 * Configuration owned by the OrangeHRM sample, not by the reusable framework.
 *
 * The URL is safe to keep in source. Credentials are deliberately different:
 * local runs can read them from a properties file outside the repository, and
 * CI can read them from environment variables backed by the CI secret store.
 */
public final class OrangeHrmConfig {
    private static final String DEFAULT_LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private OrangeHrmConfig() {
    }

    public static String loginUrl() {
        return System.getProperty("orangehrm.baseUrl", DEFAULT_LOGIN_URL);
    }

    public static String username() {
        return SecretConfig.requiredSecret("orangehrm.username", "ORANGEHRM_USERNAME");
    }

    public static String password() {
        return SecretConfig.requiredSecret("orangehrm.password", "ORANGEHRM_PASSWORD");
    }
}
