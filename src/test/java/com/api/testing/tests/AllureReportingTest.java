package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class demonstrating Allure reporting features
 */
@Epic("API Testing")
@Feature("Allure Reporting")
public class AllureReportingTest extends BaseTest {

    @Test(description = "Demonstrate Allure annotations and reporting")
    @Story("Basic Allure Features")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test demonstrates various Allure annotations for enhanced reporting")
    @Link(name = "JSONPlaceholder API", url = "https://jsonplaceholder.typicode.com")
    public void testAllureBasicFeatures() {
        logger.info("Testing: Allure basic features");

        Allure.step("Step 1: Send GET request to fetch post", () -> {
            Response response = given()
                    .pathParam("id", 1)
                    .when()
                    .get("/posts/{id}")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            Allure.addAttachment("Response Body", "application/json", response.asString());
            logger.info("Response received: " + response.asString());
        });

        Allure.step("Step 2: Verify response data", () -> {
            given()
                    .pathParam("id", 1)
                    .when()
                    .get("/posts/{id}")
                    .then()
                    .body("id", equalTo(1))
                    .body("userId", equalTo(1));

            logger.info("Response data verified successfully");
        });

        Allure.step("Step 3: Complete test execution");
        logger.info("Allure basic features test completed");
    }

    @Test(description = "Test with multiple steps and attachments")
    @Story("Step-by-Step Execution")
    @Severity(SeverityLevel.NORMAL)
    @Owner("QA Team")
    public void testWithMultipleSteps() {
        logger.info("Testing: Multiple steps with attachments");

        Allure.step("Create a new post");
        String requestBody = "{\n" +
                "  \"title\": \"Allure Test Post\",\n" +
                "  \"body\": \"Testing Allure reporting\",\n" +
                "  \"userId\": 1\n" +
                "}";

        Allure.addAttachment("Request Payload", "application/json", requestBody);

        Response createResponse = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        Allure.addAttachment("Create Response", "application/json", createResponse.asString());

        Allure.step("Verify created post ID");
        Integer postId = createResponse.jsonPath().getInt("id");
        assertThat(postId, notNullValue());
        Allure.parameter("Created Post ID", postId);

        logger.info("Test with multiple steps completed");
    }

    @Test(description = "Test demonstrating environment information")
    @Story("Environment Configuration")
    @Severity(SeverityLevel.MINOR)
    public void testEnvironmentInfo() {
        logger.info("Testing: Environment information");

        Allure.step("Add environment details");
        Allure.parameter("Environment", getCurrentEnvironment());
        Allure.parameter("Base URI", BASE_URI);
        Allure.parameter("Java Version", System.getProperty("java.version"));
        Allure.parameter("OS", System.getProperty("os.name"));

        Allure.step("Execute API call");
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200);

        logger.info("Environment information test completed");
    }

    @Test(description = "Test with parameterized data")
    @Story("Parameterized Tests")
    @Severity(SeverityLevel.NORMAL)
    @Issue("TICKET-123")
    @TmsLink("TEST-456")
    public void testWithParameters() {
        logger.info("Testing: Parameterized test execution");

        int[] postIds = {1, 2, 3};

        for (int postId : postIds) {
            Allure.parameter("Post ID", postId);
            
            Allure.step("Fetch post with ID: " + postId, () -> {
                Response response = given()
                        .pathParam("id", postId)
                        .when()
                        .get("/posts/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

                assertThat(response.jsonPath().getInt("id"), equalTo(postId));
                logger.info("Successfully verified post ID: " + postId);
            });
        }

        logger.info("Parameterized test completed");
    }

    @Test(description = "Test demonstrating failure scenario", enabled = false)
    @Story("Failure Scenarios")
    @Severity(SeverityLevel.BLOCKER)
    public void testDemonstrateFailure() {
        logger.info("Testing: Failure scenario demonstration");

        Allure.step("This step will fail intentionally");
        
        given()
                .when()
                .get("/posts/999999")
                .then()
                .statusCode(200); // This will fail

        logger.info("This line won't be reached");
    }

    @Test(description = "Test with custom labels")
    @Story("Custom Metadata")
    @Severity(SeverityLevel.TRIVIAL)
    public void testWithCustomLabels() {
        logger.info("Testing: Custom labels and metadata");

        Allure.label("layer", "api");
        Allure.label("testType", "smoke");
        Allure.label("team", "backend");

        Allure.step("Execute test with custom labels");
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));

        logger.info("Custom labels test completed");
    }
}
