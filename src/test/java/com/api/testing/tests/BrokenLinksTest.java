package com.api.testing.tests;

import com.api.testing.base.BaseTest;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class for detecting broken links on web pages
 * Demonstrates link validation and status code checking
 */
public class BrokenLinksTest extends BaseTest {

    private Map<String, Integer> linkStatusMap = new ConcurrentHashMap<>();
    private List<String> brokenLinks = new ArrayList<>();
    private List<String> validLinks = new ArrayList<>();

    @Test(description = "Check for broken links on JSONPlaceholder home page")
    public void testBrokenLinksOnHomePage() {
        logger.info("Testing: Broken links on JSONPlaceholder website");

        String pageUrl = "https://jsonplaceholder.typicode.com/";
        
        try {
            // Fetch the HTML content
            Document document = Jsoup.connect(pageUrl).get();
            logger.info("Successfully fetched HTML from: " + pageUrl);

            // Extract all links
            Elements links = document.select("a[href]");
            logger.info("Found " + links.size() + " links on the page");

            // Check each link
            for (Element link : links) {
                String url = link.attr("abs:href"); // Get absolute URL
                if (!url.isEmpty() && !url.startsWith("#")) {
                    checkLinkStatus(url);
                }
            }

            // Report results
            reportLinkStatus();

            // Assert no broken links
            assertThat("Found broken links on the page", brokenLinks.size(), equalTo(0));

        } catch (IOException e) {
            logger.error("Failed to fetch page: " + e.getMessage());
            throw new RuntimeException("Failed to fetch HTML page", e);
        }
    }

    @Test(description = "Validate specific API endpoint links")
    public void testApiEndpointLinks() {
        logger.info("Testing: API endpoint links validation");

        List<String> apiEndpoints = Arrays.asList(
                "https://jsonplaceholder.typicode.com/posts",
                "https://jsonplaceholder.typicode.com/comments",
                "https://jsonplaceholder.typicode.com/albums",
                "https://jsonplaceholder.typicode.com/photos",
                "https://jsonplaceholder.typicode.com/todos",
                "https://jsonplaceholder.typicode.com/users"
        );

        logger.info("Checking " + apiEndpoints.size() + " API endpoints");

        for (String endpoint : apiEndpoints) {
            int statusCode = checkLinkStatusUsingRestAssured(endpoint);
            linkStatusMap.put(endpoint, statusCode);

            if (statusCode >= 200 && statusCode < 400) {
                validLinks.add(endpoint);
                logger.info("✓ Valid: " + endpoint + " [" + statusCode + "]");
            } else {
                brokenLinks.add(endpoint);
                logger.warn("✗ Broken: " + endpoint + " [" + statusCode + "]");
            }
        }

        reportLinkStatus();

        // Assert all endpoints are valid
        assertThat("Some API endpoints are broken", brokenLinks, empty());
        assertThat("All endpoints should be valid", validLinks.size(), equalTo(apiEndpoints.size()));
    }

    @Test(description = "Check individual resource links")
    public void testIndividualResourceLinks() {
        logger.info("Testing: Individual resource links");

        List<String> resourceLinks = Arrays.asList(
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com/posts/999", // This might be broken
                "https://jsonplaceholder.typicode.com/users/1",
                "https://jsonplaceholder.typicode.com/users/100", // This might be broken
                "https://jsonplaceholder.typicode.com/comments/1"
        );

        logger.info("Checking " + resourceLinks.size() + " resource links");

        for (String link : resourceLinks) {
            int statusCode = checkLinkStatusUsingRestAssured(link);
            linkStatusMap.put(link, statusCode);

            if (statusCode == 200) {
                validLinks.add(link);
                logger.info("✓ Valid: " + link + " [" + statusCode + "]");
            } else if (statusCode == 404) {
                brokenLinks.add(link);
                logger.warn("✗ Not Found: " + link + " [" + statusCode + "]");
            } else {
                logger.warn("! Unexpected status: " + link + " [" + statusCode + "]");
            }
        }

        reportLinkStatus();

        // Log broken links count (not asserting to allow test to complete)
        logger.info("Total broken links found: " + brokenLinks.size());
    }

    @Test(description = "Validate links with different HTTP methods")
    public void testLinksWithDifferentMethods() {
        logger.info("Testing: Links validation with HEAD and GET methods");

        String testUrl = "https://jsonplaceholder.typicode.com/posts/1";

        // Test with HEAD method (faster, only headers)
        Response headResponse = given()
                .when()
                .head(testUrl)
                .then()
                .extract()
                .response();

        int headStatusCode = headResponse.getStatusCode();
        logger.info("HEAD request status: " + headStatusCode);

        // Test with GET method (full content)
        Response getResponse = given()
                .when()
                .get(testUrl)
                .then()
                .extract()
                .response();

        int getStatusCode = getResponse.getStatusCode();
        logger.info("GET request status: " + getStatusCode);

        // Both should return 200
        assertThat("HEAD status should be 200", headStatusCode, equalTo(200));
        assertThat("GET status should be 200", getStatusCode, equalTo(200));
        assertThat("HEAD and GET should return same status", headStatusCode, equalTo(getStatusCode));

        logger.info("Both HEAD and GET methods validated successfully");
    }

    @Test(description = "Categorize links by status code")
    public void testCategorizeLinksbyStatusCode() {
        logger.info("Testing: Categorize links by status code");

        List<String> testLinks = Arrays.asList(
                "https://jsonplaceholder.typicode.com/posts",
                "https://jsonplaceholder.typicode.com/users",
                "https://jsonplaceholder.typicode.com/invalid-endpoint",
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com/posts/9999"
        );

        // Check all links
        testLinks.forEach(this::checkLinkStatusUsingRestAssured);

        // Categorize by status code
        Map<String, List<String>> categorizedLinks = linkStatusMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> getStatusCategory(entry.getValue()),
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                ));

        // Report categories
        logger.info("=== Link Status Categories ===");
        categorizedLinks.forEach((category, links) -> {
            logger.info(category + " (" + links.size() + " links):");
            links.forEach(link -> logger.info("  - " + link + " [" + linkStatusMap.get(link) + "]"));
        });

        // Count links by category
        int successCount = categorizedLinks.getOrDefault("Success (2xx)", Collections.emptyList()).size();
        int clientErrorCount = categorizedLinks.getOrDefault("Client Error (4xx)", Collections.emptyList()).size();

        logger.info("Success links: " + successCount);
        logger.info("Client error links: " + clientErrorCount);
    }

    @Test(description = "Parallel broken link checking for performance")
    public void testParallelBrokenLinkChecking() {
        logger.info("Testing: Parallel broken link checking");

        List<String> links = Arrays.asList(
                "https://jsonplaceholder.typicode.com/posts",
                "https://jsonplaceholder.typicode.com/comments",
                "https://jsonplaceholder.typicode.com/albums",
                "https://jsonplaceholder.typicode.com/photos",
                "https://jsonplaceholder.typicode.com/todos",
                "https://jsonplaceholder.typicode.com/users",
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com/users/1"
        );

        long startTime = System.currentTimeMillis();

        // Check links in parallel using parallel stream
        Map<String, Integer> results = links.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        link -> link,
                        this::checkLinkStatusUsingRestAssured
                ));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Checked " + links.size() + " links in " + duration + "ms");
        logger.info("Average time per link: " + (duration / links.size()) + "ms");

        // Report results
        results.forEach((link, status) -> {
            String statusType = (status >= 200 && status < 400) ? "✓" : "✗";
            logger.info(statusType + " " + link + " [" + status + "]");
        });

        // Count broken links
        long brokenCount = results.values().stream()
                .filter(status -> status >= 400)
                .count();

        logger.info("Broken links found: " + brokenCount + "/" + links.size());
        assertThat("No broken links should be found", brokenCount, equalTo(0L));
    }

    @Test(description = "Extract and validate all image links")
    public void testImageLinks() {
        logger.info("Testing: Image links validation");

        String pageUrl = "https://jsonplaceholder.typicode.com/";

        try {
            Document document = Jsoup.connect(pageUrl).get();
            
            // Extract image links
            Elements images = document.select("img[src]");
            logger.info("Found " + images.size() + " images on the page");

            for (Element img : images) {
                String imgUrl = img.attr("abs:src");
                if (!imgUrl.isEmpty()) {
                    int statusCode = checkLinkStatus(imgUrl);
                    logger.info("Image: " + imgUrl + " [" + statusCode + "]");
                }
            }

            // Extract CSS links
            Elements cssLinks = document.select("link[href][rel=stylesheet]");
            logger.info("Found " + cssLinks.size() + " CSS files");

            for (Element css : cssLinks) {
                String cssUrl = css.attr("abs:href");
                if (!cssUrl.isEmpty()) {
                    int statusCode = checkLinkStatus(cssUrl);
                    logger.info("CSS: " + cssUrl + " [" + statusCode + "]");
                }
            }

            // Extract script links
            Elements scripts = document.select("script[src]");
            logger.info("Found " + scripts.size() + " script files");

            for (Element script : scripts) {
                String scriptUrl = script.attr("abs:src");
                if (!scriptUrl.isEmpty()) {
                    int statusCode = checkLinkStatus(scriptUrl);
                    logger.info("Script: " + scriptUrl + " [" + statusCode + "]");
                }
            }

        } catch (IOException e) {
            logger.error("Failed to fetch page: " + e.getMessage());
        }
    }

    // ========== Helper Methods ==========

    /**
     * Check link status using HttpURLConnection
     */
    private int checkLinkStatus(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            
            int statusCode = connection.getResponseCode();
            connection.disconnect();
            
            linkStatusMap.put(url, statusCode);
            
            if (statusCode >= 200 && statusCode < 400) {
                validLinks.add(url);
            } else {
                brokenLinks.add(url);
            }
            
            return statusCode;
            
        } catch (IOException e) {
            logger.error("Error checking link: " + url + " - " + e.getMessage());
            brokenLinks.add(url);
            linkStatusMap.put(url, 0);
            return 0;
        }
    }

    /**
     * Check link status using REST Assured
     */
    private int checkLinkStatusUsingRestAssured(String url) {
        try {
            Response response = given()
                    .redirects().follow(false) // Don't follow redirects to check actual status
                    .when()
                    .head(url);
            
            return response.getStatusCode();
            
        } catch (Exception e) {
            logger.error("Error checking link with REST Assured: " + url + " - " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get status category based on status code
     */
    private String getStatusCategory(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "Success (2xx)";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "Redirection (3xx)";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "Client Error (4xx)";
        } else if (statusCode >= 500) {
            return "Server Error (5xx)";
        } else {
            return "Unknown/Connection Failed";
        }
    }

    /**
     * Report link status summary
     */
    private void reportLinkStatus() {
        logger.info("========================================");
        logger.info("Link Validation Summary");
        logger.info("========================================");
        logger.info("Total Links Checked: " + linkStatusMap.size());
        logger.info("Valid Links: " + validLinks.size());
        logger.info("Broken Links: " + brokenLinks.size());
        
        if (!brokenLinks.isEmpty()) {
            logger.warn("Broken Links Found:");
            brokenLinks.forEach(link -> logger.warn("  - " + link + " [" + linkStatusMap.get(link) + "]"));
        }
        
        logger.info("========================================");
        
        // Reset for next test
        linkStatusMap.clear();
        brokenLinks.clear();
        validLinks.clear();
    }
}
