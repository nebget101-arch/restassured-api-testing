package com.api.testing.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class to read and parse test data from JSON file
 */
public class TestDataReader {
    private static final Logger logger = LogManager.getLogger(TestDataReader.class);
    private static final String TEST_DATA_FILE = "src/test/resources/testdata.json";
    private static JsonObject testDataJson;

    static {
        loadTestData();
    }

    /**
     * Load test data from JSON file
     */
    private static void loadTestData() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(TEST_DATA_FILE)), StandardCharsets.UTF_8);
            testDataJson = JsonParser.parseString(content).getAsJsonObject();
            logger.info("Test data loaded successfully from: " + TEST_DATA_FILE);
        } catch (IOException e) {
            logger.error("Failed to load test data from: " + TEST_DATA_FILE, e);
            throw new RuntimeException("Cannot load test data file", e);
        }
    }

    /**
     * Get a specific data object by key
     */
    public static JsonObject getDataByKey(String key) {
        if (testDataJson.has(key)) {
            JsonElement element = testDataJson.get(key);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        }
        logger.warn("Data not found for key: " + key);
        return new JsonObject();
    }

    /**
     * Get an array of data by key
     */
    public static JsonArray getArrayByKey(String key) {
        if (testDataJson.has(key)) {
            JsonElement element = testDataJson.get(key);
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            }
        }
        logger.warn("Array data not found for key: " + key);
        return new JsonArray();
    }

    /**
     * Get first element from an array
     */
    public static JsonObject getFirstFromArray(String key) {
        JsonArray array = getArrayByKey(key);
        if (array.size() > 0) {
            return array.get(0).getAsJsonObject();
        }
        logger.warn("No elements found in array: " + key);
        return new JsonObject();
    }

    /**
     * Get element from array by index
     */
    public static JsonObject getFromArray(String key, int index) {
        JsonArray array = getArrayByKey(key);
        if (index >= 0 && index < array.size()) {
            return array.get(index).getAsJsonObject();
        }
        logger.warn("Index " + index + " not found in array: " + key);
        return new JsonObject();
    }

    /**
     * Convert JsonObject to JSON string
     */
    public static String toJsonString(JsonObject jsonObject) {
        return jsonObject.toString();
    }

    /**
     * Get all test data
     */
    public static JsonObject getAllTestData() {
        return testDataJson;
    }

    /**
     * Reload test data (useful for test resets)
     */
    public static void reloadTestData() {
        loadTestData();
        logger.info("Test data reloaded");
    }
}
