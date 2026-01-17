# Allure Reporting Guide

## 📊 Allure Reporting Setup

This project includes Allure reporting for comprehensive test results visualization.

---

## 🚀 Local Setup

### Prerequisites
- Java 11+
- Maven 3.6+
- Allure Command Line (optional for local viewing)

### Install Allure Command Line

**macOS:**
```bash
brew install allure
```

**Windows:**
```bash
scoop install allure
```

**Linux:**
```bash
wget https://github.com/allure-framework/allure2/releases/download/2.24.0/allure-2.24.0.tgz
tar -zxvf allure-2.24.0.tgz
sudo mv allure-2.24.0 /opt/allure
echo 'export PATH="/opt/allure/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

---

## 🧪 Running Tests with Allure

### Run Tests and Generate Results
```bash
# Run all tests
mvn clean test

# Run specific test
mvn clean test -Dtest=AllureReportingTest

# Run with specific environment
mvn clean test -Denv=staging
```

After tests run, Allure results are stored in `target/allure-results/`

### Generate and Open Allure Report Locally

**Option 1: Generate and serve report**
```bash
allure serve target/allure-results
```
This will automatically open the report in your default browser.

**Option 2: Generate report to specific directory**
```bash
# Generate report
allure generate target/allure-results --clean -o allure-report

# Open report
allure open allure-report
```

**Option 3: Using Maven plugin**
```bash
# Generate report using Maven
mvn allure:report

# Serve report
mvn allure:serve
```

---

## 🎨 Allure Features Used

### Annotations

```java
@Epic("API Testing")                    // High-level feature group
@Feature("User Management")             // Feature being tested
@Story("Create User")                   // User story
@Severity(SeverityLevel.CRITICAL)       // Test importance
@Description("Test description here")   // Detailed description
@Owner("QA Team")                       // Test owner
@Issue("JIRA-123")                      // Link to issue
@TmsLink("TC-456")                      // Link to test case
@Link(name = "API Docs", url = "...")   // Custom links
```

### Steps and Attachments

```java
// Add steps
Allure.step("Step 1: Do something", () -> {
    // Test code here
});

// Add parameters
Allure.parameter("User ID", userId);
Allure.parameter("Environment", "staging");

// Add attachments
Allure.addAttachment("Request Body", "application/json", jsonString);
Allure.addAttachment("Screenshot", screenshotBytes);

// Add labels
Allure.label("layer", "api");
Allure.label("testType", "smoke");
```

---

## 📁 Project Structure

```
restassured-api-testing/
├── .github/
│   └── workflows/
│       └── api-tests.yml          # GitHub Actions workflow
├── src/test/
│   ├── java/
│   │   └── com/api/testing/tests/
│   │       └── AllureReportingTest.java  # Allure demo tests
│   └── resources/
│       └── allure.properties      # Allure configuration
├── target/
│   ├── allure-results/            # Test results (generated)
│   └── allure-report/             # HTML report (generated)
└── pom.xml                        # Maven with Allure dependencies
```

---

## 🔧 GitHub Actions Integration

### Automatic Report Generation

The workflow (`.github/workflows/api-tests.yml`) automatically:
1. Runs tests on push/PR to main/develop branches
2. Generates Allure report
3. Publishes report to GitHub Pages
4. Uploads artifacts for download

### Accessing Reports on GitHub

**GitHub Pages Report:**
- URL: `https://yourusername.github.io/restassured-api-testing/`
- Updates automatically on each test run
- Maintains history of last 20 reports

**Download Artifacts:**
1. Go to Actions tab in GitHub
2. Click on workflow run
3. Download "allure-report" or "allure-results" artifact

### Enable GitHub Pages

1. Go to repository Settings
2. Navigate to Pages
3. Source: Deploy from a branch
4. Branch: `gh-pages` / root
5. Save

---

## 📊 Allure Report Sections

### Overview
- Total tests, passed, failed, broken, skipped
- Success rate and duration
- Trend graphs over time

### Suites
- Test organization by suites
- Hierarchical view of tests

### Graphs
- Status chart (pie chart)
- Severity distribution
- Duration trend
- Categories trend

### Timeline
- Test execution timeline
- Parallel execution visualization

### Behaviors
- Tests grouped by Epic/Feature/Story
- BDD-style organization

### Packages
- Tests organized by Java packages

### Categories
- Failed tests categorized by error type

---

## 🎯 Best Practices

### 1. Use Descriptive Names
```java
@Test(description = "Verify user can create a post with valid data")
```

### 2. Add Steps for Clarity
```java
Allure.step("Step 1: Authenticate user");
Allure.step("Step 2: Create post");
Allure.step("Step 3: Verify post was created");
```

### 3. Attach Evidence
```java
Allure.addAttachment("Request", "application/json", requestJson);
Allure.addAttachment("Response", "application/json", responseJson);
```

### 4. Use Severity Levels
- **BLOCKER**: Critical issues blocking testing
- **CRITICAL**: Major functionality issues
- **NORMAL**: Regular test cases
- **MINOR**: Minor issues
- **TRIVIAL**: Cosmetic issues

### 5. Link to Issues
```java
@Issue("BUG-123")
@TmsLink("TC-456")
```

### 6. Categorize Tests
```java
@Epic("API Testing")
@Feature("Posts API")
@Story("Create Post")
```

---

## 🔍 Troubleshooting

### GitHub Actions Issues

#### Error: Failed to push to gh-pages
**Solution:**
1. Go to repository **Settings** → **Actions** → **General**
2. Scroll to **Workflow permissions**
3. Select **Read and write permissions**
4. Check **Allow GitHub Actions to create and approve pull requests**
5. Click **Save**

#### Error: GitHub Pages deployment failed
**Solution:**
1. Go to **Settings** → **Pages**
2. Ensure Source is set to **Deploy from a branch**
3. Select branch: **gh-pages** and folder: **/ (root)**
4. Save and wait for deployment

#### Workflow not triggering
**Solution:**
- Ensure workflow file is in `.github/workflows/` directory
- Check branch names match (main/develop)
- Manually trigger: Go to **Actions** → Select workflow → **Run workflow**

### Local Issues

### Issue: Report not generating
```bash
# Check Allure results exist
ls -la target/allure-results/

# Verify Allure installation
allure --version

# Clean and regenerate
mvn clean test
allure generate target/allure-results --clean
```

### Issue: Empty report
- Ensure tests are actually running
- Check testng.xml has Allure listener
- Verify allure-testng dependency in pom.xml

### Issue: Attachments not showing
- Check file size (Allure has limits)
- Verify content type is correct
- Ensure data is not null

### Issue: GitHub Pages not updating
- Check workflow execution in Actions tab
- Verify gh-pages branch exists
- Check repository Settings > Pages is enabled

---

## 📈 CI/CD Integration Examples

### Jenkins
```groovy
pipeline {
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Allure Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
}
```

### GitLab CI
```yaml
test:
  script:
    - mvn clean test
  artifacts:
    when: always
    paths:
      - target/allure-results
    expire_in: 1 week

pages:
  stage: deploy
  script:
    - allure generate target/allure-results -o public
  artifacts:
    paths:
      - public
```

---

## 📚 Additional Resources

- [Allure Documentation](https://docs.qameta.io/allure/)
- [Allure TestNG](https://docs.qameta.io/allure/#_testng)
- [Allure REST Assured](https://docs.qameta.io/allure/#_rest_assured)
- [GitHub Actions](https://docs.github.com/en/actions)

---

## 🎨 Customization

### Custom Categories
Create `categories.json` in `src/test/resources/`:

```json
[
  {
    "name": "Ignored tests",
    "matchedStatuses": ["skipped"]
  },
  {
    "name": "Infrastructure problems",
    "matchedStatuses": ["broken", "failed"],
    "messageRegex": ".*Connection.*"
  }
]
```

### Environment Information
The report automatically includes environment info from `allure.properties` and test execution.

---

## 💡 Quick Commands Reference

```bash
# Run tests
mvn clean test

# Generate and open report locally
allure serve target/allure-results

# Generate report to directory
allure generate target/allure-results -o allure-report

# Open existing report
allure open allure-report

# Clean old results
rm -rf target/allure-results target/allure-report

# Run specific test with Allure
mvn clean test -Dtest=AllureReportingTest

# Generate report with Maven
mvn allure:report

# Serve report with Maven
mvn allure:serve
```
