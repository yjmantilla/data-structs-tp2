package com.ift2015.tp2;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class InputParser {

    public static class ParsedData {
        public List<EmergencySupplyNetwork.City> cities = new ArrayList<>();
        public List<EmergencySupplyNetwork.Warehouse> warehouses = new ArrayList<>();
    }

    public static ParsedData parseInput(String filePath) throws IOException {
        ParsedData data = new ParsedData();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isCitySection = false;
            boolean isWarehouseSection = false;

            Pattern cityPattern = Pattern.compile(
                    "City (\\w+): ID = (\\d+), Coordinates = \\((\\d+), (\\d+)\\), Demand = (\\d+) units, Priority = (\\w+)");
            Pattern warehousePattern = Pattern.compile(
                    "Warehouse (\\w+): ID = (\\d+), Coordinates = \\((\\d+), (\\d+)\\), Capacity = (\\d+) units");

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Cities:")) {
                    isCitySection = true;
                    isWarehouseSection = false;
                    continue;
                }

                if (line.startsWith("Warehouses:")) {
                    isWarehouseSection = true;
                    isCitySection = false;
                    continue;
                }

                if (isCitySection) {
                    Matcher matcher = cityPattern.matcher(line);
                    if (matcher.matches()) {
                        String name = matcher.group(1);
                        int id = Integer.parseInt(matcher.group(2));
                        int x = Integer.parseInt(matcher.group(3));
                        int y = Integer.parseInt(matcher.group(4));
                        int demand = Integer.parseInt(matcher.group(5));
                        String priority = matcher.group(6);

                        EmergencySupplyNetwork.Priority priorityEnum = EmergencySupplyNetwork.Priority.valueOf(priority.toUpperCase());
                        EmergencySupplyNetwork.City city = new EmergencySupplyNetwork.City(id, x, y, demand, priorityEnum, name);
                        data.cities.add(city);
                    }
                }

                if (isWarehouseSection) {
                    Matcher matcher = warehousePattern.matcher(line);
                    if (matcher.matches()) {
                        String name = matcher.group(1);
                        int id = Integer.parseInt(matcher.group(2));
                        int x = Integer.parseInt(matcher.group(3));
                        int y = Integer.parseInt(matcher.group(4));
                        int capacity = Integer.parseInt(matcher.group(5));

                        EmergencySupplyNetwork.Warehouse warehouse = new EmergencySupplyNetwork.Warehouse(id, x, y, capacity, name);
                        data.warehouses.add(warehouse);
                    }
                }
            }
        }

        return data;
    }
}
