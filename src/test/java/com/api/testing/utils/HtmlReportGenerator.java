package com.api.testing.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * HTML Report Generator for test execution results
 */
public class HtmlReportGenerator {
    private String reportName;
    private LocalDateTime reportTime;
    private List<TestResult> results;
    private static final String REPORTS_DIR = "target/api-test-reports/";

    public HtmlReportGenerator(String reportName) {
        this.reportName = reportName;
        this.reportTime = LocalDateTime.now();
        this.results = new ArrayList<>();
    }

    /**
     * Add a test result to the report
     */
    public void addTestResult(String testName, String description, String status, long duration, String responseBody) {
        results.add(new TestResult(testName, description, status, duration, responseBody));
    }

    /**
     * Generate HTML report file
     */
    public void generateReport() {
        try {
            String fileName = REPORTS_DIR + "test-report-" + 
                    reportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".html";
            
            // Create directory if not exists
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(REPORTS_DIR));
            
            // Write HTML report
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(generateHtmlContent());
            }
            
            System.out.println("Report generated: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to generate HTML report: " + e.getMessage());
        }
    }

    /**
     * Generate HTML content
     */
    private String generateHtmlContent() {
        int passed = (int) results.stream().filter(r -> "PASSED".equals(r.status)).count();
        int failed = (int) results.stream().filter(r -> "FAILED".equals(r.status)).count();
        int skipped = (int) results.stream().filter(r -> "SKIPPED".equals(r.status)).count();
        int total = results.size();
        double passRate = total > 0 ? (passed * 100.0) / total : 0;

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>").append(reportName).append(" - Test Report</title>\n");
        html.append(getStyles());
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"container\">\n");
        html.append(getHeader());
        html.append(getStatistics(passed, failed, skipped, total, passRate));
        html.append(getDetailedResults());
        html.append(getFooter());
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private String getStyles() {
        StringBuilder style = new StringBuilder();
        style.append("<style>\n");
        style.append("* {margin: 0; padding: 0; box-sizing: border-box;}\n");
        style.append("body {font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #333; padding: 20px; min-height: 100vh;}\n");
        style.append(".container {max-width: 1200px; margin: 0 auto; background: white; border-radius: 10px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); overflow: hidden;}\n");
        style.append(".header {background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; text-align: center;}\n");
        style.append(".header h1 {font-size: 2.5em; margin-bottom: 10px;}\n");
        style.append(".header p {font-size: 1.1em; opacity: 0.9;}\n");
        style.append(".content {padding: 40px;}\n");
        style.append(".statistics {display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 40px;}\n");
        style.append(".stat-card {background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; border-radius: 8px; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1);}\n");
        style.append(".stat-card.passed {background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);}\n");
        style.append(".stat-card.failed {background: linear-gradient(135deg, #ee0979 0%, #ff6a00 100%);}\n");
        style.append(".stat-card.skipped {background: linear-gradient(135deg, #ffa400 0%, #ffb74d 100%);}\n");
        style.append(".stat-value {font-size: 2.5em; font-weight: bold; margin: 10px 0;}\n");
        style.append(".stat-label {font-size: 0.9em; opacity: 0.9;}\n");
        style.append(".results-table {width: 100%; border-collapse: collapse; margin: 20px 0;}\n");
        style.append(".results-table thead {background: #f5f5f5; border-bottom: 2px solid #667eea;}\n");
        style.append(".results-table th {padding: 15px; text-align: left; font-weight: 600; color: #333;}\n");
        style.append(".results-table td {padding: 15px; border-bottom: 1px solid #eee;}\n");
        style.append(".results-table tr:hover {background: #f9f9f9;}\n");
        style.append(".status-badge {display: inline-block; padding: 5px 12px; border-radius: 20px; font-weight: 600; font-size: 0.85em;}\n");
        style.append(".status-badge.passed {background: #d4edda; color: #155724;}\n");
        style.append(".status-badge.failed {background: #f8d7da; color: #721c24;}\n");
        style.append(".status-badge.skipped {background: #fff3cd; color: #856404;}\n");
        style.append(".footer {background: #f5f5f5; padding: 20px; text-align: center; color: #666; font-size: 0.9em; border-top: 1px solid #ddd;}\n");
        style.append(".section-title {font-size: 1.5em; margin: 30px 0 20px 0; color: #333; border-bottom: 2px solid #667eea; padding-bottom: 10px;}\n");
        style.append("</style>\n");
        return style.toString();
    }

    private String getHeader() {
        String timestamp = reportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "    <div class=\"header\"><h1>📊 Test Execution Report</h1><p>" + reportName + "</p><p style=\"margin-top: 10px; font-size: 0.9em;\">Generated: " + timestamp + "</p></div>\n";
    }

    private String getStatistics(int passed, int failed, int skipped, int total, double passRate) {
        return "    <div class=\"content\"><div class=\"section-title\">📈 Test Statistics</div><div class=\"statistics\">" +
                "<div class=\"stat-card\"><div class=\"stat-label\">Total Tests</div><div class=\"stat-value\">" + total + "</div></div>" +
                "<div class=\"stat-card passed\"><div class=\"stat-label\">Passed ✓</div><div class=\"stat-value\">" + passed + "</div></div>" +
                "<div class=\"stat-card failed\"><div class=\"stat-label\">Failed ✗</div><div class=\"stat-value\">" + failed + "</div></div>" +
                "<div class=\"stat-card skipped\"><div class=\"stat-label\">Skipped ⊗</div><div class=\"stat-value\">" + skipped + "</div></div>" +
                "<div class=\"stat-card\"><div class=\"stat-label\">Pass Rate</div><div class=\"stat-value\">" + String.format("%.1f", passRate) + "%</div></div>" +
                "</div>\n";
    }

    private String getDetailedResults() {
        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"section-title\">📋 Test Results</div><table class=\"results-table\"><thead><tr><th style=\"width: 25%;\">Test Name</th><th style=\"width: 35%;\">Description</th><th style=\"width: 15%;\">Status</th><th style=\"width: 10%;\">Duration</th></tr></thead><tbody>\n");

        for (TestResult result : results) {
            String statusClass = result.status.toLowerCase();
            html.append("<tr><td><strong>").append(result.testName).append("</strong></td><td>").append(result.description != null ? result.description : "N/A").append("</td><td><span class=\"status-badge ").append(statusClass).append("\">").append(result.status).append("</span></td><td>").append(result.duration).append("ms</td></tr>\n");
        }

        html.append("</tbody></table></div>\n");
        return html.toString();
    }

    private String getFooter() {
        String timestamp = reportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "        <div class=\"footer\"><p>RestAssured API Testing Framework | Report generated on " + timestamp + "</p></div></div>\n";
    }

    private static class TestResult {
        String testName;
        String description;
        String status;
        long duration;
        String responseBody;

        TestResult(String testName, String description, String status, long duration, String responseBody) {
            this.testName = testName;
            this.description = description;
            this.status = status;
            this.duration = duration;
            this.responseBody = responseBody;
        }
    }
}
