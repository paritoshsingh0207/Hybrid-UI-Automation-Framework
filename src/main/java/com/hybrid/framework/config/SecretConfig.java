package com.hybrid.framework.config;

/**
 * Small helper for values that must never be committed to source control.
 *
 * Passwords, tokens and similar values are read from environment variables.
 * That keeps the same code usable on a developer laptop and in CI, while the
 * actual secret stays outside the repository.
 */
public final class SecretConfig {
    private SecretConfig() {
    }

    public static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Required secret environment variable '" + name + "' is not configured. "
                            + "Set it on the machine running the test or store it in the CI secret store; "
                            + "do not add the value to the repository.");
        }
        return value;
    }

    public static boolean isEnvironmentVariableConfigured(String name) {
        String value = System.getenv(name);
        return value != null && !value.trim().isEmpty();
    }
}
