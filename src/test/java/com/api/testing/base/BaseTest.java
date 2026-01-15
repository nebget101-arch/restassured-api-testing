package com.api.testing.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for API tests - provides common setup and utilities
 */
public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static final String BASE_URI = "https://jsonplaceholder.typicode.com";

    static {
        RestAssured.baseURI = BASE_URI;
    }

    /**
     * Get a basic request specification with common headers
     */
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
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
}
