package com.ift2015.tp2;
import java.io.IOException;
// NetworkApp.java
import java.util.*;

import com.ift2015.tp2.EmergencySupplyNetwork.City;
import com.ift2015.tp2.EmergencySupplyNetwork.ResourceAllocation;


public class NetworkApp {
    public static void main(String[] args) {


        String inputFilePath = "TestCase0.txt";

        try {
            InputParser.ParsedData parsedData = InputParser.parseInput(inputFilePath);

            List<EmergencySupplyNetwork.City> cities = parsedData.cities;
            List<EmergencySupplyNetwork.Warehouse> warehouses = parsedData.warehouses;


            System.out.println("");
            System.out.println("");

            System.out.println("Parsed Cities:"+ cities.size());
            for (City city : cities) {
                System.out.println("City " + city.id + " Priority: " + city.priority);
            }
            System.out.println("");

            System.out.println("Parsed warehouses: " + warehouses.size());
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("Warehouse " + warehouse.id + " Capacity: " + warehouse.capacity);
            }
            System.out.println("");
            System.out.println("");

            // Task 1 & 2: Graph and Resource Allocation
            EmergencySupplyNetwork network = new EmergencySupplyNetwork(cities, warehouses);
            double[][] costMatrix = network.getCostMatrix();

            System.out.println("Graph Representation (Cost Matrix):");
            // for (double[] row : costMatrix) {
            //     System.out.println(Arrays.toString(row));
            // }
            
            int numChars=0;
            numChars+="cities    |".length();
            for (int i = 0; i < warehouses.size(); i++) {
                numChars += (" Warehouse " + warehouses.get(i).id + " |").length();
            }
            String separator = "-".repeat(numChars);
            System.out.println(separator);
            // Print header
            System.out.print("cities    |");
            for (int i = 0; i < warehouses.size(); i++) {
                System.out.printf(" Warehouse %d |", warehouses.get(i).id);
            }
            System.out.println();

            // Print separator
            System.out.println(separator);
            // Print rows
            for (int i = 0; i < cities.size(); i++) {
                int spaces = 5;//(" City " + cities.get(i).id + " |").length();
                System.out.printf("City %-"+spaces+"d|", cities.get(i).id); // Align city IDs
                for (int j = 0; j < warehouses.size(); j++) {
                    spaces = (" Warehouse " + warehouses.get(j).id + " |").length()-2;
                    System.out.printf(" %-"+ spaces +".2f|", costMatrix[i][j]); // Format values to 2 decimal places
                }
                System.out.println(); // New line after each row
            }

            // Print footer line
            System.out.println(separator);
            System.out.println("");
            System.out.println("");

            Map<EmergencySupplyNetwork.City, List<EmergencySupplyNetwork.ResourceAllocation>> allocations = network.allocateResources();
            //System.out.println("Resource Allocations:");
            // for (Map.Entry<EmergencySupplyNetwork.City, List<EmergencySupplyNetwork.ResourceAllocation>> entry : allocations.entrySet()) {
            //     System.out.println("City " + entry.getKey().id + " Allocations: " + entry.getValue());
            // }

            // System.out.println("Resource Allocations:");
            // for (Map.Entry<EmergencySupplyNetwork.City, List<EmergencySupplyNetwork.ResourceAllocation>> entry : allocations.entrySet()) {
            //     EmergencySupplyNetwork.City city = entry.getKey();
            //     List<EmergencySupplyNetwork.ResourceAllocation> allocationsForCity = entry.getValue();

            //     System.out.println("Allocating resources for City " + city.id + " (Priority: " + city.priority + ")");
            //     for (EmergencySupplyNetwork.ResourceAllocation allocation : allocationsForCity) {
            //         System.out.println("  Allocated " + allocation.units + " units from Warehouse " + allocation.warehouseId);
            //     }
            // }
            System.out.println("");
            System.out.println("");

            System.out.println("Remaining Warehouse Capacities:");
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
            }

            // Task 3: Resource Redistribution
            
            ResourceRedistribution redistribution = new ResourceRedistribution(warehouses);

            //ResourceRedistribution redistribution = new ResourceRedistribution(warehouses);
            List<ResourceRedistribution.Transfer> transfers = redistribution.redistributeResources();
            System.out.println("");
            System.out.println("");
            System.out.println("Resource Transfers:");
            for (ResourceRedistribution.Transfer transfer : transfers) {
                System.out.println(transfer);
            }
            System.out.println("");
            System.out.println("");

            System.out.println("Final Resource Levels:");
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
            }

            // Task 4: Dynamic Resource Sharing

            Map<Integer, List<Integer>> cityToWarehouses = new HashMap<>();
            Map<Integer, String> cityToName = new HashMap<>();
            for (EmergencySupplyNetwork.City city : cities) {
                List<Integer> warehouseIds = new ArrayList<>();
                cityToName.put(city.id, city.name);

                List<ResourceAllocation> cityAllocations = allocations.get(city);

                if (cityAllocations != null) {
                    for (ResourceAllocation allocation : cityAllocations) {
                        // Access properties of each ResourceAllocation
                        // System.out.println("Warehouse ID: " + allocation.warehouse);
                        // System.out.println("Allocated Units: " + allocation.units);
                        warehouseIds.add(allocation.warehouse.id);
                    }
                } else {
                    System.out.println("No allocations found for city: " + city.id);
                }

                cityToWarehouses.put(city.id, warehouseIds);
            }
            
            DynamicResourceSharing sharing = new DynamicResourceSharing(cityToWarehouses);

            // Print initial clusters
            System.out.println("");
            System.out.println("");

            System.out.println("Initial Clusters:");
            for (int cityId : cityToWarehouses.keySet()) {
                System.out.println("City " + cityToName.get(cityId) + " belongs to cluster: " + sharing.find(cityId));
            }

            // Perform unions based on shared resources
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) {
                    if (sharing.shareResources(cities.get(i).id, cities.get(j).id)) {
                        System.out.println("Merging clusters of City " + cities.get(i).id + " and City " + cities.get(j).id + "...");
                        sharing.union(cities.get(i).id, cities.get(j).id);
                    }
                }
            }

            // Print updated clusters
            System.out.println("");
            System.out.println("");

            System.out.println("Updated Clusters:");
            for (int cityId : cityToWarehouses.keySet()) {
                System.out.println("City " + cityToName.get(cityId) + " belongs to cluster: " + sharing.find(cityId));
            }

            System.out.println("");
            System.out.println("");
            System.out.println("Querying if cities are in the same cluster:");
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) { // Start from i + 1 to avoid redundant checks
                    EmergencySupplyNetwork.City city1 = cities.get(i);
                    EmergencySupplyNetwork.City city2 = cities.get(j);
            
                    // Perform the query
                    System.out.println("Query: Are City " + city1.id + " and City " + city2.id + " in the same cluster?");
                    System.out.println(sharing.areInSameCluster(city1.id, city2.id) ? "Yes" : "No");
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
