package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import com.api.testing.models.Comment;
import com.api.testing.models.Post;
import com.api.testing.models.User;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test cases demonstrating Java Collections with POJO classes
 * Shows List, Set, Map operations and Java Streams with POJOs
 */
public class PojoCollectionsTest extends BaseTest {

    @Test(description = "Deserialize API response to List of POJOs")
    public void testDeserializeToList() {
        logger.info("Testing: Deserialize to List<Post>");

        // Using TypeRef for generic types
        List<Post> posts = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        logger.info("Retrieved " + posts.size() + " posts as List");
        assertThat(posts, hasSize(greaterThan(0)));
        assertThat(posts, everyItem(hasProperty("id")));
        assertThat(posts, everyItem(hasProperty("title")));

        // Access list elements
        Post firstPost = posts.get(0);
        logger.info("First post: ID=" + firstPost.getId() + ", Title=" + firstPost.getTitle());

        logger.info("Successfully deserialized to List<Post>");
    }

    @Test(description = "Filter POJOs using Java Streams")
    public void testFilterPostsUsingStreams() {
        logger.info("Testing: Filter posts using Java Streams");

        List<Post> allPosts = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Filter posts by userId using streams
        List<Post> userOnePosts = allPosts.stream()
                .filter(post -> post.getUserId() == 1)
                .collect(Collectors.toList());

        logger.info("Filtered " + userOnePosts.size() + " posts for userId=1");
        assertThat(userOnePosts, not(empty()));
        
        // Verify all filtered posts belong to user 1
        userOnePosts.forEach(post -> 
            assertThat(post.getUserId(), equalTo(1))
        );

        logger.info("Successfully filtered posts using streams");
    }

    @Test(description = "Sort POJOs using Comparator")
    public void testSortPostsUsingComparator() {
        logger.info("Testing: Sort posts using Comparator");

        List<Post> posts = given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Sort by ID in descending order
        List<Post> sortedPosts = posts.stream()
                .sorted(Comparator.comparing(Post::getId).reversed())
                .collect(Collectors.toList());

        logger.info("Sorted " + sortedPosts.size() + " posts by ID (descending)");
        
        // Verify sorting
        for (int i = 0; i < sortedPosts.size() - 1; i++) {
            assertThat(sortedPosts.get(i).getId(), greaterThan(sortedPosts.get(i + 1).getId()));
        }

        logger.info("First post ID: " + sortedPosts.get(0).getId());
        logger.info("Last post ID: " + sortedPosts.get(sortedPosts.size() - 1).getId());
        logger.info("Successfully sorted posts");
    }

    @Test(description = "Group POJOs by field using Map")
    public void testGroupPostsByUserId() {
        logger.info("Testing: Group posts by userId using Map");

        List<Post> posts = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Group posts by userId
        Map<Integer, List<Post>> postsByUser = posts.stream()
                .collect(Collectors.groupingBy(Post::getUserId));

        logger.info("Grouped posts into " + postsByUser.size() + " user groups");
        assertThat(postsByUser.size(), greaterThan(0));

        // Verify each group
        postsByUser.forEach((userId, userPosts) -> {
            logger.info("User " + userId + " has " + userPosts.size() + " posts");
            assertThat(userPosts, not(empty()));
            
            // Verify all posts in group belong to the same user
            userPosts.forEach(post -> 
                assertThat(post.getUserId(), equalTo(userId))
            );
        });

        logger.info("Successfully grouped posts by userId");
    }

    @Test(description = "Convert List to Set to remove duplicates")
    public void testConvertListToSet() {
        logger.info("Testing: Convert List to Set");

        List<User> users = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<User>>() {});

        // Extract unique usernames using Set
        Set<String> uniqueUsernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        logger.info("Found " + uniqueUsernames.size() + " unique usernames");
        assertThat(uniqueUsernames, hasSize(users.size()));

        // Extract unique email domains
        Set<String> emailDomains = users.stream()
                .map(user -> user.getEmail().split("@")[1])
                .collect(Collectors.toSet());

        logger.info("Found " + emailDomains.size() + " unique email domains");
        emailDomains.forEach(domain -> logger.info("Domain: " + domain));

        logger.info("Successfully converted List to Set");
    }

    @Test(description = "Use Map to create POJO lookup")
    public void testCreatePojoLookupMap() {
        logger.info("Testing: Create POJO lookup using Map");

        List<Post> posts = given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Create a Map with ID as key and Post as value
        Map<Integer, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));

        logger.info("Created lookup map with " + postMap.size() + " posts");

        // Quick lookup by ID
        if (postMap.containsKey(1)) {
            Post post = postMap.get(1);
            logger.info("Found post 1: " + post.getTitle());
            assertThat(post.getId(), equalTo(1));
        }

        // Check if specific ID exists
        assertThat(postMap.keySet(), hasItem(posts.get(0).getId()));

        logger.info("Successfully created POJO lookup map");
    }

    @Test(description = "Count and aggregate POJO data")
    public void testCountAndAggregateData() {
        logger.info("Testing: Count and aggregate POJO data");

        List<Comment> comments = given()
                .queryParam("postId", 1)
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Comment>>() {});

        // Count total comments
        long totalComments = comments.size();
        logger.info("Total comments: " + totalComments);

        // Count comments with specific email domain
        long gmailComments = comments.stream()
                .filter(comment -> comment.getEmail().contains("@"))
                .count();
        logger.info("Comments with email: " + gmailComments);

        // Get unique email addresses
        Set<String> uniqueEmails = comments.stream()
                .map(Comment::getEmail)
                .collect(Collectors.toSet());
        logger.info("Unique email addresses: " + uniqueEmails.size());

        assertThat(totalComments, greaterThan(0L));
        logger.info("Successfully counted and aggregated data");
    }

    @Test(description = "Find and match POJOs using predicates")
    public void testFindAndMatchPojos() {
        logger.info("Testing: Find and match POJOs");

        List<Post> posts = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Find first post with specific userId
        Optional<Post> firstUserPost = posts.stream()
                .filter(post -> post.getUserId() == 1)
                .findFirst();

        assertThat(firstUserPost.isPresent(), is(true));
        firstUserPost.ifPresent(post -> 
            logger.info("Found first post for user 1: " + post.getTitle())
        );

        // Check if any post has specific title
        boolean hasSpecificTitle = posts.stream()
                .anyMatch(post -> post.getTitle() != null && !post.getTitle().isEmpty());
        assertThat(hasSpecificTitle, is(true));

        // Check if all posts have userId
        boolean allHaveUserId = posts.stream()
                .allMatch(post -> post.getUserId() != null);
        assertThat(allHaveUserId, is(true));
        logger.info("All posts have userId: " + allHaveUserId);

        logger.info("Successfully found and matched POJOs");
    }

    @Test(description = "Transform and map POJOs to different types")
    public void testTransformPojos() {
        logger.info("Testing: Transform POJOs");

        List<User> users = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<User>>() {});

        // Extract just the names
        List<String> userNames = users.stream()
                .map(User::getName)
                .collect(Collectors.toList());

        logger.info("Extracted " + userNames.size() + " user names");
        userNames.forEach(name -> logger.info("User: " + name));

        // Create a map of username to email
        Map<String, String> usernameToEmail = users.stream()
                .collect(Collectors.toMap(User::getUsername, User::getEmail));

        logger.info("Created username to email mapping with " + usernameToEmail.size() + " entries");
        
        // Create summary strings
        List<String> userSummaries = users.stream()
                .map(user -> user.getName() + " (" + user.getEmail() + ")")
                .collect(Collectors.toList());

        logger.info("Created " + userSummaries.size() + " user summaries");
        assertThat(userSummaries, hasSize(users.size()));

        logger.info("Successfully transformed POJOs");
    }

    @Test(description = "Partition POJOs based on conditions")
    public void testPartitionPojos() {
        logger.info("Testing: Partition POJOs");

        List<Post> posts = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Partition posts by userId (user 1 vs others)
        Map<Boolean, List<Post>> partitioned = posts.stream()
                .collect(Collectors.partitioningBy(post -> post.getUserId() == 1));

        List<Post> userOnePosts = partitioned.get(true);
        List<Post> otherUserPosts = partitioned.get(false);

        logger.info("User 1 posts: " + userOnePosts.size());
        logger.info("Other users posts: " + otherUserPosts.size());

        assertThat(userOnePosts, not(empty()));
        assertThat(otherUserPosts, not(empty()));

        // Verify partition
        userOnePosts.forEach(post -> assertThat(post.getUserId(), equalTo(1)));
        otherUserPosts.forEach(post -> assertThat(post.getUserId(), not(equalTo(1))));

        logger.info("Successfully partitioned POJOs");
    }

    @Test(description = "Combine multiple collections of POJOs")
    public void testCombineCollections() {
        logger.info("Testing: Combine multiple collections");

        // Get posts from two different users
        List<Post> user1Posts = given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        List<Post> user2Posts = given()
                .queryParam("userId", 2)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Combine both lists
        List<Post> combinedPosts = new ArrayList<>();
        combinedPosts.addAll(user1Posts);
        combinedPosts.addAll(user2Posts);

        logger.info("Combined posts: " + combinedPosts.size());
        logger.info("User 1 posts: " + user1Posts.size());
        logger.info("User 2 posts: " + user2Posts.size());

        assertThat(combinedPosts.size(), equalTo(user1Posts.size() + user2Posts.size()));

        // Get unique user IDs
        Set<Integer> uniqueUserIds = combinedPosts.stream()
                .map(Post::getUserId)
                .collect(Collectors.toSet());
        
        logger.info("Unique user IDs in combined list: " + uniqueUserIds);
        assertThat(uniqueUserIds, hasItems(1, 2));

        logger.info("Successfully combined collections");
    }

    @Test(description = "Calculate statistics from POJO collections")
    public void testCalculateStatistics() {
        logger.info("Testing: Calculate statistics from POJOs");

        List<Post> posts = given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<Post>>() {});

        // Calculate average, min, max of post IDs
        IntSummaryStatistics stats = posts.stream()
                .mapToInt(Post::getId)
                .summaryStatistics();

        logger.info("Post ID Statistics:");
        logger.info("  Count: " + stats.getCount());
        logger.info("  Min: " + stats.getMin());
        logger.info("  Max: " + stats.getMax());
        logger.info("  Average: " + stats.getAverage());
        logger.info("  Sum: " + stats.getSum());

        assertThat(stats.getCount(), equalTo((long) posts.size()));
        assertThat(stats.getMin(), greaterThan(0));

        // Count posts per user
        Map<Integer, Long> postsPerUser = posts.stream()
                .collect(Collectors.groupingBy(Post::getUserId, Collectors.counting()));

        logger.info("Posts per user: " + postsPerUser);
        
        logger.info("Successfully calculated statistics");
    }
}
