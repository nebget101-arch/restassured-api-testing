package com.api.testing.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Alternative utility class to read test data from hardcoded JSON file
 * Uses ClassLoader to load resources from classpath
 */
public class JsonDataProvider {
    private static final Logger logger = LogManager.getLogger(JsonDataProvider.class);
    private static final String TEST_DATA_FILE = "testdata.json";
    private static JsonObject dataJson;

    static {
        loadJsonData();
    }

    /**
     * Load JSON data from hardcoded file path using ClassLoader
     */
    private static void loadJsonData() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(JsonDataProvider.class.getClassLoader()
                                .getResourceAsStream(TEST_DATA_FILE)),
                        StandardCharsets.UTF_8))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            dataJson = JsonParser.parseString(content.toString()).getAsJsonObject();
            logger.info("JSON data loaded successfully from: " + TEST_DATA_FILE);

        } catch (IOException | NullPointerException e) {
            logger.error("Failed to load JSON data from: " + TEST_DATA_FILE, e);
            throw new RuntimeException("Cannot load JSON data file: " + TEST_DATA_FILE, e);
        }
    }

    /**
     * Get data element by key path (e.g., "posts", "comments")
     */
    public static JsonElement getByKey(String key) {
        if (dataJson != null && dataJson.has(key)) {
            logger.debug("Retrieved data for key: " + key);
            return dataJson.get(key);
        }
        logger.warn("Key not found: " + key);
        return null;
    }

    /**
     * Get a specific object from array by key and index
     */
    public static JsonObject getObjectFromArray(String arrayKey, int index) {
        try {
            JsonElement element = getByKey(arrayKey);
            if (element != null && element.isJsonArray()) {
                return element.getAsJsonArray().get(index).getAsJsonObject();
            }
            logger.warn("Array not found for key: " + arrayKey);
        } catch (IndexOutOfBoundsException e) {
            logger.error("Index out of bounds for array: " + arrayKey + ", index: " + index, e);
        }
        return new JsonObject();
    }

    /**
     * Get first object from array
     */
    public static JsonObject getFirstObject(String arrayKey) {
        return getObjectFromArray(arrayKey, 0);
    }

    /**
     * Get last object from array
     */
    public static JsonObject getLastObject(String arrayKey) {
        try {
            JsonElement element = getByKey(arrayKey);
            if (element != null && element.isJsonArray()) {
                int size = element.getAsJsonArray().size();
                return element.getAsJsonArray().get(size - 1).getAsJsonObject();
            }
        } catch (Exception e) {
            logger.error("Error getting last object from array: " + arrayKey, e);
        }
        return new JsonObject();
    }

    /**
     * Get array size
     */
    public static int getArraySize(String arrayKey) {
        try {
            JsonElement element = getByKey(arrayKey);
            if (element != null && element.isJsonArray()) {
                return element.getAsJsonArray().size();
            }
        } catch (Exception e) {
            logger.error("Error getting array size for: " + arrayKey, e);
        }
        return 0;
    }

    /**
     * Convert JsonObject to pretty JSON string
     */
    public static String toPrettyJson(JsonObject jsonObject) {
        return jsonObject.toString();
    }

    /**
     * Get full JSON object
     */
    public static JsonObject getFullData() {
        return dataJson;
    }

    /**
     * Check if key exists in data
     */
    public static boolean hasKey(String key) {
        return dataJson != null && dataJson.has(key);
    }

    /**
     * Get string value from JsonObject by field name
     */
    public static String getStringValue(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.has(fieldName)) {
            return jsonObject.get(fieldName).getAsString();
        }
        logger.warn("Field not found: " + fieldName);
        return null;
    }

    /**
     * Get integer value from JsonObject by field name
     */
    public static int getIntValue(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.has(fieldName)) {
            return jsonObject.get(fieldName).getAsInt();
        }
        logger.warn("Field not found: " + fieldName);
        return 0;
    }

    /**
     * Reload data from file (useful for testing or refreshing data)
     */
    public static void refresh() {
        loadJsonData();
        logger.info("JSON data reloaded");
    }
}
