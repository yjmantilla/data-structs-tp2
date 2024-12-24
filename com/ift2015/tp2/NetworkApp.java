package com.ift2015.tp2;
import java.io.IOException;
// NetworkApp.java
import java.util.*;

import com.ift2015.tp2.EmergencySupplyNetwork.City;
import com.ift2015.tp2.EmergencySupplyNetwork.ResourceAllocation;

/**
 * The NetworkApp class is the main entry point for the Emergency Supply Network application.
 * It reads input data from a file, processes the data to create a network of cities and warehouses,
 * performs resource allocation, redistribution, and dynamic resource sharing tasks, and outputs the results
 * in both console and JSON format.
 *
 * Usage: java NetworkApp <inputFilePath>
 * If no input file path is provided, the default file "TestCase1.txt" is used.
 *
 * The main tasks performed by this application are:
 *
 *   Task 1 & 2: Graph and Resource Allocation
 *   Task 3: Resource Redistribution
 *   Task 4: Dynamic Resource Sharing
 * 
 *
 * Each task involves parsing input data, performing computations, and generating output in JSON format.
 *
 * Exceptions are handled to ensure that errors during file reading or JSON writing are reported to the console.
 */
public class NetworkApp {
    public static void main(String[] args) {

        System.out.println("IFT2015 - TP2: Emergency Supply Network");
        String inputFilePath = "TestCase1.txt";

        // Check if the file path is provided as an argument
        if (args.length < 1) { // No arguments provided, use default file path
            System.out.println("Usage: java NetworkApp <inputFilePath>");
            System.out.println("Using default file path: TestCase1.txt");
        }
        else{ // Use the first argument as the file path
            inputFilePath = args[0]; // Use the first argument as the file path
            System.out.println("Using file path: " + inputFilePath);
        }


        try {
            // Parse the input file
            InputParser.ParsedData parsedData = InputParser.parseInput(inputFilePath);

            // To make the json, we will create nested LinkedHashMaps and Lists
            // LinkedHashMaps will be used to represent JSON objects to maintain insertion order
            List<EmergencySupplyNetwork.City> cities = parsedData.cities;
            List<EmergencySupplyNetwork.Warehouse> warehouses = parsedData.warehouses;
            LinkedHashMap<String,Object> jsonMap = new LinkedHashMap<>();
            LinkedHashMap<String,Object> task1_2 = new LinkedHashMap<>();
            LinkedHashMap<String,Object> task3 = new LinkedHashMap<>();
            LinkedHashMap<String,Object> task4 = new LinkedHashMap<>();
            LinkedHashMap<String,Object> graphRepresentation = new LinkedHashMap<>();
            LinkedHashMap<String,Object> redisMap = new LinkedHashMap<>();

            // There will be many newlines to separate the different sections of the output in the console
            System.out.println("");
            System.out.println("");

            // Print the parsed cities and warehouses to check if the input was read correctly
            System.out.println("Parsed Cities:"+ cities.size());
            for (City city : cities) {
                System.out.println("City " + city.name + " Priority: " + city.priority);
            }
            System.out.println("");

            System.out.println("Parsed warehouses: " + warehouses.size());
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("Warehouse " + warehouse.name + " Capacity: " + warehouse.capacity);
            }
            System.out.println("");
            System.out.println("");

            ////////////////////////////////////////////////
            // Task 1 & 2: Graph and Resource Allocation //
            //////////////////////////////////////////////
            
            EmergencySupplyNetwork network = new EmergencySupplyNetwork(cities, warehouses); // Create the network
            double[][] costMatrix = network.getCostMatrix(); // Get the cost matrix

            // Print the graph representation (cost matrix) to the console
            // This is a very simple representation
            // may not be the most readable for large graphs
            System.out.println("Graph Representation (Cost Matrix):");

            // Calculate the number of characters needed to print one row of the cost matrix
            // This is used to print a separator line
            int numChars=0;
            numChars+="cities    |".length();
            for (int i = 0; i < warehouses.size(); i++) {
                numChars += (" Warehouse " + warehouses.get(i).id + " |").length();
            }
            String separator = "-".repeat(numChars);
            System.out.println(separator);

            // Print header
            System.out.print("cities     |");
            for (int i = 0; i < warehouses.size(); i++) {
                System.out.printf(" Warehouse %d |", warehouses.get(i).id);
            }
            System.out.println();

            // Another separator line
            System.out.println(separator);

            // Print rows (cities) of the cost matrix
            List<Object> costArray = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                int spaces = 5; // Default spaces for city names, this is hardcoded, but could be adjusted based on the city names
                System.out.print("City "+cities.get(i).name+" ".repeat(spaces)+"|"); // Print the city name

                Map<String,Object> thiscost = new LinkedHashMap<>(); // Create a map to store the cost matrix (warehouse costs for each city) for the json
                thiscost.put("City",cities.get(i).name);  // Add the city ID to the map

                // Print the cost matrix values for each warehouse
                for (int j = 0; j < warehouses.size(); j++) {
                    spaces = (" Warehouse " + warehouses.get(j).id + " |").length()-2; // -2 was hardcoded visually
                    System.out.printf(" %-"+ spaces +".2f|", costMatrix[i][j]); // Format values to 2 decimal places
                    String twodec = String.format("%.2f",costMatrix[i][j]);
                    float val = Float.parseFloat(twodec);
                    thiscost.put("Warehouse "+warehouses.get(j).id,val); // Add the cost to the map for the json
                }
                costArray.add(thiscost); // Add the cost matrix for this city to the list for the json
                System.out.println(); // New line after each row
            }

            graphRepresentation.put("Cost Matrix",costArray); // Add the cost matrix to graphRepresentation for the json
            task1_2.put("Graph Representation",graphRepresentation); // Add the graphRepresentation to task1_2 for the json

            // Print footer line
            System.out.println(separator);
            System.out.println("");
            System.out.println("");


            Map<EmergencySupplyNetwork.City, List<EmergencySupplyNetwork.ResourceAllocation>> allocations = network.allocateResources();

            // Print the resource allocations to the console
            System.out.println("");
            System.out.println("");

            // Resource allocations for the json
            List<Object> allocationArray = new ArrayList<>();

            // Each entry in the allocations list is a map that contains the city, priority, and the warehouse and units allocated
            // if there is only one warehouse allocated, it is a flat map
            // if there are multiple warehouses allocated, the allocations will be a list of maps

            for (Map.Entry<EmergencySupplyNetwork.City, List<EmergencySupplyNetwork.ResourceAllocation>> entry : allocations.entrySet()) {

                Map<String,Object> thisAllocation = new LinkedHashMap<>(); // Create a map to store the resource allocations of this city for the json

                thisAllocation.put("City",entry.getKey().name); // Add the city ID to the map

                // Deal with the priority. Originally it was Capital Case, so we will capitalize the first letter and lowercase the rest
                // as internally we used ALL CAPS for the priority

                String capcase = entry.getKey().priority.name();
                // Capitalize the first letter of the priority, and lowercase the rest
                String priority = capcase.substring(0, 1).toUpperCase() + capcase.substring(1).toLowerCase();
                thisAllocation.put("Priority",priority); // Add the priority to the map
                
                // Determine if the allocation is a flat map or a list of maps
                if (entry.getValue().size() == 1){ // If there is only one allocation
                    thisAllocation.put("Allocated", entry.getValue().get(0).units); // Add the units allocated to the map
                    thisAllocation.put("Warehouse", "Warehouse "+entry.getValue().get(0).warehouse.id); // Add the warehouse ID to the map
                }
                else{ // If there are multiple allocations
                    List<Object> allocs = new ArrayList<>();    // Create a list to store the allocations for this city
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        Map<String,Object> thisAlloc = new LinkedHashMap<>();   // Create a map to store the allocation for this warehouse
                        thisAlloc.put("Units", entry.getValue().get(i).units); // Add the units allocated to the map
                        thisAlloc.put("Warehouse", "Warehouse "+entry.getValue().get(i).warehouse.id); // Add the warehouse ID to the map
                        allocs.add(thisAlloc); // Add the allocation to the list
                    }
                    thisAllocation.put("Allocated", allocs); // Add the list of allocations to the map
                }
                allocationArray.add(thisAllocation); // Add the resource allocations for this city to the list for the json
            }


            Map<String,Object> remCap = new LinkedHashMap<>(); // Create a map to store the remaining capacities of the warehouses for the json
            System.out.println("Remaining Warehouse Capacities:");
            // Print the remaining capacities of the warehouses
            // And make the Remaining Capacities map for the json
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
                remCap.put("Warehouse "+warehouse.id,warehouse.remainingCapacity);
            }

            task1_2.put("Resource Allocation",allocationArray); // Add the resource allocations to task1_2 for the json
            task1_2.put("Remaining Capacities",remCap); // Add the remaining capacities to task1_2 for the json
            jsonMap.put("Task 1 and 2",task1_2);  // Add task1_2 to the jsonMap

            //////////////////////////////////////
            // Task 3: Resource Redistribution //
            ////////////////////////////////////
            
            // Create a ResourceRedistribution object
            ResourceRedistribution redistribution = new ResourceRedistribution(warehouses);

            // Perform the resource redistribution
            List<ResourceRedistribution.Transfer> transfers = redistribution.redistributeResources();

            // Print the resource transfers to the console
            System.out.println("");
            System.out.println("");
            System.out.println("Resource Transfers:");
            for (ResourceRedistribution.Transfer transfer : transfers) {
                System.out.println(transfer);
            }
            System.out.println("");
            System.out.println("");

            // Resource transfers for the json
            List<Object> transferArray = new ArrayList<>();
            for (int i = 0; i < transfers.size(); i++) {
                ResourceRedistribution.Transfer transfer = transfers.get(i);
                Map<String,Object> thisTransfer = new LinkedHashMap<>();
                thisTransfer.put("From","Warehouse "+transfer.fromWarehouse.name);
                thisTransfer.put("To","Warehouse "+transfer.toWarehouse.name);
                thisTransfer.put("Units",transfer.units);
                transferArray.add(thisTransfer);
            }


            // Final resource levels for the json and console
            System.out.println("Final Resource Levels:");
            Map<String,Object> finalCap = new LinkedHashMap<>();
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
                finalCap.put("Warehouse "+warehouse.id,warehouse.remainingCapacity); // Add the remaining capacity to the map for the json
            }

            redisMap.put("Transfers",transferArray); // Add the transfers to the map for the json
            redisMap.put("Final Resource Levels",finalCap); // Add the final resource levels to the map for the json
            task3.put("Resource Redistribution",redisMap); // Add the resource redistribution to task3 for the json

            jsonMap.put("Task 3",task3); // Add task3 to the jsonMap

            ///////////////////////////////////////
            // Task 4: Dynamic Resource Sharing //
            /////////////////////////////////////

            Map<Integer, List<Integer>> cityToWarehouses = new HashMap<>(); // Map city ID to list of warehouse IDs
            Map<Integer, String> cityToName = new HashMap<>(); // Map city ID to city name (this is auxiliary)

            // Fill the cityToWarehouses map and cityToName map
            for (EmergencySupplyNetwork.City city : cities) {
                List<Integer> warehouseIds = new ArrayList<>();
                cityToName.put(city.id, city.name);

                List<ResourceAllocation> cityAllocations = allocations.get(city);

                if (cityAllocations != null) {
                    for (ResourceAllocation allocation : cityAllocations) {
                        warehouseIds.add(allocation.warehouse.id);
                    }
                } else {
                    System.out.println("No allocations found for city: " + city.id);
                }
                cityToWarehouses.put(city.id, warehouseIds);
            }
            

            // Create a DynamicResourceSharing object
            DynamicResourceSharing sharing = new DynamicResourceSharing(cityToWarehouses);
            LinkedHashMap<String,Object> DRSmap = new LinkedHashMap<>(); // DRS=Dynamic Resource Sharing for the json

            // Print initial clusters
            System.out.println("");
            System.out.println("");

            LinkedHashMap<String,Object> initClusters = new LinkedHashMap<>(); // Create a map to store the initial clusters for the json
            System.out.println("Initial Clusters:");
            for (int cityId : cityToWarehouses.keySet()) {
                System.out.println("City " + cityToName.get(cityId) + " belongs to cluster: " + sharing.find(cityId));
                initClusters.put("City "+cityToName.get(cityId),"Cluster "+sharing.find(cityId)); // Add the cluster membership to the map for the json
            }
            DRSmap.put("Initial Clusters",initClusters); // Add the initial clusters to the DRSmap for the json


            System.out.println("");
            System.out.println("");

            // Perform unions based on shared resources
            ArrayList<Object> steps = new ArrayList<>(); // Create a list to store the merging steps for the json

            // Perform unions based on shared resources
            // Iterate over all pairs of cities
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) {

                    if (sharing.shareResources(cities.get(i).id, cities.get(j).id)) { // Check if cities share resources
                        System.out.println("Merging clusters of City " + cities.get(i).name + " and City " + cities.get(j).name + "...");
                        sharing.union(cities.get(i).id, cities.get(j).id); // Merge the clusters
                        Map<String,Object> thisStep = new LinkedHashMap<>();
                        thisStep.put("Action","Merge"); // Add the action to the map for the json
                        thisStep.put("Cities",List.of("City "+cities.get(i).name,"City "+cities.get(j).name)); // Add the cities to the map for the json
                        thisStep.put("Cluster After Merge","Cluster "+sharing.find(cities.get(i).id)); // Add the cluster after the merge to the map for the json
                        steps.add(thisStep); // Add the merging step to the list for the json
                    }
                }
            }
            DRSmap.put("Merging Steps",steps); // Add the merging steps to the DRSmap for the json

            // Print updated clusters
            System.out.println("");
            System.out.println("");

            LinkedHashMap<String,Object> finalClusters = new LinkedHashMap<>(); // Create a map to store the final clusters for the json
            System.out.println("Updated Clusters:"); // Print the updated clusters
            for (int cityId : cityToWarehouses.keySet()) {
                System.out.println("City " + cityToName.get(cityId) + " belongs to cluster: " + sharing.find(cityId));
                finalClusters.put("City "+cityToName.get(cityId),"Cluster "+sharing.find(cityId)); // Add the cluster membership to the map for the json
            }
            DRSmap.put("Cluster Membership After Merging",finalClusters); // Add the final clusters to the DRSmap for the json


            // Perform queries
            System.out.println("");
            System.out.println("");
            System.out.println("Querying if cities are in the same cluster:");
            ArrayList<Object> queries = new ArrayList<>(); // Create a list to store the queries for the json

            // Combine all pairs of cities and check if they are in the same cluster
            // Print in console and store the results in the json
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) { // Start from i + 1 to avoid redundant checks
                    EmergencySupplyNetwork.City city1 = cities.get(i);
                    EmergencySupplyNetwork.City city2 = cities.get(j);
            
                    // Perform the query
                    LinkedHashMap<String,Object> thisQuery = new LinkedHashMap<>(); // Create a map to store the query and result for the json
                    System.out.println("Query: Are City " + city1.name + " and City " + city2.name + " in the same cluster?");
                    thisQuery.put("Query","Are City "+city1.name+" and City "+city2.name+" in the same cluster?"); // Add the query to the map for the json
                    System.out.println(sharing.areInSameCluster(city1.id, city2.id) ? "Yes" : "No");
                    thisQuery.put("Result",sharing.areInSameCluster(city1.id, city2.id) ? "Yes" : "No"); // Add the result to the map for the json
                    queries.add(thisQuery); // Add the query to the list for the json
                }
            }
            DRSmap.put("Queries",queries); // Add the queries to the DRSmap for the json

            task4.put("Dynamic Resource Sharing",DRSmap); // Add the DRSmap to task4 for the json
            jsonMap.put("Task 4",task4); // Add task4 to the jsonMap


            // Write JSON output to file
            try {
                String jsonOutput = JsonUtils.mapToJson(jsonMap, 4); // Convert the jsonMap to a JSON string with 4 spaces for indentation
                String outputFilePath = "Output_"+ inputFilePath.replace(".txt", ".json"); // Change the file extension to .json
                System.out.println("Saving results to " + outputFilePath); // Print the output file path
                JsonUtils.saveToJson(outputFilePath, jsonOutput); // Save the JSON to the output file
                System.out.println(jsonOutput); // Print the JSON to the console
                System.out.println("Results saved to " + outputFilePath); // Print a message indicating that the results were saved
            } catch (IOException e) {
                System.err.println("Error saving JSON: " + e.getMessage());
            }
            
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
