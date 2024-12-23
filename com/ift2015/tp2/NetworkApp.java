package com.ift2015.tp2;
import java.io.IOException;
// NetworkApp.java
import java.util.*;

import com.ift2015.tp2.EmergencySupplyNetwork.City;
import com.ift2015.tp2.EmergencySupplyNetwork.ResourceAllocation;

public class NetworkApp {
    public static void main(String[] args) {

        System.out.println("IFT2015 - TP2: Emergency Supply Network");
        String inputFilePath = "TestCase1.txt";

        // Check if the file path is provided as an argument
        if (args.length < 1) {
            System.out.println("Usage: java NetworkApp <inputFilePath>");
            System.out.println("Using default file path: TestCase1.txt");
        }
        else{
            inputFilePath = args[0]; // Use the first argument as the file path
            System.out.println("Using file path: " + inputFilePath);
        }


        try {
            InputParser.ParsedData parsedData = InputParser.parseInput(inputFilePath);

            List<EmergencySupplyNetwork.City> cities = parsedData.cities;
            List<EmergencySupplyNetwork.Warehouse> warehouses = parsedData.warehouses;
            LinkedHashMap<String,Object> jsonMap = new LinkedHashMap<>();
            LinkedHashMap<String,Object> task1_2 = new LinkedHashMap<>();
            LinkedHashMap<String,Object> task3 = new LinkedHashMap<>();
            LinkedHashMap<String,Object> task4 = new LinkedHashMap<>();
            LinkedHashMap<String,Object> graphRepresentation = new LinkedHashMap<>();
            LinkedHashMap<String,Object> redisMap = new LinkedHashMap<>();

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
            List<Object> costArray = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                int spaces = 5;//(" City " + cities.get(i).id + " |").length();
                System.out.printf("City %-"+spaces+"d|", cities.get(i).id); // Align city IDs
                Map<String,Object> thiscost = new LinkedHashMap<>();
                thiscost.put("City","City "+cities.get(i).id);
                for (int j = 0; j < warehouses.size(); j++) {
                    spaces = (" Warehouse " + warehouses.get(j).id + " |").length()-2;
                    System.out.printf(" %-"+ spaces +".2f|", costMatrix[i][j]); // Format values to 2 decimal places
                    String twodec = String.format("%.2f",costMatrix[i][j]);
                    float val = Float.parseFloat(twodec);
                    thiscost.put("Warehouse "+warehouses.get(j).id,val);
                }
                costArray.add(thiscost);
                System.out.println(); // New line after each row
            }

            graphRepresentation.put("Cost Matrix",costArray);
            task1_2.put("Graph Representation",graphRepresentation);

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

            Map<String,Object> remCap = new LinkedHashMap<>();
            System.out.println("Remaining Warehouse Capacities:");
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
                remCap.put("Warehouse "+warehouse.id,warehouse.remainingCapacity);
            }

            List<Object> allocationArray = new ArrayList<>();

            for (Map.Entry<EmergencySupplyNetwork.City, List<EmergencySupplyNetwork.ResourceAllocation>> entry : allocations.entrySet()) {
                Map<String,Object> thisAllocation = new LinkedHashMap<>();
                thisAllocation.put("City","City "+entry.getKey().id);
                String capcase = entry.getKey().priority.name();
                // Capitalize the first letter of the priority, and lowercase the rest
                String priority = capcase.substring(0, 1).toUpperCase() + capcase.substring(1).toLowerCase();
                thisAllocation.put("Priority",priority);
                
                if (entry.getValue().size() == 1){
                    thisAllocation.put("Allocated", entry.getValue().get(0).units);
                    thisAllocation.put("Warehouse", "Warehouse "+entry.getValue().get(0).warehouse.id);
                }
                else{
                    List<Object> allocs = new ArrayList<>();
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        Map<String,Object> thisAlloc = new LinkedHashMap<>();
                        thisAlloc.put("Units", entry.getValue().get(i).units);
                        thisAlloc.put("Warehouse", "Warehouse "+entry.getValue().get(i).warehouse.id);
                        allocs.add(thisAlloc);
                    }
                    thisAllocation.put("Allocated", allocs);
                }
                allocationArray.add(thisAllocation);
            }

            task1_2.put("Resource Allocation",allocationArray);
            task1_2.put("Remaining Capacities",remCap);


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
            Map<String,Object> finalCap = new LinkedHashMap<>();
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
                finalCap.put("Warehouse "+warehouse.id,warehouse.remainingCapacity);
            }

            List<Object> transferArray = new ArrayList<>();
            for (int i = 0; i < transfers.size(); i++) {
                ResourceRedistribution.Transfer transfer = transfers.get(i);
                Map<String,Object> thisTransfer = new LinkedHashMap<>();
                thisTransfer.put("From","Warehouse "+transfer.fromWarehouse.name);
                thisTransfer.put("To","Warehouse "+transfer.toWarehouse.name);
                thisTransfer.put("Units",transfer.units);
                transferArray.add(thisTransfer);
            }
            redisMap.put("Transfers",transferArray);
            redisMap.put("Final Resource Levels",finalCap);
            task3.put("Resource Redistribution",redisMap);

            jsonMap.put("Task 1 and 2",task1_2);
            jsonMap.put("Task 3",task3);


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
            LinkedHashMap<String,Object> DRSmap = new LinkedHashMap<>();
            // Print initial clusters
            System.out.println("");
            System.out.println("");

            LinkedHashMap<String,Object> initClusters = new LinkedHashMap<>();
            System.out.println("Initial Clusters:");
            for (int cityId : cityToWarehouses.keySet()) {
                System.out.println("City " + cityToName.get(cityId) + " belongs to cluster: " + sharing.find(cityId));
                initClusters.put("City "+cityToName.get(cityId),"Cluster "+sharing.find(cityId));
            }
            DRSmap.put("Initial Clusters",initClusters);

            // Perform unions based on shared resources
            ArrayList<Object> steps = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) {
                    if (sharing.shareResources(cities.get(i).id, cities.get(j).id)) {
                        System.out.println("Merging clusters of City " + cities.get(i).id + " and City " + cities.get(j).id + "...");
                        sharing.union(cities.get(i).id, cities.get(j).id);
                        Map<String,Object> thisStep = new LinkedHashMap<>();
                        thisStep.put("Action","Merge");
                        thisStep.put("Cities",List.of("City "+cities.get(i).name,"City "+cities.get(j).name));
                        thisStep.put("Cluster After Merge","Cluster "+sharing.find(cities.get(i).id));
                        steps.add(thisStep);
                    }
                }
            }
            DRSmap.put("Merging Steps",steps);

            // Print updated clusters
            System.out.println("");
            System.out.println("");

            LinkedHashMap<String,Object> finalClusters = new LinkedHashMap<>();
            System.out.println("Updated Clusters:");
            for (int cityId : cityToWarehouses.keySet()) {
                System.out.println("City " + cityToName.get(cityId) + " belongs to cluster: " + sharing.find(cityId));
                finalClusters.put("City "+cityToName.get(cityId),"Cluster "+sharing.find(cityId));
            }
            DRSmap.put("Cluster Membership After Merging",finalClusters);

            System.out.println("");
            System.out.println("");
            System.out.println("Querying if cities are in the same cluster:");
            ArrayList<Object> queries = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) { // Start from i + 1 to avoid redundant checks
                    EmergencySupplyNetwork.City city1 = cities.get(i);
                    EmergencySupplyNetwork.City city2 = cities.get(j);
            
                    // Perform the query
                    LinkedHashMap<String,Object> thisQuery = new LinkedHashMap<>();
                    System.out.println("Query: Are City " + city1.name + " and City " + city2.name + " in the same cluster?");
                    thisQuery.put("Query","Are City "+city1.name+" and City "+city2.name+" in the same cluster?");
                    System.out.println(sharing.areInSameCluster(city1.id, city2.id) ? "Yes" : "No");
                    thisQuery.put("Result",sharing.areInSameCluster(city1.id, city2.id) ? "Yes" : "No");
                    queries.add(thisQuery);
                }
            }
            DRSmap.put("Queries",queries);

            task4.put("Dynamic Resource Sharing",DRSmap);
            jsonMap.put("Task 4",task4);


            // Write JSON output to file
            try {
                String jsonOutput = JsonUtils.mapToJson(jsonMap, 4);
                String outputFilePath = inputFilePath.replace(".txt", ".json");
                System.out.println("Saving results to " + outputFilePath);
                JsonUtils.saveToJson(outputFilePath, jsonOutput);
                System.out.println(jsonOutput);
                System.out.println("Results saved to " + outputFilePath);
            } catch (IOException e) {
                System.err.println("Error saving JSON: " + e.getMessage());
            }
            
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
