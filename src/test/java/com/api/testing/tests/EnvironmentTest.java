package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.utils.ConfigManager;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test to demonstrate environment configuration
 */
public class EnvironmentTest extends BaseTest {

    @Test(description = "Verify environment configuration is loaded")
    public void testEnvironmentConfig() {
        logger.info("Testing environment configuration");
        logger.info("Current Environment: " + getCurrentEnvironment());
        logger.info("Base URL: " + ConfigManager.getBaseUrl());
        logger.info("API Version: " + ConfigManager.getApiVersion());
        logger.info("Connection Timeout: " + ConfigManager.getConnectionTimeout());
        logger.info("Is Production: " + isProduction());
        logger.info("Is Staging: " + isStaging());
        logger.info("Is Development: " + isDevelopment());
    }

    @Test(description = "Test API call with environment configuration")
    public void testApiCallWithEnvironment() {
        logger.info("Testing API call in " + getCurrentEnvironment() + " environment");

        // This test will work in dev environment (jsonplaceholder)
        // For other environments, update the endpoint accordingly
        if (isDevelopment()) {
            given()
                    .when()
                    .get("/posts/1")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(1))
                    .body("userId", notNullValue());

            logger.info("API test successful in " + getCurrentEnvironment() + " environment");
        } else {
            logger.info("Skipping API test for non-dev environment");
        }
    }

    @Test(description = "Test with custom headers based on environment")
    public void testWithEnvironmentHeaders() {
        logger.info("Testing with environment-specific headers");

        RequestSpecification spec = getRequestSpec(
                "X-Environment", getCurrentEnvironment(),
                "X-API-Version", ConfigManager.getApiVersion()
        );

        if (isDevelopment()) {
            given()
                    .spec(spec)
                    .when()
                    .get("/users/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1));

            logger.info("Environment headers test successful");
        }
    }

    @Test(description = "Demonstrate authentication configuration")
    public void testAuthenticationConfig() {
        logger.info("Testing authentication configuration");
        logger.info("Username: " + ConfigManager.getUsername());
        logger.info("Auth Token: " + (ConfigManager.getAuthToken() != null ? "***configured***" : "not set"));
        
        // Note: This is just demonstrating the config, not making actual auth calls
        // since jsonplaceholder doesn't require authentication
    }
}
