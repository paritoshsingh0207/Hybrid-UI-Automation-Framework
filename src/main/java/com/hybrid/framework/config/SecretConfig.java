package com.hybrid.framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads sensitive values without putting them in source control.
 *
 * Local development can point the framework to a properties file that lives
 * outside the repository. CI can keep using environment variables / secret
 * stores. The secret value itself is never written to logs by this class.
 */
public final class SecretConfig {
    private static final Object FILE_LOCK = new Object();
    private static String cachedCredentialsFile;
    private static Properties cachedCredentials;

    private SecretConfig() {
    }

    /**
     * Reads a secret from the local credential file when -Dcredentials.file is
     * supplied. If no local file is configured, it falls back to the requested
     * environment variable. This keeps local runs convenient and CI secure.
     */
    public static String requiredSecret(String propertyName, String environmentVariableName) {
        String credentialsFile = System.getProperty("credentials.file");

        if (!isBlank(credentialsFile)) {
            return requiredCredentialFileProperty(credentialsFile.trim(), propertyName);
        }

        return requiredEnvironmentVariable(environmentVariableName);
    }

    public static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (isBlank(value)) {
            throw new IllegalStateException(
                    "Required secret environment variable '" + name + "' is not configured. "
                            + "Either provide -Dcredentials.file=<path-to-local-properties-file> "
                            + "or configure the environment variable / CI secret. "
                            + "Do not add the secret value to the repository.");
        }
        return value.trim();
    }

    public static boolean isEnvironmentVariableConfigured(String name) {
        return !isBlank(System.getenv(name));
    }

    public static boolean isCredentialFileConfigured() {
        return !isBlank(System.getProperty("credentials.file"));
    }

    private static String requiredCredentialFileProperty(String filePath, String propertyName) {
        Properties properties = credentialsFrom(filePath);
        String value = properties.getProperty(propertyName);

        if (isBlank(value)) {
            throw new IllegalStateException(
                    "Required credential property '" + propertyName + "' is missing or blank in local credential file: "
                            + new File(filePath).getAbsolutePath()
                            + ". Keep the credential file outside the repository and do not commit it.");
        }

        return value.trim();
    }

    private static Properties credentialsFrom(String filePath) {
        String absolutePath = new File(filePath).getAbsolutePath();

        synchronized (FILE_LOCK) {
            if (cachedCredentials != null && absolutePath.equals(cachedCredentialsFile)) {
                return cachedCredentials;
            }

            File file = new File(absolutePath);
            if (!file.isFile()) {
                throw new IllegalStateException(
                        "Credential file does not exist or is not a file: " + absolutePath
                                + ". Supply a valid path with -Dcredentials.file=<path>.");
            }

            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Unable to read local credential file: " + absolutePath,
                        exception);
            }

            cachedCredentialsFile = absolutePath;
            cachedCredentials = properties;
            return cachedCredentials;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
