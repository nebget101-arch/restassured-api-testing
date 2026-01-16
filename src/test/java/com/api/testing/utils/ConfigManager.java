package com.api.testing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for handling different environments
 * Supports dev, staging, and prod environments
 */
public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static Properties properties;
    private static String currentEnvironment;

    static {
        loadProperties();
    }

    /**
     * Load properties from config.properties file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("Unable to find config.properties");
                throw new RuntimeException("config.properties file not found");
            }
            properties.load(input);
            
            // Get environment from system property or default to 'dev'
            currentEnvironment = System.getProperty("env", "dev").toLowerCase();
            logger.info("Loaded configuration for environment: " + currentEnvironment);
            
        } catch (IOException e) {
            logger.error("Error loading config.properties", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Get current environment (dev, staging, or prod)
     */
    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Get base URL for current environment
     */
    public static String getBaseUrl() {
        String key = "base.url." + currentEnvironment;
        String url = properties.getProperty(key);
        if (url == null) {
            logger.warn("Base URL not found for environment: " + currentEnvironment + ", using dev");
            url = properties.getProperty("base.url.dev");
        }
        logger.debug("Using base URL: " + url);
        return url;
    }

    /**
     * Get property value by key
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property with default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get environment-specific property
     * Example: getEnvProperty("auth.username") returns "auth.username.dev" value for dev environment
     */
    public static String getEnvProperty(String baseKey) {
        String key = baseKey + "." + currentEnvironment;
        return properties.getProperty(key);
    }

    /**
     * Get username for current environment
     */
    public static String getUsername() {
        return getEnvProperty("auth.username");
    }

    /**
     * Get password for current environment
     */
    public static String getPassword() {
        return getEnvProperty("auth.password");
    }

    /**
     * Get auth token for current environment
     */
    public static String getAuthToken() {
        return getEnvProperty("auth.token");
    }

    /**
     * Get connection timeout
     */
    public static int getConnectionTimeout() {
        return Integer.parseInt(getProperty("connection.timeout", "10000"));
    }

    /**
     * Get request timeout
     */
    public static int getRequestTimeout() {
        return Integer.parseInt(getProperty("request.timeout", "30000"));
    }

    /**
     * Get API version
     */
    public static String getApiVersion() {
        return getProperty("api.version", "v1");
    }

    /**
     * Check if logging is enabled
     */
    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("enable.logging", "true"));
    }

    /**
     * Get retry count
     */
    public static int getRetryCount() {
        return Integer.parseInt(getProperty("retry.count", "3"));
    }

    /**
     * Set environment programmatically (useful for testing)
     */
    public static void setEnvironment(String env) {
        currentEnvironment = env.toLowerCase();
        logger.info("Environment changed to: " + currentEnvironment);
    }

    /**
     * Reload properties (useful when config changes)
     */
    public static void reload() {
        loadProperties();
    }
}
