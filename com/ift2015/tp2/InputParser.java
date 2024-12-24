package com.ift2015.tp2;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * The InputParser class is responsible for reading and parsing a text-based input file
 * which contains information about cities and warehouses. This class utilizes regular
 * expressions (Patterns) to extract the relevant data fields for each city and warehouse.
 */
public class InputParser {

    /**
     * A simple container class to hold the parsed city and warehouse data.
     */
    public static class ParsedData {
        /**
         * A list of City objects extracted from the input file.
         */
        public List<EmergencySupplyNetwork.City> cities = new ArrayList<>();

        /**
         * A list of Warehouse objects extracted from the input file.
         */
        public List<EmergencySupplyNetwork.Warehouse> warehouses = new ArrayList<>();
    }

    /**
     * Parses the input file at the specified path to extract city and warehouse data.
     *
     * @param filePath The path to the input file that needs to be parsed.
     * @return A ParsedData object containing lists of cities and warehouses.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static ParsedData parseInput(String filePath) throws IOException {
        // Create a new instance of ParsedData to store the results.
        ParsedData data = new ParsedData();

        // Use a try-with-resources to ensure the file reader is closed properly.
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // These booleans track which section of the file we are currently parsing.
            boolean isCitySection = false;
            boolean isWarehouseSection = false;

            // Compile regular expressions to match city and warehouse lines respectively.
            Pattern cityPattern = Pattern.compile(
                    "City (\\w+): ID = (\\d+), Coordinates = \\((\\d+), (\\d+)\\), Demand = (\\d+) units, Priority = (\\w+)");
            Pattern warehousePattern = Pattern.compile(
                    "Warehouse (\\w+): ID = (\\d+), Coordinates = \\((\\d+), (\\d+)\\), Capacity = (\\d+) units");

            // Read the file line by line.
            while ((line = br.readLine()) != null) {
                // Remove leading/trailing whitespace.
                line = line.trim();

                // Check if we have encountered the start of the "Cities" section.
                if (line.startsWith("Cities:")) {
                    isCitySection = true;
                    isWarehouseSection = false;
                    continue;
                }

                // Check if we have encountered the start of the "Warehouses" section.
                if (line.startsWith("Warehouses:")) {
                    isWarehouseSection = true;
                    isCitySection = false;
                    continue;
                }

                // If we are in the cities section, match each line against the city pattern.
                if (isCitySection) {
                    Matcher matcher = cityPattern.matcher(line);
                    if (matcher.matches()) {
                        // Extract values from the matched groups.
                        String name = matcher.group(1);
                        int id = Integer.parseInt(matcher.group(2));
                        int x = Integer.parseInt(matcher.group(3));
                        int y = Integer.parseInt(matcher.group(4));
                        int demand = Integer.parseInt(matcher.group(5));
                        String priority = matcher.group(6);

                        // Convert the priority string to the corresponding enum value.
                        EmergencySupplyNetwork.Priority priorityEnum =
                                EmergencySupplyNetwork.Priority.valueOf(priority.toUpperCase());

                        // Create a new city object and add it to the data.
                        EmergencySupplyNetwork.City city =
                                new EmergencySupplyNetwork.City(id, x, y, demand, priorityEnum, name);
                        data.cities.add(city);
                    }
                }

                // If we are in the warehouses section, match each line against the warehouse pattern.
                if (isWarehouseSection) {
                    Matcher matcher = warehousePattern.matcher(line);
                    if (matcher.matches()) {
                        // Extract values from the matched groups.
                        String name = matcher.group(1);
                        int id = Integer.parseInt(matcher.group(2));
                        int x = Integer.parseInt(matcher.group(3));
                        int y = Integer.parseInt(matcher.group(4));
                        int capacity = Integer.parseInt(matcher.group(5));

                        // Create a new warehouse object and add it to the data.
                        EmergencySupplyNetwork.Warehouse warehouse =
                                new EmergencySupplyNetwork.Warehouse(id, x, y, capacity, name);
                        data.warehouses.add(warehouse);
                    }
                }
            }
        }

        // Return the populated ParsedData object.
        return data;
    }
}
