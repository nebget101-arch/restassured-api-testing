package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.utils.TestDataReader;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Sample PUT/PATCH Request Tests using RestAssured
 */
public class PutPatchRequestTest extends BaseTest {

    @Test(description = "Update a post using PUT")
    public void testUpdatePostWithPut() {
        logger.info("Testing: Update a post using PUT");

        JsonObject updateData = TestDataReader.getFirstFromArray("updateData");
        String updatedBody = TestDataReader.toJsonString(updateData);

        logger.info("Request body: " + updatedBody);

        given()
                .contentType(ContentType.JSON)
                .body(updatedBody)
                .pathParam("id", 1)
                .when()
                .put("/posts/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo(updateData.get("title").getAsString()))
                .body("body", equalTo(updateData.get("body").getAsString()));

        logger.info("Successfully updated post using PUT");
    }

    @Test(description = "Partially update a post using PATCH")
    public void testUpdatePostWithPatch() {
        logger.info("Testing: Partially update a post using PATCH");

        JsonObject patchData = TestDataReader.getFirstFromArray("patchData");
        String partialUpdate = TestDataReader.toJsonString(patchData);

        logger.info("Request body: " + partialUpdate);

        given()
                .contentType(ContentType.JSON)
                .body(partialUpdate)
                .pathParam("id", 1)
                .when()
                .patch("/posts/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo(patchData.get("title").getAsString()));

        logger.info("Successfully updated post using PATCH");
    }
}
