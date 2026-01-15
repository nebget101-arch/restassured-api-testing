# RestAssured API Testing Project

A complete API testing framework using RestAssured, TestNG, and Log4j.

## Project Structure

```
restassured-api-testing/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/api/testing/
│       │       ├── base/
│       │       │   └── BaseTest.java          # Base class with common setup
│       │       └── tests/
│       │           ├── GetRequestTest.java    # GET request tests
│       │           ├── PostRequestTest.java   # POST request tests
│       │           ├── PutPatchRequestTest.java # PUT/PATCH request tests
│       │           └── DeleteRequestTest.java # DELETE request tests
│       └── resources/
│           └── log4j2.xml                     # Logging configuration
├── pom.xml                                    # Maven configuration
├── testng.xml                                 # TestNG suite configuration
└── README.md                                  # This file
```

## Dependencies

- **RestAssured 5.3.2**: REST API testing library
- **TestNG 7.8.0**: Testing framework
- **JUnit 4.13.2**: Testing framework (alternative)
- **Gson 2.10.1**: JSON serialization
- **Log4j 2.20.0**: Logging framework

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Installation

1. Navigate to the project directory:
```bash
cd /Users/nebyougetaneh/Desktop/Restassured
```

2. Install dependencies:
```bash
mvn clean install
```

## Running Tests

### Run all tests:
```bash
mvn test
```

### Run specific test class:
```bash
mvn test -Dtest=GetRequestTest
```

### Run specific test method:
```bash
mvn test -Dtest=GetRequestTest#testGetAllPosts
```

### Run tests in parallel:
```bash
mvn test -DsuiteXmlFile=testng.xml
```

## Test Categories

### 1. GET Request Tests (GetRequestTest.java)
- Get all posts
- Get post by ID
- Get posts by user ID
- Get user by ID
- Verify response headers

### 2. POST Request Tests (PostRequestTest.java)
- Create a new post
- Create a comment on a post

### 3. PUT/PATCH Request Tests (PutPatchRequestTest.java)
- Update a post using PUT
- Partially update a post using PATCH

### 4. DELETE Request Tests (DeleteRequestTest.java)
- Delete a post
- Verify deleted post

## Base API

This project uses the JSONPlaceholder API as an example:
- **Base URI**: https://jsonplaceholder.typicode.com

## Key Features

- **BaseTest Class**: Provides common setup and utilities for all tests
- **Reusable RequestSpec**: Methods for creating request specifications with headers
- **Logging**: All tests log their execution using Log4j
- **Assertions**: Uses Hamcrest matchers for flexible assertions
- **Parallel Execution**: TestNG is configured for parallel test execution

## Example Usage

```java
@Test
public void testGetAllPosts() {
    given()
        .when()
        .get("/posts")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
}
```

## Logging

Logs are generated in two locations:
- **Console**: Real-time output during test execution
- **File**: `logs/api-testing.log` for persistent logging

## Extending the Project

1. Add new test classes in `src/test/java/com/api/testing/tests/`
2. Extend `BaseTest` class for common setup
3. Update `testng.xml` to include new test classes
4. Follow existing test patterns for consistency

## Best Practices

- Use descriptive test names and descriptions
- Add logging statements for debugging
- Use path parameters for dynamic URLs
- Use query parameters for filtering
- Validate both status codes and response body
- Group related tests in separate classes
- Use base URL and common headers from BaseTest

## Troubleshooting

If tests fail due to network issues:
1. Verify internet connection
2. Check API endpoint availability
3. Review logs in `logs/api-testing.log`
4. Ensure correct base URI in BaseTest class

## Resources

- [RestAssured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [JSONPlaceholder API](https://jsonplaceholder.typicode.com/)
# restassured-api-testing
