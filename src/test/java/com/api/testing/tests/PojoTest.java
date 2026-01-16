package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.models.Comment;
import com.api.testing.models.Post;
import com.api.testing.models.User;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test cases demonstrating POJO usage with REST Assured
 * Shows serialization (Object to JSON) and deserialization (JSON to Object)
 */
public class PojoTest extends BaseTest {

    @Test(description = "Deserialize single post response to POJO")
    public void testDeserializePostToPojo() {
        logger.info("Testing: Deserialize single post to POJO");

        Post post = given()
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Post.class);

        // Verify using POJO getters
        logger.info("Post object: " + post);
        assertThat(post.getId(), equalTo(1));
        assertThat(post.getUserId(), equalTo(1));
        assertThat(post.getTitle(), notNullValue());
        assertThat(post.getBody(), notNullValue());

        logger.info("Successfully deserialized post: ID=" + post.getId() + ", Title=" + post.getTitle());
    }

    @Test(description = "Deserialize list of posts to POJO array")
    public void testDeserializePostsListToPojo() {
        logger.info("Testing: Deserialize posts list to POJO array");

        Post[] posts = given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Post[].class);

        logger.info("Retrieved " + posts.length + " posts");
        assertThat(posts.length, greaterThan(0));

        // Verify all posts belong to userId 1
        for (Post post : posts) {
            assertThat(post.getUserId(), equalTo(1));
            logger.debug("Post ID: " + post.getId() + ", Title: " + post.getTitle());
        }

        logger.info("Successfully deserialized " + posts.length + " posts");
    }

    @Test(description = "Serialize POJO to JSON and create a post")
    public void testSerializePojoToCreatePost() {
        logger.info("Testing: Serialize POJO to create a post");

        // Create a Post object
        Post newPost = new Post(1, "POJO Test Post", "This post was created using a POJO class");

        // Serialize POJO to JSON and send as request body
        Post createdPost = given()
                .contentType(ContentType.JSON)
                .body(newPost)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .as(Post.class);

        // Verify the created post
        logger.info("Created post: " + createdPost);
        assertThat(createdPost.getId(), notNullValue());
        assertThat(createdPost.getUserId(), equalTo(1));
        assertThat(createdPost.getTitle(), equalTo("POJO Test Post"));
        assertThat(createdPost.getBody(), equalTo("This post was created using a POJO class"));

        logger.info("Successfully created post with ID: " + createdPost.getId());
    }

    @Test(description = "Update post using POJO serialization")
    public void testUpdatePostUsingPojo() {
        logger.info("Testing: Update post using POJO");

        // Create updated Post object
        Post updatedPost = new Post(1, 1, "Updated POJO Post", "This post was updated using POJO");

        // Send PUT request with POJO
        Post responsePost = given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(updatedPost)
                .when()
                .put("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Post.class);

        logger.info("Updated post: " + responsePost);
        assertThat(responsePost.getId(), equalTo(1));
        assertThat(responsePost.getTitle(), equalTo("Updated POJO Post"));

        logger.info("Successfully updated post using POJO");
    }

    @Test(description = "Deserialize user with nested objects to POJO")
    public void testDeserializeUserToPojo() {
        logger.info("Testing: Deserialize user with nested objects to POJO");

        User user = given()
                .pathParam("id", 1)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(User.class);

        // Verify user details
        logger.info("User object: " + user);
        assertThat(user.getId(), equalTo(1));
        assertThat(user.getName(), notNullValue());
        assertThat(user.getEmail(), containsString("@"));

        // Verify nested Address object
        assertThat(user.getAddress(), notNullValue());
        assertThat(user.getAddress().getCity(), notNullValue());
        assertThat(user.getAddress().getGeo(), notNullValue());
        assertThat(user.getAddress().getGeo().getLat(), notNullValue());

        // Verify nested Company object
        assertThat(user.getCompany(), notNullValue());
        assertThat(user.getCompany().getName(), notNullValue());

        logger.info("User: " + user.getName() + ", City: " + user.getAddress().getCity() + 
                   ", Company: " + user.getCompany().getName());
        logger.info("Successfully deserialized user with nested objects");
    }

    @Test(description = "Deserialize comment to POJO")
    public void testDeserializeCommentToPojo() {
        logger.info("Testing: Deserialize comment to POJO");

        Comment comment = given()
                .pathParam("id", 1)
                .when()
                .get("/comments/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Comment.class);

        logger.info("Comment object: " + comment);
        assertThat(comment.getId(), equalTo(1));
        assertThat(comment.getPostId(), notNullValue());
        assertThat(comment.getName(), notNullValue());
        assertThat(comment.getEmail(), containsString("@"));
        assertThat(comment.getBody(), notNullValue());

        logger.info("Comment by: " + comment.getEmail() + " on post: " + comment.getPostId());
        logger.info("Successfully deserialized comment");
    }

    @Test(description = "Create comment using POJO serialization")
    public void testCreateCommentUsingPojo() {
        logger.info("Testing: Create comment using POJO");

        Comment newComment = new Comment(1, "POJO Test Comment", 
                                        "test@example.com", 
                                        "This comment was created using a POJO class");

        Comment createdComment = given()
                .contentType(ContentType.JSON)
                .body(newComment)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .as(Comment.class);

        logger.info("Created comment: " + createdComment);
        assertThat(createdComment.getId(), notNullValue());
        assertThat(createdComment.getPostId(), equalTo(1));
        assertThat(createdComment.getName(), equalTo("POJO Test Comment"));
        assertThat(createdComment.getEmail(), equalTo("test@example.com"));

        logger.info("Successfully created comment with ID: " + createdComment.getId());
    }

    @Test(description = "Extract and validate specific fields from POJO")
    public void testExtractSpecificFieldsFromPojo() {
        logger.info("Testing: Extract specific fields from POJO");

        Post post = given()
                .pathParam("id", 5)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        // Extract and work with specific fields
        Integer postId = post.getId();
        String title = post.getTitle();
        String body = post.getBody();

        logger.info("Extracted - ID: " + postId + ", Title: " + title);
        
        assertThat(postId, equalTo(5));
        assertThat(title, not(emptyString()));
        assertThat(body, not(emptyString()));

        logger.info("Successfully extracted and validated specific fields");
    }

    @Test(description = "Demonstrate POJO advantages over string-based assertions")
    public void testPojoAdvantages() {
        logger.info("Testing: POJO advantages demonstration");

        Post post = given()
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        // With POJO, you get:
        // 1. Type safety
        // 2. IDE auto-completion
        // 3. Easy refactoring
        // 4. Better code readability
        // 5. Reusability across tests

        // Type-safe assertions
        assertThat(post.getId(), instanceOf(Integer.class));
        assertThat(post.getTitle(), instanceOf(String.class));

        // Easy null checks
        if (post.getTitle() != null && !post.getTitle().isEmpty()) {
            logger.info("Post has title: " + post.getTitle());
        }

        // Easy object manipulation
        post.setTitle("Modified Title");
        assertThat(post.getTitle(), equalTo("Modified Title"));

        logger.info("POJO advantages demonstrated successfully");
    }
}
