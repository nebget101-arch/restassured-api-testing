package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.utils.TestDataReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Sample DELETE Request Tests using RestAssured
 * Demonstrates Data-Driven Testing using TestNG @DataProvider with JSON data
 */
public class DeleteRequestTest extends BaseTest {

    /**
     * DataProvider that reads test data from JSON file
     * Dynamically generates test data sets from testdata.json
     */
    @DataProvider(name = "deleteTestDataFromJson")
    public Object[][] provideDeleteTestDataFromJson() {
        // Read the deleteTestData array from testdata.json
        JsonArray deleteDataArray = TestDataReader.getArrayByKey("deleteTestData");
        
        Object[][] testData = new Object[deleteDataArray.size()][3];
        
        // Convert each JSON object to test parameters
        for (int i = 0; i < deleteDataArray.size(); i++) {
            JsonObject dataObj = deleteDataArray.get(i).getAsJsonObject();
            testData[i][0] = dataObj.get("postId").getAsInt();           // postId
            testData[i][1] = dataObj.get("expectedStatus").getAsInt();   // expectedStatus
            testData[i][2] = dataObj.get("scenario").getAsString();      // scenario description
        }
        
        return testData;
    }

    /**
     * Parameterized test that reads data from JSON file
     * Runs once for each entry in deleteTestData array
     */
    @Test(description = "Delete posts using JSON data provider", dataProvider = "deleteTestDataFromJson")
    public void testDeletePostWithJsonData(int postId, int expectedStatus, String scenario) {
        logger.info("Testing: " + scenario);

        int actualStatus = given()
                .pathParam("id", postId)
                .when()
                .delete("/posts/{id}")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(actualStatus, expectedStatus, 
                           "Status code mismatch for scenario: " + scenario + " (Post ID: " + postId + ")");
        logger.info("Successfully deleted post with ID: " + postId + " - " + scenario);
    }

    /**
     * Alternative approach: Static DataProvider (original implementation)
     */
    @DataProvider(name = "postIds")
    public Object[][] providePostIds() {
        return new Object[][]{
                {1, "Delete post with ID 1"},
                {2, "Delete post with ID 2"},
                {5, "Delete post with ID 5"},
                {10, "Delete post with ID 10"}
        };
    }

    /**
     * Test using static hardcoded data
     */
    @Test(description = "Delete posts with different IDs", dataProvider = "postIds")
    public void testDeletePostDynamically(int postId, String description) {
        logger.info("Testing: " + description);

        int statusCode = given()
                .pathParam("id", postId)
                .when()
                .delete("/posts/{id}")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(statusCode, 200, "Status code should be 200 for post ID: " + postId);
        logger.info("Successfully deleted post with ID: " + postId);
    }
}
