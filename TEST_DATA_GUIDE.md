# Test Data Management

## Overview

The RestAssured API testing project now uses external JSON files for test data management instead of hardcoding values in test classes. This approach provides better maintainability, reusability, and flexibility.

## Test Data File Structure

**Location:** `src/test/resources/testdata.json`

The test data is organized by data type:

```json
{
  "posts": [
    {
      "title": "Test Post",
      "body": "This is a test post created using RestAssured",
      "userId": 1
    }
  ],
  "comments": [
    {
      "postId": 1,
      "name": "Test Comment",
      "email": "test@example.com",
      "body": "This is a test comment"
    }
  ],
  "updateData": [
    {
      "id": 1,
      "title": "Updated Post Title",
      "body": "This is an updated post",
      "userId": 1
    }
  ],
  "patchData": [
    {
      "title": "Patched Title"
    }
  ]
}
```

## TestDataReader Utility

**Location:** `src/test/java/com/api/testing/utils/TestDataReader.java`

The `TestDataReader` class provides convenient methods to read and parse test data:

### Available Methods

#### `getDataByKey(String key)`
Retrieves a single JSON object by key.
```java
JsonObject data = TestDataReader.getDataByKey("updateData");
```

#### `getArrayByKey(String key)`
Retrieves an array of JSON objects by key.
```java
JsonArray posts = TestDataReader.getArrayByKey("posts");
```

#### `getFirstFromArray(String key)`
Retrieves the first element from an array.
```java
JsonObject firstPost = TestDataReader.getFirstFromArray("posts");
```

#### `getFromArray(String key, int index)`
Retrieves an element from an array by index.
```java
JsonObject secondComment = TestDataReader.getFromArray("comments", 1);
```

#### `toJsonString(JsonObject jsonObject)`
Converts a JsonObject to a JSON string.
```java
String jsonString = TestDataReader.toJsonString(postData);
```

#### `getAllTestData()`
Returns the entire test data JSON object.
```java
JsonObject allData = TestDataReader.getAllTestData();
```

#### `reloadTestData()`
Reloads test data from the file (useful for test resets).
```java
TestDataReader.reloadTestData();
```

## Usage Examples

### PostRequestTest
```java
@Test(description = "Create a new post")
public void testCreatePost() {
    // Read test data from JSON
    JsonObject postData = TestDataReader.getFirstFromArray("posts");
    String requestBody = TestDataReader.toJsonString(postData);

    // Use in test
    given()
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post("/posts")
        .then()
        .statusCode(201)
        .body("title", equalTo(postData.get("title").getAsString()));
}
```

### PutPatchRequestTest
```java
@Test(description = "Update a post using PUT")
public void testUpdatePostWithPut() {
    // Read update data
    JsonObject updateData = TestDataReader.getFirstFromArray("updateData");
    String updatedBody = TestDataReader.toJsonString(updateData);

    given()
        .contentType(ContentType.JSON)
        .body(updatedBody)
        .pathParam("id", 1)
        .when()
        .put("/posts/{id}")
        .then()
        .statusCode(200)
        .body("title", equalTo(updateData.get("title").getAsString()));
}
```

## Benefits

### ✅ Maintainability
- Test data is centralized in a single JSON file
- Easy to update test data without modifying test code
- Changes to test data don't require recompilation

### ✅ Reusability
- Same test data can be used across multiple tests
- Easy to add new test data sets

### ✅ Flexibility
- Support for multiple test scenarios
- Easy to add parameters for different test conditions

### ✅ Scalability
- Easy to extend with new data categories
- Can support complex nested data structures

### ✅ Readability
- Clear separation between test logic and test data
- Data is human-readable JSON format

## How to Add New Test Data

1. Open `src/test/resources/testdata.json`
2. Add a new key/category to the JSON:
```json
{
  "newDataCategory": [
    {
      "field1": "value1",
      "field2": "value2"
    }
  ]
}
```

3. In your test, retrieve and use it:
```java
JsonObject newData = TestDataReader.getFirstFromArray("newDataCategory");
String jsonString = TestDataReader.toJsonString(newData);
```

## File Access

The TestDataReader uses the file path: `src/test/resources/testdata.json`

Make sure the file exists in this location for tests to run properly.

## Logging

Test data loading is logged using Log4j:
- Successful load: `INFO` level log
- Failures: `ERROR` level log with exception details

Check `logs/api-testing.log` for detailed loading information.
