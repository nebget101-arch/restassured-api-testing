# Environment Configuration Guide

This REST Assured API testing framework supports multiple environments (dev, staging, production) with easy configuration management.

## Quick Start

### Running Tests in Different Environments

**Development (default):**
```bash
mvn test
# or explicitly
mvn test -Denv=dev
```

**Staging:**
```bash
mvn test -Denv=staging
```

**Production:**
```bash
mvn test -Denv=prod
```

**Run specific test class:**
```bash
mvn test -Dtest=ChainRequestTest -Denv=staging
```

## Configuration Files

### config.properties
Located at: `src/test/resources/config.properties`

Contains environment-specific settings:
- Base URLs for each environment
- Authentication credentials
- Timeout settings
- API version
- Feature flags

## ConfigManager Usage

### In Test Classes

```java
import com.api.testing.utils.ConfigManager;

// Get base URL
String baseUrl = ConfigManager.getBaseUrl();

// Get environment
String env = ConfigManager.getCurrentEnvironment();

// Get credentials
String username = ConfigManager.getUsername();
String password = ConfigManager.getPassword();
String token = ConfigManager.getAuthToken();

// Get timeouts
int connectionTimeout = ConfigManager.getConnectionTimeout();
int requestTimeout = ConfigManager.getRequestTimeout();

// Get custom property
String apiVersion = ConfigManager.getApiVersion();
```

### Using BaseTest Methods

```java
public class MyTest extends BaseTest {
    
    @Test
    public void testSomething() {
        // Check environment
        if (isProduction()) {
            // Skip destructive tests in production
            return;
        }
        
        // Use basic request spec
        given().spec(getRequestSpec())
            .when().get("/endpoint")
            .then().statusCode(200);
        
        // Use authenticated request spec
        given().spec(getAuthenticatedRequestSpec())
            .when().get("/protected")
            .then().statusCode(200);
    }
}
```

## Environment-Specific Configuration

### Adding New Environment

1. Add new properties in `config.properties`:
```properties
base.url.qa=https://qa-api.example.com
auth.username.qa=qa_user
auth.password.qa=qa_password
```

2. Run tests with new environment:
```bash
mvn test -Denv=qa
```

### Updating Environment Settings

Edit `src/test/resources/config.properties` and update the relevant properties:

```properties
# Development settings
base.url.dev=https://jsonplaceholder.typicode.com
auth.token.dev=your_dev_token

# Staging settings
base.url.staging=https://staging-api.yourcompany.com
auth.token.staging=your_staging_token

# Production settings
base.url.prod=https://api.yourcompany.com
auth.token.prod=your_prod_token
```

## Environment Checks in Tests

```java
@Test
public void testDataCreation() {
    // Skip destructive tests in production
    if (isProduction()) {
        logger.info("Skipping destructive test in production");
        throw new SkipException("Destructive test skipped in production");
    }
    
    // Safe to create test data in dev/staging
    // ... test code
}
```

## CI/CD Integration

### GitHub Actions Example
```yaml
- name: Run Tests in Staging
  run: mvn test -Denv=staging

- name: Run Tests in Production
  run: mvn test -Denv=prod
```

### Jenkins Pipeline
```groovy
stage('Test Staging') {
    steps {
        sh 'mvn clean test -Denv=staging'
    }
}
```

## Best Practices

1. **Never commit sensitive credentials** - Use environment variables or secret management
2. **Use different test suites** for different environments
3. **Skip destructive tests** in production using environment checks
4. **Validate environment** before running critical tests
5. **Use meaningful environment names** (dev, staging, prod, qa, etc.)

## Advanced Configuration

### Using Environment Variables

You can override config properties using environment variables:

```bash
export BASE_URL=https://custom-api.example.com
export AUTH_TOKEN=custom_token
mvn test
```

Update ConfigManager to read from environment variables:
```java
String baseUrl = System.getenv("BASE_URL") != null ? 
    System.getenv("BASE_URL") : ConfigManager.getBaseUrl();
```

### Dynamic Configuration

```java
@BeforeClass
public void setup() {
    // Change environment programmatically
    ConfigManager.setEnvironment("staging");
    
    // Reload configuration
    ConfigManager.reload();
}
```

## Troubleshooting

**Issue: Tests failing with wrong URL**
- Check if correct environment is set: `mvn test -Denv=dev`
- Verify `config.properties` has correct URL

**Issue: Configuration not loading**
- Ensure `config.properties` is in `src/test/resources`
- Check file permissions
- Verify properties file format

**Issue: Authentication failing**
- Verify credentials in `config.properties`
- Check if using correct auth method (Bearer token vs Basic auth)
- Ensure auth token is not expired

## Example Test Run

```bash
# Development testing (default)
mvn clean test

# Staging testing with specific test
mvn clean test -Dtest=ChainRequestTest -Denv=staging

# Production smoke tests only
mvn clean test -Dgroups=smoke -Denv=prod
```
