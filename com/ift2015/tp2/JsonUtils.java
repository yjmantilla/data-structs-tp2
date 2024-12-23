package com.ift2015.tp2;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

public class JsonUtils {

    /**
     * Converts a nested map into a JSON string with a specified indentation.
     *
     * @param map    The input map (can contain nested maps, strings, or numbers).
     * @param indent The number of spaces for indentation.
     * @return The formatted JSON string.
     */


     public static String mapToJson(Map<String, Object> map, int indent) {
        StringBuilder jsonBuilder = new StringBuilder();
        buildJsonFromMap(map, jsonBuilder, 0, indent);
        return jsonBuilder.toString();
    }

    private static void buildJsonFromMap(Map<String, Object> map, StringBuilder jsonBuilder, int currentIndent, int indent) {
        jsonBuilder.append("{\n");
        int entryCount = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            addIndent(jsonBuilder, currentIndent + indent);
            jsonBuilder.append("\"").append(entry.getKey()).append("\": ");
            Object value = entry.getValue();

            if (value instanceof Map) {
                // Recursive call for nested maps
                buildJsonFromMap((Map<String, Object>) value, jsonBuilder, currentIndent + indent, indent);
            } else if (value instanceof List) {
                // Recursive call for lists
                buildJsonFromList((List<Object>) value, jsonBuilder, currentIndent + indent, indent);
            } else if (value instanceof String) {
                // String values
                jsonBuilder.append("\"").append(value).append("\"");
            } else if (value instanceof Number) {
                // Number values
                jsonBuilder.append(value);
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
            }

            entryCount++;
            if (entryCount < map.size()) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        addIndent(jsonBuilder, currentIndent);
        jsonBuilder.append("}");
    }

    private static void buildJsonFromList(List<Object> list, StringBuilder jsonBuilder, int currentIndent, int indent) {
        jsonBuilder.append("[\n");
        int elementCount = 0;
        for (Object element : list) {
            addIndent(jsonBuilder, currentIndent + indent);

            if (element instanceof Map) {
                // Recursive call for nested maps
                buildJsonFromMap((Map<String, Object>) element, jsonBuilder, currentIndent + indent, indent);
            } else if (element instanceof List) {
                // Recursive call for nested lists
                buildJsonFromList((List<Object>) element, jsonBuilder, currentIndent + indent, indent);
            } else if (element instanceof String) {
                // String values
                jsonBuilder.append("\"").append(element).append("\"");
            } else if (element instanceof Number) {
                // Number values
                jsonBuilder.append(element);
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + element.getClass());
            }

            elementCount++;
            if (elementCount < list.size()) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        addIndent(jsonBuilder, currentIndent);
        jsonBuilder.append("]");
    }

    private static void addIndent(StringBuilder jsonBuilder, int indent) {
        for (int i = 0; i < indent; i++) {
            jsonBuilder.append(" ");
        }
    }
     public static void saveToJson(String filePath, String jsonString) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonString);
        }
    }

}
