package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Sample GET Request Tests using RestAssured
 */
public class GetRequestTest extends BaseTest {

    @Test(description = "Get all posts")
    public void testGetAllPosts() {
        logger.info("Testing: Get all posts");

        Response response = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .extract()
                .response();

        logger.info("Response body: " + response.asString());
    }

    @Test(description = "Get post by ID")
    public void testGetPostById() {
        logger.info("Testing: Get post by ID");

        given()
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("title", notNullValue())
                .body("body", notNullValue())
                .body("userId", equalTo(1));

        logger.info("Successfully retrieved post with ID 1");
    }

    @Test(description = "Get posts by user ID")
    public void testGetPostsByUserId() {
        logger.info("Testing: Get posts by user ID");

        given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("userId", everyItem(equalTo(1)));

        logger.info("Successfully retrieved all posts for user ID 1");
    }

    @Test(description = "Get user by ID")
    public void testGetUserById() {
        logger.info("Testing: Get user by ID");

        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", notNullValue())
                .body("email", containsString("@"))
                .body("phone", notNullValue());

        logger.info("Successfully retrieved user with ID 1");
    }

    @Test(description = "Verify response headers")
    public void testResponseHeaders() {
        logger.info("Testing: Response headers verification");

        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .header("content-type", containsString("application/json"));

        logger.info("Response headers verified successfully");
    }
}
