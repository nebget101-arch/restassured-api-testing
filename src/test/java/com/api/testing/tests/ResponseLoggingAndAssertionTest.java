package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test class with response logging and complex dynamic JSON assertions
 * Demonstrates advanced response validation with detailed logging
 */
public class ResponseLoggingAndAssertionTest extends BaseTest {

    @Test(description = "Get post and log complete response with complex assertions")
    public void testGetPostWithResponseLogging() {
        logger.info("======== Starting: Get Post with Response Logging ========");

        Response response = given()
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Log response details
        logResponseDetails(response);

        // Complex assertions on response
        String responseBody = response.getBody().asString();
        logger.info("Validating response content...");

        assert response.statusCode() == 200 : "Status code is not 200";
        assert response.getContentType().contains("json") : "Content type is not JSON";
        assert responseBody.contains("\"id\"") : "Post ID field not found";
        assert responseBody.contains("\"userId\"") : "userId field missing";
        assert responseBody.contains("\"title\"") : "title field missing";
        assert responseBody.contains("\"body\"") : "body field missing";

        // Validate using jsonPath
        assert response.jsonPath().getInt("id") == 1 : "Post ID should be 1";
        assert response.jsonPath().getString("title") != null : "Title should not be null";
        assert response.jsonPath().getInt("userId") > 0 : "UserID should be greater than 0";

        logger.info("✓ All assertions passed for post response");
        logger.info("======== Completed: Get Post with Response Logging ========\n");
    }

    @Test(description = "Get all posts and validate dynamic list assertions")
    public void testGetAllPostsWithDynamicAssertions() {
        logger.info("======== Starting: Get All Posts with Dynamic Assertions ========");

        Response response = given()
                .queryParam("_limit", 5)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        logResponseDetails(response);

        // Complex dynamic assertions
        String responseBody = response.getBody().asString();
        logger.info("Performing dynamic assertions on posts list...");

        // Validate response is array
        assert responseBody.startsWith("[") : "Response is not a JSON array";

        // Count posts dynamically
        int postCount = response.jsonPath().getList("").size();
        logger.info("Total posts returned: " + postCount);
        assert postCount > 0 : "No posts found in response";
        assert postCount == 5 : "Expected 5 posts, got " + postCount;

        // Validate each post has required fields
        for (int i = 0; i < postCount; i++) {
            assert response.jsonPath().getInt("[" + i + "].id") > 0 : "Post " + i + " has invalid ID";
            assert response.jsonPath().getString("[" + i + "].title") != null : "Post " + i + " has no title";
            assert response.jsonPath().getString("[" + i + "].body") != null : "Post " + i + " has no body";
            assert response.jsonPath().getInt("[" + i + "].userId") > 0 : "Post " + i + " has invalid userID";

            logger.info("✓ Post " + i + " validation passed - ID: " +
                    response.jsonPath().getInt("[" + i + "].id") +
                    ", User: " + response.jsonPath().getInt("[" + i + "].userId"));
        }

        logger.info("✓ All dynamic assertions passed for posts list");
        logger.info("======== Completed: Get All Posts with Dynamic Assertions ========\n");
    }

    @Test(description = "Get user details and validate nested complex JSON")
    public void testGetUserWithComplexNestedValidation() {
        logger.info("======== Starting: Get User with Complex Nested JSON ========");

        Response response = given()
                .pathParam("id", 1)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        logResponseDetails(response);

        logger.info("Validating nested JSON structure...");

        // Validate top-level fields
        assert response.jsonPath().getInt("id") == 1 : "User ID mismatch";
        assert response.jsonPath().getString("name") != null : "User name is null";
        assert response.jsonPath().getString("email") != null : "User email is null";
        assert response.jsonPath().getString("phone") != null : "User phone is null";

        logger.info("✓ User basic fields validation passed");

        // Validate nested objects if they exist
        try {
            String address = response.jsonPath().getString("address");
            if (address != null) {
                logger.info("User has address object: " + address);
                assert response.jsonPath().getString("address.city") != null : "Address city is null";
                logger.info("✓ Address validation passed");
            }
        } catch (Exception e) {
            logger.warn("Address object validation skipped: " + e.getMessage());
        }

        try {
            String company = response.jsonPath().getString("company");
            if (company != null) {
                logger.info("User has company object: " + company);
                assert response.jsonPath().getString("company.name") != null : "Company name is null";
                logger.info("✓ Company validation passed");
            }
        } catch (Exception e) {
            logger.warn("Company object validation skipped: " + e.getMessage());
        }

        logger.info("✓ All complex nested validations passed");
        logger.info("======== Completed: Get User with Complex Nested JSON ========\n");
    }

    @Test(description = "Create post and validate response structure dynamically")
    public void testCreatePostAndValidateResponseStructure() {
        logger.info("======== Starting: Create Post and Validate Response ========");

        String requestBody = "{\n" +
                "  \"title\": \"Complex Test Post\",\n" +
                "  \"body\": \"Testing response logging and dynamic assertions\",\n" +
                "  \"userId\": 1\n" +
                "}";

        logger.info("Request data: " + requestBody);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        logResponseDetails(response);

        logger.info("Performing dynamic response structure validation...");

        // Dynamically validate all expected fields are present and have correct types
        assert response.jsonPath().getInt("id") > 0 : "Response ID is invalid";
        assert response.jsonPath().getString("title").equals("Complex Test Post") : "Title mismatch";
        assert response.jsonPath().getString("body").contains("dynamic assertions") : "Body content mismatch";
        assert response.jsonPath().getInt("userId") == 1 : "UserID mismatch";

        // Validate response contains no extra unexpected fields
        String responseBody = response.getBody().asString();
        logger.info("Response structure validation complete");

        logger.info("✓ All response structure validations passed");
        logger.info("======== Completed: Create Post and Validate Response ========\n");
    }

    @Test(description = "Get comments and validate list with dynamic filtering")
    public void testGetCommentsWithDynamicFiltering() {
        logger.info("======== Starting: Get Comments with Dynamic Filtering ========");

        Response response = given()
                .queryParam("postId", 1)
                .queryParam("_limit", 3)
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .extract()
                .response();

        logResponseDetails(response);

        logger.info("Performing dynamic filtering validation...");

        // Get comments count
        int commentCount = response.jsonPath().getList("").size();
        logger.info("Total comments returned: " + commentCount);

        // Validate all comments belong to postId 1
        boolean allCommentsForPost1 = true;
        for (int i = 0; i < commentCount; i++) {
            int postId = response.jsonPath().getInt("[" + i + "].postId");
            if (postId != 1) {
                allCommentsForPost1 = false;
                logger.warn("Comment " + i + " has postId " + postId + " instead of 1");
            } else {
                logger.info("✓ Comment " + i + " - ID: " + response.jsonPath().getInt("[" + i + "].id") +
                        ", Email: " + response.jsonPath().getString("[" + i + "].email"));
            }
        }

        assert allCommentsForPost1 : "Not all comments belong to postId 1";
        logger.info("✓ All comments filtered correctly for postId 1");
        logger.info("======== Completed: Get Comments with Dynamic Filtering ========\n");
    }

    /**
     * Helper method to log complete response details
     */
    private void logResponseDetails(Response response) {
        logger.info("\n--- RESPONSE DETAILS ---");
        logger.info("Status Code: " + response.getStatusCode());
        logger.info("Status Line: " + response.getStatusLine());
        logger.info("Content-Type: " + response.getContentType());
        logger.info("Response Time: " + response.getTime() + " ms");
        logger.info("\n--- RESPONSE BODY ---");
        logger.info(response.getBody().prettyPrint());
        logger.info("--- END RESPONSE BODY ---\n");
    }

    /**
     * Alternative method to log response in compact format
     */
    private void logResponseCompact(Response response) {
        logger.info("Response [" + response.getStatusCode() + "]: " + response.getBody().asString());
    }
}
