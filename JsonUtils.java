import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for converting Java Maps (potentially containing nested Maps/Lists)
 * to a formatted JSON string and saving it to a file.
 */
public class JsonUtils {

    /**
     * Converts a nested map into a JSON string with a specified indentation.
     *
     * @param map    The input map (can contain nested maps, strings, or numbers).
     * @param indent The number of spaces to use for indentation in the resulting JSON.
     * @return The formatted JSON string representation of the input map.
     */
    public static String mapToJson(Map<String, Object> map, int indent) {
        // StringBuilder is used for efficient string concatenation.
        StringBuilder jsonBuilder = new StringBuilder();
        
        // Recursively build the JSON string from the map.
        // The initial currentIndent is 0 (no indentation at the start).
        buildJsonFromMap(map, jsonBuilder, 0, indent);
        
        // Convert the StringBuilder to a string and return.
        return jsonBuilder.toString();
    }

    /**
     * Recursively builds the JSON representation from a Map and appends it to the given StringBuilder.
     *
     * @param map           The current map to process.
     * @param jsonBuilder   The StringBuilder that accumulates the JSON output.
     * @param currentIndent The current indentation level.
     * @param indent        The number of spaces to increase per indentation level.
     */
    private static void buildJsonFromMap(Map<String, Object> map, StringBuilder jsonBuilder, int currentIndent, int indent) {
        // Start object scope
        jsonBuilder.append("{\n");
        
        // We'll use entryCount to determine if we need a trailing comma after each key-value pair.
        int entryCount = 0;
        
        // Iterate through each entry in the map.
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Indent and then print the key in quotes.
            addIndent(jsonBuilder, currentIndent + indent);
            jsonBuilder.append("\"").append(entry.getKey()).append("\": ");
            
            // Retrieve the value for further inspection.
            Object value = entry.getValue();

            // Check the type of the value to handle it appropriately.
            if (value instanceof Map) {
                // Handle nested maps by making a recursive call.
                buildJsonFromMap((Map<String, Object>) value, jsonBuilder, currentIndent + indent, indent);
            } else if (value instanceof List) {
                // Handle lists (possibly nested) by calling buildJsonFromList.
                buildJsonFromList((List<Object>) value, jsonBuilder, currentIndent + indent, indent);
            } else if (value instanceof String) {
                // String values need to be enclosed in quotes.
                jsonBuilder.append("\"").append(value).append("\"");
            } else if (value instanceof Number) {
                // Number values can be appended directly (no quotes).
                jsonBuilder.append(value);
            } else {
                // If we encounter any unknown type, we throw an exception.
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
            }

            // If this is not the last entry, add a comma.
            entryCount++;
            if (entryCount < map.size()) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        
        // Close the object scope with appropriate indentation.
        addIndent(jsonBuilder, currentIndent);
        jsonBuilder.append("}");
    }

    /**
     * Recursively builds the JSON representation from a List and appends it to the given StringBuilder.
     *
     * @param list          The current list to process.
     * @param jsonBuilder   The StringBuilder that accumulates the JSON output.
     * @param currentIndent The current indentation level.
     * @param indent        The number of spaces to increase per indentation level.
     */
    private static void buildJsonFromList(List<Object> list, StringBuilder jsonBuilder, int currentIndent, int indent) {
        // Start array scope
        jsonBuilder.append("[\n");
        
        // We use elementCount to determine if we need a trailing comma after each element.
        int elementCount = 0;
        
        // Iterate over each element in the list.
        for (Object element : list) {
            // Indent for better readability.
            addIndent(jsonBuilder, currentIndent + indent);

            // Check the type of the list element and process accordingly.
            if (element instanceof Map) {
                // If the element is a map, recurse using the buildJsonFromMap method.
                buildJsonFromMap((Map<String, Object>) element, jsonBuilder, currentIndent + indent, indent);
            } else if (element instanceof List) {
                // If the element is another list, recurse using the buildJsonFromList method.
                buildJsonFromList((List<Object>) element, jsonBuilder, currentIndent + indent, indent);
            } else if (element instanceof String) {
                // String elements must be enclosed in quotes.
                jsonBuilder.append("\"").append(element).append("\"");
            } else if (element instanceof Number) {
                // Number elements can be appended directly.
                jsonBuilder.append(element);
            } else {
                // If we encounter an unknown type, throw an exception.
                throw new IllegalArgumentException("Unsupported value type: " + element.getClass());
            }

            // If this is not the last element in the list, add a comma.
            elementCount++;
            if (elementCount < list.size()) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        
        // Close the array scope with appropriate indentation.
        addIndent(jsonBuilder, currentIndent);
        jsonBuilder.append("]");
    }

    /**
     * Helper method to add a specific number of spaces (for indentation) to the StringBuilder.
     *
     * @param jsonBuilder The StringBuilder to append indentation to.
     * @param indent      The number of spaces to append.
     */
    private static void addIndent(StringBuilder jsonBuilder, int indent) {
        // Append 'indent' number of space characters.
        for (int i = 0; i < indent; i++) {
            jsonBuilder.append(" ");
        }
    }

    /**
     * Saves the provided JSON string to a file at the specified file path.
     *
     * @param filePath   The path (including file name) where the JSON should be saved.
     * @param jsonString The JSON content to be written to the file.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public static void saveToJson(String filePath, String jsonString) throws IOException {
        // Use try-with-resources to ensure FileWriter is closed automatically.
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonString);
        }
    }

}
