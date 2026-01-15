package com.api.testing.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.*;

/**
 * Custom TestNG listener for generating neat test reports
 */
public class TestReportListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestReportListener.class);
    private List<TestResultDetails> testResults = new ArrayList<>();
    private LocalDateTime suiteStartTime;
    private LocalDateTime suiteEndTime;

    @Override
    public void onStart(ITestContext context) {
        suiteStartTime = LocalDateTime.now();
        logger.info("\n" + "=".repeat(80));
        logger.info("TEST SUITE STARTED: " + context.getName());
        logger.info("Start Time: " + suiteStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("=".repeat(80) + "\n");
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("▶ TEST STARTED: " + result.getMethod().getMethodName());
        logger.info("  Description: " + result.getMethod().getDescription());
        logger.info("  Class: " + result.getTestClass().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        logger.info("✓ TEST PASSED: " + result.getMethod().getMethodName() + " (" + duration + "ms)");
        logSeparator();

        testResults.add(new TestResultDetails(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription(),
                "PASSED",
                duration,
                null
        ));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        logger.error("✗ TEST FAILED: " + result.getMethod().getMethodName() + " (" + duration + "ms)");
        logger.error("  Error: " + result.getThrowable().getMessage());
        logSeparator();

        testResults.add(new TestResultDetails(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription(),
                "FAILED",
                duration,
                result.getThrowable().getMessage()
        ));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⊗ TEST SKIPPED: " + result.getMethod().getMethodName());
        logSeparator();

        testResults.add(new TestResultDetails(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription(),
                "SKIPPED",
                0,
                result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason provided"
        ));
    }

    @Override
    public void onFinish(ITestContext context) {
        suiteEndTime = LocalDateTime.now();
        long totalDuration = System.currentTimeMillis() - context.getStartDate().getTime();

        logger.info("\n" + "=".repeat(80));
        logger.info("TEST SUITE COMPLETED: " + context.getName());
        logger.info("End Time: " + suiteEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("=".repeat(80));

        generateReport(context, totalDuration);
    }

    /**
     * Generate detailed test report
     */
    private void generateReport(ITestContext context, long totalDuration) {
        logger.info("\n" + "█".repeat(80));
        logger.info("█" + " ".repeat(78) + "█");
        logger.info("█" + center("TEST EXECUTION SUMMARY REPORT", 78) + "█");
        logger.info("█" + " ".repeat(78) + "█");
        logger.info("█".repeat(80));

        // Suite information
        logger.info("\n┌─ SUITE INFORMATION " + "─".repeat(57) + "┐");
        logger.info("│ Suite Name       : " + padRight(context.getName(), 57) + "│");
        logger.info("│ Start Time       : " + padRight(
                suiteStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 57) + "│");
        logger.info("│ End Time         : " + padRight(
                suiteEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 57) + "│");
        logger.info("│ Total Duration   : " + padRight(formatDuration(totalDuration), 57) + "│");
        logger.info("└" + "─".repeat(77) + "┘");

        // Test statistics
        int passed = (int) testResults.stream().filter(r -> "PASSED".equals(r.status)).count();
        int failed = (int) testResults.stream().filter(r -> "FAILED".equals(r.status)).count();
        int skipped = (int) testResults.stream().filter(r -> "SKIPPED".equals(r.status)).count();
        int total = testResults.size();
        double passRate = total > 0 ? (passed * 100.0) / total : 0;

        logger.info("\n┌─ TEST STATISTICS " + "─".repeat(59) + "┐");
        logger.info("│ Total Tests      : " + padRight(String.valueOf(total), 57) + "│");
        logger.info("│ Passed           : " + padRight(passed + " ✓", 57) + "│");
        logger.info("│ Failed           : " + padRight(failed + " ✗", 57) + "│");
        logger.info("│ Skipped          : " + padRight(skipped + " ⊗", 57) + "│");
        logger.info("│ Pass Rate        : " + padRight(String.format("%.2f%%", passRate), 57) + "│");
        logger.info("└" + "─".repeat(77) + "┘");

        // Detailed results
        if (!testResults.isEmpty()) {
            logger.info("\n┌─ DETAILED TEST RESULTS " + "─".repeat(52) + "┐");
            logger.info("│ " + padRight("Test Name", 30) + " │ " + padRight("Status", 8) + " │ Duration   │");
            logger.info("├─" + "─".repeat(30) + "─┼─" + "─".repeat(8) + "─┼────────────┤");

            for (TestResultDetails result : testResults) {
                String statusSymbol = getStatusSymbol(result.status);
                logger.info("│ " + padRight(result.testName, 30) + " │ " +
                        padRight(statusSymbol + " " + result.status, 8) + " │ " +
                        padRight(result.duration + "ms", 10) + " │");
            }
            logger.info("└─" + "─".repeat(30) + "─┴─" + "─".repeat(8) + "─┴────────────┘");
        }

        // Failed tests details
        List<TestResultDetails> failedTestList = testResults.stream()
                .filter(r -> "FAILED".equals(r.status))
                .collect(Collectors.toList());

        if (!failedTestList.isEmpty()) {
            logger.info("\n┌─ FAILED TESTS DETAILS " + "─".repeat(53) + "┐");
            for (TestResultDetails failedTest : failedTestList) {
                logger.info("│ Test: " + failedTest.testName + " " + " ".repeat(Math.max(0, 68 - failedTest.testName.length())) + "│");
                logger.info("│ Error: " + padRight(failedTest.errorMessage, 70) + "│");
            }
            logger.info("└" + "─".repeat(77) + "┘");
        }

        // Overall status
        String overallStatus = failed == 0 ? "✓ ALL TESTS PASSED" : "✗ SOME TESTS FAILED";
        logger.info("\n┌" + "─".repeat(77) + "┐");
        logger.info("│" + center(overallStatus, 77) + "│");
        logger.info("└" + "─".repeat(77) + "┘\n");
    }

    private String getStatusSymbol(String status) {
        if ("PASSED".equals(status)) {
            return "✓";
        } else if ("FAILED".equals(status)) {
            return "✗";
        } else if ("SKIPPED".equals(status)) {
            return "⊗";
        } else {
            return "?";
        }
    }

    private void logSeparator() {
        logger.info("─".repeat(80));
    }

    private String padRight(String str, int length) {
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
    }

    private String center(String str, int length) {
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        int totalPadding = length - str.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + str + " ".repeat(rightPadding);
    }

    private String formatDuration(long ms) {
        if (ms < 1000) {
            return ms + "ms";
        }
        return String.format("%.2fs", ms / 1000.0);
    }

    /**
     * Inner class to hold test result details
     */
    private static class TestResultDetails {
        String testName;
        String description;
        String status;
        long duration;
        String errorMessage;

        TestResultDetails(String testName, String description, String status, long duration, String errorMessage) {
            this.testName = testName;
            this.description = description;
            this.status = status;
            this.duration = duration;
            this.errorMessage = errorMessage;
        }
    }
}
