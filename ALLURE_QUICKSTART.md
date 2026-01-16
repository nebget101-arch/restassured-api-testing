# Allure Reporting - Quick Start

## ✅ Setup Complete!

Allure reporting has been successfully integrated into your REST Assured API testing framework.

---

## 🎯 What's Been Added

### 1. Dependencies (pom.xml)
- `allure-testng` (2.24.0) - Allure integration with TestNG
- `allure-rest-assured` (2.24.0) - REST Assured integration
- `aspectjweaver` (1.9.20) - For AspectJ weaving support
- `allure-maven` plugin (2.12.0) - Maven plugin for report generation

### 2. Configuration Files
- `src/test/resources/allure.properties` - Allure configuration
- `testng.xml` - Added AllureTestNg listener
- `.github/workflows/api-tests.yml` - GitHub Actions workflow with Allure

### 3. Test Examples
- `AllureReportingTest.java` - Demonstrates all Allure features:
  - Step-by-step execution with `Allure.step()`
  - Attachments for requests/responses
  - Parameters and environment info
  - Epic/Feature/Story organization
  - Severity levels
  - Issue/TMS links
  - Custom labels

### 4. Documentation
- `ALLURE_GUIDE.md` - Comprehensive Allure guide with examples

---

## 🚀 Quick Commands

### Run Tests Locally
```bash
# Run all tests (generates Allure results)
mvn clean test

# Run specific Allure demo test
mvn clean test -Dtest=AllureReportingTest
```

### View Allure Report Locally

**Option 1: Auto-open in browser (recommended)**
```bash
allure serve target/allure-results
```

**Option 2: Generate to directory**
```bash
allure generate target/allure-results -o allure-report
allure open allure-report
```

**Option 3: Using Maven plugin**
```bash
mvn allure:serve
```

### Install Allure CLI (if not installed)

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

## 📊 GitHub Actions Integration

The workflow automatically:
1. ✅ Runs tests on push/PR to main/develop
2. ✅ Generates Allure report
3. ✅ Publishes to GitHub Pages
4. ✅ Uploads artifacts (results + report)

### Enable GitHub Pages
1. Go to repository **Settings**
2. Navigate to **Pages**
3. Set Source: **Deploy from a branch**
4. Select Branch: **gh-pages** / **root**
5. Save

### Access Reports
- **GitHub Pages:** `https://yourusername.github.io/restassured-api-testing/`
- **Artifacts:** Go to Actions > Workflow Run > Download artifacts

---

## 🎨 Key Allure Features

### Annotations
```java
@Epic("API Testing")
@Feature("User Management")
@Story("Create User")
@Severity(SeverityLevel.CRITICAL)
@Description("Detailed test description")
@Owner("QA Team")
@Issue("BUG-123")
@TmsLink("TC-456")
@Link(name = "API Docs", url = "https://...")
```

### Steps
```java
Allure.step("Step 1: Send GET request", () -> {
    // Test code
});
```

### Attachments
```java
Allure.addAttachment("Request", "application/json", requestJson);
Allure.addAttachment("Response", "application/json", responseJson);
```

### Parameters
```java
Allure.parameter("User ID", userId);
Allure.parameter("Environment", "staging");
```

---

## 📈 Report Sections

1. **Overview** - Test summary, trends, success rate
2. **Suites** - Tests organized by suites
3. **Graphs** - Status, severity, duration charts
4. **Timeline** - Execution timeline
5. **Behaviors** - Epic/Feature/Story organization
6. **Packages** - Tests by Java package
7. **Categories** - Failure categorization

---

## ✨ Next Steps

1. **Run tests locally:**
   ```bash
   mvn clean test
   allure serve target/allure-results
   ```

2. **Push to GitHub:**
   - Commits trigger GitHub Actions
   - Reports auto-publish to GitHub Pages

3. **Customize:**
   - Update `allure.properties` with your issue tracker URLs
   - Add custom categories in `categories.json`
   - Enhance tests with more Allure annotations

4. **Explore:**
   - Check `AllureReportingTest.java` for examples
   - Read `ALLURE_GUIDE.md` for detailed documentation

---

## 📚 Resources

- [ALLURE_GUIDE.md](ALLURE_GUIDE.md) - Complete guide
- [Allure Documentation](https://docs.qameta.io/allure/)
- [Allure TestNG](https://docs.qameta.io/allure/#_testng)
- [Allure REST Assured](https://docs.qameta.io/allure/#_rest_assured)

---

## 🎉 You're All Set!

Your framework now includes enterprise-grade reporting with:
- ✅ Beautiful HTML reports
- ✅ Step-by-step test execution visualization
- ✅ Request/Response attachments
- ✅ Test categorization and organization
- ✅ Historical trends
- ✅ GitHub Pages integration
- ✅ CI/CD ready

Happy Testing! 🚀
