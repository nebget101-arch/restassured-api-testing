# RestAssured API Testing Project

## Quick Start Guide

### 1. Build the project
```bash
mvn clean install
```

### 2. Run all tests
```bash
mvn test
```

### 3. View test results
Test results will be displayed in the console and logged to `logs/api-testing.log`

## Project Highlights

✓ Complete Maven project structure
✓ 4 test classes covering GET, POST, PUT/PATCH, DELETE operations
✓ BaseTest class with reusable utilities
✓ TestNG framework with parallel execution support
✓ Log4j2 logging configuration
✓ Comprehensive README with examples
✓ JSONPlaceholder API integration

## Next Steps

1. Modify the `BASE_URI` in [BaseTest.java](src/test/java/com/api/testing/base/BaseTest.java) to test against your own API
2. Create additional test classes following the existing patterns
3. Add authentication headers if needed
4. Customize assertions based on your API responses
