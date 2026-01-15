package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.utils.TestDataReader;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Sample POST Request Tests using RestAssured
 */
public class PostRequestTest extends BaseTest {

    @Test(description = "Create a new post")
    public void testCreatePost() {
        logger.info("Testing: Create a new post");

        JsonObject postData = TestDataReader.getFirstFromArray("posts");
        String requestBody = TestDataReader.toJsonString(postData);

        logger.info("Request body: " + requestBody);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts");

        // Using TestNG Assertions
        Assert.assertEquals(response.statusCode(), 201, "Status code should be 201");
        Assert.assertNotNull(response.jsonPath().get("id"), "Post ID should not be null");
        Assert.assertEquals(response.jsonPath().getString("title"), 
                           postData.get("title").getAsString(), 
                           "Title should match request data");
        Assert.assertEquals(response.jsonPath().getString("body"), 
                           postData.get("body").getAsString(), 
                           "Body should match request data");
        Assert.assertEquals(response.jsonPath().getInt("userId"), 
                           postData.get("userId").getAsInt(), 
                           "User ID should match request data");

        logger.info("Successfully created a new post");
    }

    @Test(description = "Create a comment on a post")
    public void testCreateComment() {
        logger.info("Testing: Create a comment on a post");

        JsonObject commentData = TestDataReader.getFirstFromArray("comments");
        String commentBody = TestDataReader.toJsonString(commentData);

        logger.info("Request body: " + commentBody);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(commentBody)
                .when()
                .post("/comments");

        // Using TestNG Assertions
        Assert.assertEquals(response.statusCode(), 201, "Status code should be 201");
        Assert.assertEquals(response.jsonPath().getInt("postId"), 
                           commentData.get("postId").getAsInt(), 
                           "Post ID should match request data");
        Assert.assertEquals(response.jsonPath().getString("name"), 
                           commentData.get("name").getAsString(), 
                           "Name should match request data");
        Assert.assertEquals(response.jsonPath().getString("email"), 
                           commentData.get("email").getAsString(), 
                           "Email should match request data");

        logger.info("Successfully created a comment");
    }
}
