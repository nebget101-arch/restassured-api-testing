package com.api.testing.base;

import com.api.testing.utils.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

import java.util.concurrent.TimeUnit;

/**
 * Base class for API tests - provides common setup and utilities
 * Supports multiple environments via ConfigManager
 */
public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static String BASE_URI;
    protected static String ENVIRONMENT;

    @BeforeSuite
    public void setupEnvironment() {
        // Load configuration from ConfigManager
        BASE_URI = ConfigManager.getBaseUrl();
        ENVIRONMENT = ConfigManager.getCurrentEnvironment();
        
        // Configure RestAssured with environment settings
        RestAssured.baseURI = BASE_URI;
        RestAssured.useRelaxedHTTPSValidation();
        
        // Set timeouts
        RestAssured.config = RestAssured.config()
                .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigManager.getConnectionTimeout())
                        .setParam("http.socket.timeout", ConfigManager.getRequestTimeout()));
        
        logger.info("========================================");
        logger.info("Environment: " + ENVIRONMENT);
        logger.info("Base URI: " + BASE_URI);
        logger.info("API Version: " + ConfigManager.getApiVersion());
        logger.info("========================================");
    }

    /**
     * Get a basic request specification with common headers
     */
    protected RequestSpecification getRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setContentType("application/json");
        builder.setAccept("application/json");
        
        // Add logging if enabled
        if (ConfigManager.isLoggingEnabled()) {
            builder.addFilter(io.restassured.filter.log.RequestLoggingFilter.logRequestTo(System.out));
            builder.addFilter(io.restassured.filter.log.ResponseLoggingFilter.logResponseTo(System.out));
        }
        
        return builder.build();
    }

    /**
     * Get a basic request specification with additional headers
     */
    protected RequestSpecification getRequestSpec(String... headers) {
        RequestSpecification spec = getRequestSpec();
        for (int i = 0; i < headers.length; i += 2) {
            spec.header(headers[i], headers[i + 1]);
        }
        return spec;
    }

    /**
     * Get request specification with authentication
     */
    protected RequestSpecification getAuthenticatedRequestSpec() {
        return getRequestSpec()
                .header("Authorization", "Bearer " + ConfigManager.getAuthToken());
    }

    /**
     * Get request specification with basic auth
     */
    protected RequestSpecification getBasicAuthRequestSpec() {
        return RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .auth()
                .basic(ConfigManager.getUsername(), ConfigManager.getPassword());
    }

    /**
     * Get current environment
     */
    protected String getCurrentEnvironment() {
        return ENVIRONMENT;
    }

    /**
     * Check if running in production
     */
    protected boolean isProduction() {
        return "prod".equalsIgnoreCase(ENVIRONMENT);
    }

    /**
     * Check if running in staging
     */
    protected boolean isStaging() {
        return "staging".equalsIgnoreCase(ENVIRONMENT);
    }

    /**
     * Check if running in development
     */
    protected boolean isDevelopment() {
        return "dev".equalsIgnoreCase(ENVIRONMENT);
    }
}
