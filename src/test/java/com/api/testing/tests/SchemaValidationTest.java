package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * JSON Schema Validation Tests using RestAssured
 * These tests validate API responses against predefined JSON schemas
 */
public class SchemaValidationTest extends BaseTest {

    @Test(description = "Validate single post response against JSON schema")
    public void testValidatePostSchema() {
        logger.info("Testing: Validate post schema");

        given()
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"));

        logger.info("Post schema validation successful");
    }

    @Test(description = "Validate list of posts response against JSON schema")
    public void testValidatePostsListSchema() {
        logger.info("Testing: Validate posts list schema");

        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/posts-list-schema.json"));

        logger.info("Posts list schema validation successful");
    }

    @Test(description = "Validate user response against JSON schema")
    public void testValidateUserSchema() {
        logger.info("Testing: Validate user schema");

        given()
                .pathParam("id", 1)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-schema.json"));

        logger.info("User schema validation successful");
    }

    @Test(description = "Validate comment response against JSON schema")
    public void testValidateCommentSchema() {
        logger.info("Testing: Validate comment schema");

        given()
                .pathParam("id", 1)
                .when()
                .get("/comments/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/comment-schema.json"));

        logger.info("Comment schema validation successful");
    }

    @Test(description = "Validate posts filtered by userId against JSON schema")
    public void testValidateFilteredPostsSchema() {
        logger.info("Testing: Validate filtered posts schema");

        given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/posts-list-schema.json"));

        logger.info("Filtered posts schema validation successful");
    }

    @Test(description = "Validate schema and perform additional assertions")
    public void testSchemaValidationWithAssertions() {
        logger.info("Testing: Schema validation with additional assertions");

        given()
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"))
                .body("id", equalTo(1))
                .body("userId", equalTo(1));

        logger.info("Schema validation with assertions successful");
    }

    @Test(description = "Validate created post response against schema")
    public void testValidateCreatedPostSchema() {
        logger.info("Testing: Validate created post schema");

        String requestBody = "{\n" +
                "  \"title\": \"Schema Validation Test\",\n" +
                "  \"body\": \"Testing schema validation for POST request\",\n" +
                "  \"userId\": 1\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"));

        logger.info("Created post schema validation successful");
    }
}
