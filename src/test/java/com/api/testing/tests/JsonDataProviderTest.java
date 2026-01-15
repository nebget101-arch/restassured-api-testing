package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.utils.JsonDataProvider;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Sample tests demonstrating JsonDataProvider usage
 * Alternative approach to reading test data from hardcoded JSON file
 */
public class JsonDataProviderTest extends BaseTest {

    @Test(description = "Create post using JsonDataProvider - first post data")
    public void testCreatePostWithFirstData() {
        logger.info("Testing: Create post using JsonDataProvider");

        // Get first post data using JsonDataProvider
        JsonObject postData = JsonDataProvider.getFirstObject("posts");
        String requestBody = postData.toString();

        logger.info("Post data: " + requestBody);
        logger.info("Post title: " + JsonDataProvider.getStringValue(postData, "title"));

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("title", equalTo(JsonDataProvider.getStringValue(postData, "title")))
                .body("userId", equalTo(JsonDataProvider.getIntValue(postData, "userId")));

        logger.info( "Post created successfully");
    }

    @Test(description = "Create comment using JsonDataProvider - last comment data")
    public void testCreateCommentWithLastData() {
        logger.info("Testing: Create comment using JsonDataProvider");

        // Get last comment data
        JsonObject commentData = JsonDataProvider.getLastObject("comments");
        String requestBody = commentData.toString();

        logger.info("Comment data: " + requestBody);
        logger.info("Total comments available: " + JsonDataProvider.getArraySize("comments"));

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .body("name", equalTo(JsonDataProvider.getStringValue(commentData, "name")))
                .body("email", equalTo(JsonDataProvider.getStringValue(commentData, "email")));

        logger.info("Comment created successfully");
    }

    @Test(description = "Verify JSON data availability")
    public void testJsonDataAvailability() {
        logger.info("Testing: JSON data availability");

        // Check if all required keys exist
        assert JsonDataProvider.hasKey("posts") : "Posts key not found";
        assert JsonDataProvider.hasKey("comments") : "Comments key not found";
        assert JsonDataProvider.hasKey("updateData") : "Update data key not found";
        assert JsonDataProvider.hasKey("patchData") : "Patch data key not found";

        logger.info("All required data keys are available");
    }

    @Test(description = "Get specific object by index from array")
    public void testGetObjectByIndex() {
        logger.info("Testing: Get specific object by index");

        // Get second post (index 1)
        JsonObject secondPost = JsonDataProvider.getObjectFromArray("posts", 1);
        logger.info("Second post title: " + JsonDataProvider.getStringValue(secondPost, "title"));

        assert JsonDataProvider.getStringValue(secondPost, "title").equals("Another Test Post");

        logger.info("Successfully retrieved object by index");
    }

    @Test(description = "Demonstrate full data access")
    public void testFullDataAccess() {
        logger.info("Testing: Full data access");

        // Get entire data object
        var allData = JsonDataProvider.getFullData();
        assert allData.has("posts") : "Posts data missing";
        assert allData.has("comments") : "Comments data missing";

        logger.info("Full data object retrieved successfully");
    }
}
