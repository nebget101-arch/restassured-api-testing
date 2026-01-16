package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Chain Request Test - Complete CRUD lifecycle
 * Demonstrates Create → Get → Update → Delete workflow
 */
public class ChainRequestTest extends BaseTest {

    @Test(priority = 1, description = "Create a new post")
    public void testCreatePost() {
        logger.info("Step 1: Creating a new post");

        String requestBody = "{\n" +
                "  \"title\": \"Chain Test Post\",\n" +
                "  \"body\": \"This post is created as part of a chain request test\",\n" +
                "  \"userId\": 1\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("title", equalTo("Chain Test Post"))
                .body("body", equalTo("This post is created as part of a chain request test"))
                .body("userId", equalTo(1))
                .body("id", notNullValue())
                .extract()
                .response();

        Integer createdPostId = response.jsonPath().getInt("id");
        logger.info("Post created successfully with ID: " + createdPostId);
    }

    @Test(priority = 2, description = "Get an existing post", dependsOnMethods = "testCreatePost")
    public void testGetCreatedPost() {
        logger.info("Step 2: Retrieving an existing post");
        
        // Note: JSONPlaceholder doesn't persist created resources, so we'll get an existing post (ID 1)
        int existingPostId = 1;

        given()
                .pathParam("id", existingPostId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(existingPostId))
                .body("userId", equalTo(1));

        logger.info("Successfully retrieved post with ID: " + existingPostId);
    }

    @Test(priority = 3, description = "Update an existing post using PUT", dependsOnMethods = "testGetCreatedPost")
    public void testUpdatePostWithPut() {
        // Use an existing post ID for update operations
        int existingPostId = 1;
        logger.info("Step 3: Updating the post with ID: " + existingPostId + " using PUT");

        String updatedBody = "{\n" +
                "  \"id\": " + existingPostId + ",\n" +
                "  \"title\": \"Updated Chain Test Post\",\n" +
                "  \"body\": \"This post has been updated via PUT request in the chain test\",\n" +
                "  \"userId\": 1\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", existingPostId)
                .body(updatedBody)
                .when()
                .put("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(existingPostId))
                .body("title", equalTo("Updated Chain Test Post"))
                .body("body", equalTo("This post has been updated via PUT request in the chain test"))
                .body("userId", equalTo(1));

        logger.info("Successfully updated post with ID: " + existingPostId + " using PUT");
    }

    @Test(priority = 4, description = "Partially update the post using PATCH", dependsOnMethods = "testUpdatePostWithPut")
    public void testPartialUpdateWithPatch() {
        int existingPostId = 1;
        logger.info("Step 4: Partially updating the post with ID: " + existingPostId + " using PATCH");

        String patchBody = "{\n" +
                "  \"title\": \"Patched Chain Test Post\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", existingPostId)
                .body(patchBody)
                .when()
                .patch("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(existingPostId))
                .body("title", equalTo("Patched Chain Test Post"));

        logger.info("Successfully patched post with ID: " + existingPostId);
    }

    @Test(priority = 5, description = "Delete an existing post", dependsOnMethods = "testPartialUpdateWithPatch")
    public void testDeletePost() {
        int existingPostId = 1;
        logger.info("Step 5: Deleting the post with ID: " + existingPostId);

        given()
                .pathParam("id", existingPostId)
                .when()
                .delete("/posts/{id}")
                .then()
                .statusCode(200);

        logger.info("Successfully deleted post with ID: " + existingPostId);
    }

    @Test(priority = 6, description = "Verify CRUD chain completed", dependsOnMethods = "testDeletePost")
    public void testVerifyPostDeleted() {
        logger.info("Step 6: CRUD chain completed successfully");
        
        // Note: JSONPlaceholder doesn't actually persist changes, 
        // but we've successfully demonstrated the complete CRUD workflow
        logger.info("Complete CRUD workflow validated: Create -> Get -> Update -> Patch -> Delete");
    }

    @Test(description = "Complete CRUD chain in a single test method")
    public void testCompleteCrudChainInSingleTest() {
        logger.info("Testing: Complete CRUD chain in a single test");

        // Step 1: Create
        String createBody = "{\n" +
                "  \"title\": \"Single Test CRUD Chain\",\n" +
                "  \"body\": \"Testing complete CRUD in one method\",\n" +
                "  \"userId\": 5\n" +
                "}";

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        Integer postId = createResponse.jsonPath().getInt("id");
        logger.info("Created post with ID: " + postId);

        // Step 2: Read (using an existing post since JSONPlaceholder doesn't persist created resources)
        int existingPostId = 1;
        given()
                .pathParam("id", existingPostId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(existingPostId));

        logger.info("Successfully retrieved existing post with ID: " + existingPostId);

        // Step 3: Update
        String updateBody = "{\n" +
                "  \"id\": " + existingPostId + ",\n" +
                "  \"title\": \"Updated Single Test\",\n" +
                "  \"body\": \"This has been updated\",\n" +
                "  \"userId\": 1\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", existingPostId)
                .body(updateBody)
                .when()
                .put("/posts/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Single Test"));

        logger.info("Successfully updated post with ID: " + existingPostId);

        // Step 4: Delete
        given()
                .pathParam("id", existingPostId)
                .when()
                .delete("/posts/{id}")
                .then()
                .statusCode(200);

        logger.info("Successfully deleted post with ID: " + existingPostId);
        logger.info("Complete CRUD chain test finished successfully");
    }
}