package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test class with hardcoded test data as strings
 * Data is defined directly in the class for easy access and modification
 */
public class HardcodedDataTest extends BaseTest {

    // Hardcoded test data as string constants
    private static final String POST_DATA_1 = "{\n" +
            "  \"title\": \"Hardcoded Post Title\",\n" +
            "  \"body\": \"This is a hardcoded post from test class\",\n" +
            "  \"userId\": 1\n" +
            "}";

    private static final String POST_DATA_2 = "{\n" +
            "  \"title\": \"Another Hardcoded Post\",\n" +
            "  \"body\": \"Second hardcoded post for testing\",\n" +
            "  \"userId\": 2\n" +
            "}";

    private static final String COMMENT_DATA_1 = "{\n" +
            "  \"postId\": 1,\n" +
            "  \"name\": \"Test Comment\",\n" +
            "  \"email\": \"testcomment@example.com\",\n" +
            "  \"body\": \"This is a hardcoded comment\"\n" +
            "}";

    private static final String COMMENT_DATA_2 = "{\n" +
            "  \"postId\": 2,\n" +
            "  \"name\": \"Another Comment\",\n" +
            "  \"email\": \"another@example.com\",\n" +
            "  \"body\": \"Another hardcoded comment for testing\"\n" +
            "}";

    private static final String UPDATE_DATA = "{\n" +
            "  \"id\": 1,\n" +
            "  \"title\": \"Hardcoded Updated Title\",\n" +
            "  \"body\": \"This post was updated with hardcoded data\",\n" +
            "  \"userId\": 1\n" +
            "}";

    private static final String PATCH_DATA = "{\n" +
            "  \"title\": \"Hardcoded Patched Title\"\n" +
            "}";

    // Test methods using hardcoded data

    @Test(description = "Create post with first hardcoded data")
    public void testCreatePostWithFirstData() {
        logger.info("Testing: Create post with first hardcoded data");
        logger.info("Request body: " + POST_DATA_1);

        given()
                .contentType(ContentType.JSON)
                .body(POST_DATA_1)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Hardcoded Post Title"))
                .body("body", equalTo("This is a hardcoded post from test class"))
                .body("userId", equalTo(1));

        logger.info("Successfully created post with first hardcoded data");
    }

    @Test(description = "Create post with second hardcoded data")
    public void testCreatePostWithSecondData() {
        logger.info("Testing: Create post with second hardcoded data");
        logger.info("Request body: " + POST_DATA_2);

        given()
                .contentType(ContentType.JSON)
                .body(POST_DATA_2)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("title", equalTo("Another Hardcoded Post"))
                .body("body", equalTo("Second hardcoded post for testing"))
                .body("userId", equalTo(2));

        logger.info("Successfully created post with second hardcoded data");
    }

    @Test(description = "Create comment with first hardcoded data")
    public void testCreateCommentWithFirstData() {
        logger.info("Testing: Create comment with first hardcoded data");
        logger.info("Request body: " + COMMENT_DATA_1);

        given()
                .contentType(ContentType.JSON)
                .body(COMMENT_DATA_1)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .body("postId", equalTo(1))
                .body("name", equalTo("Test Comment"))
                .body("email", equalTo("testcomment@example.com"));

        logger.info("Successfully created comment with first hardcoded data");
    }

    @Test(description = "Create comment with second hardcoded data")
    public void testCreateCommentWithSecondData() {
        logger.info("Testing: Create comment with second hardcoded data");
        logger.info("Request body: " + COMMENT_DATA_2);

        given()
                .contentType(ContentType.JSON)
                .body(COMMENT_DATA_2)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .body("postId", equalTo(2))
                .body("name", equalTo("Another Comment"))
                .body("email", equalTo("another@example.com"));

        logger.info("Successfully created comment with second hardcoded data");
    }

    @Test(description = "Update post with hardcoded data using PUT")
    public void testUpdatePostWithHardcodedData() {
        logger.info("Testing: Update post with hardcoded data using PUT");
        logger.info("Request body: " + UPDATE_DATA);

        given()
                .contentType(ContentType.JSON)
                .body(UPDATE_DATA)
                .pathParam("id", 1)
                .when()
                .put("/posts/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo("Hardcoded Updated Title"))
                .body("body", equalTo("This post was updated with hardcoded data"));

        logger.info("Successfully updated post with hardcoded data");
    }

    @Test(description = "Patch post with hardcoded data")
    public void testPatchPostWithHardcodedData() {
        logger.info("Testing: Patch post with hardcoded data");
        logger.info("Request body: " + PATCH_DATA);

        given()
                .contentType(ContentType.JSON)
                .body(PATCH_DATA)
                .pathParam("id", 1)
                .when()
                .patch("/posts/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo("Hardcoded Patched Title"));

        logger.info("Successfully patched post with hardcoded data");
    }

    @Test(description = "Create post with inline hardcoded data")
    public void testCreatePostWithInlineData() {
        logger.info("Testing: Create post with inline hardcoded data");

        String inlinePostData = "{\n" +
                "  \"title\": \"Inline Hardcoded Post\",\n" +
                "  \"body\": \"This post is created with inline hardcoded data\",\n" +
                "  \"userId\": 3\n" +
                "}";

        logger.info("Request body: " + inlinePostData);

        given()
                .contentType(ContentType.JSON)
                .body(inlinePostData)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("title", equalTo("Inline Hardcoded Post"))
                .body("body", equalTo("This post is created with inline hardcoded data"))
                .body("userId", equalTo(3));

        logger.info("Successfully created post with inline hardcoded data");
    }
}
