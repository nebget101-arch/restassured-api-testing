package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.github.javafaker.Faker;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * API Tests using Faker library to generate random test data
 * Demonstrates creating multiple requests with random data
 */
public class FakerDataTest extends BaseTest {

    private static final Faker faker = new Faker();

    /**
     * Test that creates multiple posts with random data
     * Each execution generates different data
     */
    @Test(description = "Create posts with random Faker data")
    public void testCreatePostWithRandomData() {
        logger.info("Testing: Create posts with random Faker data");

        // Generate 3 posts with random data
        for (int i = 1; i <= 3; i++) {
            String title = faker.book().title();
            String body = faker.lorem().paragraphs(3).toString();
            int userId = faker.number().numberBetween(1, 10);

            JsonObject postData = new JsonObject();
            postData.addProperty("title", title);
            postData.addProperty("body", body);
            postData.addProperty("userId", userId);

            logger.info("Post " + i + " - Title: " + title + ", User ID: " + userId);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(postData.toString())
                    .when()
                    .post("/posts");

            Assert.assertEquals(response.statusCode(), 201, "Post creation failed for iteration " + i);
            Assert.assertNotNull(response.jsonPath().get("id"), "Post ID should not be null");
        }

        logger.info("Successfully created 3 posts with random data");
    }

    /**
     * Test that creates comments with random Faker data
     */
    @Test(description = "Create comments with random Faker data")
    public void testCreateCommentsWithRandomData() {
        logger.info("Testing: Create comments with random Faker data");

        // Generate 5 comments with random data
        for (int i = 1; i <= 5; i++) {
            String name = faker.name().name();
            String email = faker.internet().emailAddress();
            String body = faker.lorem().sentence();
            int postId = faker.number().numberBetween(1, 100);

            JsonObject commentData = new JsonObject();
            commentData.addProperty("postId", postId);
            commentData.addProperty("name", name);
            commentData.addProperty("email", email);
            commentData.addProperty("body", body);

            logger.info("Comment " + i + " - Name: " + name + ", Email: " + email);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(commentData.toString())
                    .when()
                    .post("/comments");

            Assert.assertEquals(response.statusCode(), 201, "Comment creation failed for iteration " + i);
            Assert.assertNotNull(response.jsonPath().get("id"), "Comment ID should not be null");
        }

        logger.info("Successfully created 5 comments with random data");
    }

    /**
     * DataProvider that generates random test data
     * Returns an array of different user data with random values
     */
    @DataProvider(name = "randomUserData")
    public Object[][] provideRandomUserData() {
        Object[][] userData = new Object[5][3];

        for (int i = 0; i < 5; i++) {
            userData[i][0] = faker.name().name();           // Random name
            userData[i][1] = faker.internet().emailAddress(); // Random email
            userData[i][2] = faker.number().numberBetween(1, 100); // Random ID
        }

        return userData;
    }

    /**
     * Parameterized test using DataProvider with random Faker data
     * Runs 5 times with different randomly generated data
     */
    @Test(description = "Create multiple users with random data from DataProvider", dataProvider = "randomUserData")
    public void testCreateUsersWithRandomDataProvider(String name, String email, int userId) {
        logger.info("Creating user with Name: " + name + ", Email: " + email + ", ID: " + userId);

        JsonObject userData = new JsonObject();
        userData.addProperty("name", name);
        userData.addProperty("email", email);
        userData.addProperty("id", userId);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userData.toString())
                .when()
                .post("/users");

        // For demonstration, we just check it was received (201 or similar)
        Assert.assertTrue(response.statusCode() >= 200 && response.statusCode() < 300, 
                         "User creation returned invalid status code: " + response.statusCode());
        logger.info("Successfully created user: " + name);
    }

    /**
     * Test that validates random data before sending
     */
    @Test(description = "Validate and use random data in requests")
    public void testValidateAndUseRandomData() {
        logger.info("Testing: Validate and use random data in requests");

        // Generate random user data
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().safeEmailAddress();
        String phone = faker.phoneNumber().cellPhone();
        String username = faker.name().username().replaceAll("[^a-zA-Z0-9]", "");

        logger.info("Generated User Data:");
        logger.info("  First Name: " + firstName);
        logger.info("  Last Name: " + lastName);
        logger.info("  Email: " + email);
        logger.info("  Phone: " + phone);
        logger.info("  Username: " + username);

        // Validate generated data
        Assert.assertNotNull(firstName, "First name should not be null");
        Assert.assertNotNull(lastName, "Last name should not be null");
        Assert.assertTrue(email.contains("@"), "Email should contain @");
        Assert.assertFalse(phone.isEmpty(), "Phone should not be empty");
        Assert.assertFalse(username.isEmpty(), "Username should not be empty");

        // Create request body with validated random data
        JsonObject userData = new JsonObject();
        userData.addProperty("firstName", firstName);
        userData.addProperty("lastName", lastName);
        userData.addProperty("email", email);
        userData.addProperty("phone", phone);
        userData.addProperty("username", username);

        logger.info("Sending request with validated random data");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userData.toString())
                .when()
                .post("/users");

        Assert.assertTrue(response.statusCode() >= 200 && response.statusCode() < 300);
        logger.info("Successfully processed request with random data");
    }

    /**
     * Test that demonstrates different Faker methods
     */
    @Test(description = "Demonstrate various Faker data generators")
    public void testFakerDataGenerators() {
        logger.info("Testing: Demonstrating various Faker data generators");

        // Person data
        logger.info("Person Data:");
        logger.info("  Full Name: " + faker.name().fullName());
        logger.info("  First Name: " + faker.name().firstName());
        logger.info("  Last Name: " + faker.name().lastName());

        // Internet data
        logger.info("Internet Data:");
        logger.info("  Email: " + faker.internet().emailAddress());
        logger.info("  Username: " + faker.name().username());
        logger.info("  URL: " + faker.internet().url());

        // Lorem/Text data
        logger.info("Text Data:");
        logger.info("  Word: " + faker.lorem().word());
        logger.info("  Sentence: " + faker.lorem().sentence());
        logger.info("  Paragraph: " + faker.lorem().paragraph());

        // Book/Movie data
        logger.info("Book Data:");
        logger.info("  Title: " + faker.book().title());
        logger.info("  Author: " + faker.book().author());

        // Number data
        logger.info("Number Data:");
        logger.info("  Random Number (1-100): " + faker.number().numberBetween(1, 100));
        logger.info("  Random Double: " + faker.number().randomDouble(2, 1, 100));

        // Date data
        logger.info("Date Data:");
        logger.info("  Birthday: " + faker.date().birthday());

        logger.info("Faker data generation completed successfully");
    }
}
