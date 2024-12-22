package com.ift2015.tp2;
import java.io.IOException;
// NetworkApp.java
import java.util.*;

import com.ift2015.tp2.EmergencySupplyNetwork.City;


public class NetworkApp {
    public static void main(String[] args) {


        String inputFilePath = "TestCase0.txt";

        try {
            InputParser.ParsedData parsedData = InputParser.parseInput(inputFilePath);

            List<EmergencySupplyNetwork.City> cities = parsedData.cities;
            List<EmergencySupplyNetwork.Warehouse> warehouses = parsedData.warehouses;


            // Continue with tasks...

            System.out.println("Parsed Cities:"+ cities.size());
            for (City city : cities) {
                System.out.println("City " + city.id + " Priority: " + city.priority);
            }

            System.out.println("Parsed warehouses: " + warehouses.size());
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("Warehouse " + warehouse.id + " Capacity: " + warehouse.capacity);
            }
            // Task 1 & 2: Graph and Resource Allocation
            EmergencySupplyNetwork network = new EmergencySupplyNetwork(cities, warehouses);
            double[][] costMatrix = network.getCostMatrix();
            System.out.println("Cost Matrix:");
            for (double[] row : costMatrix) {
                System.out.println(Arrays.toString(row));
            }

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
            System.out.println("Remaining Warehouse Capacities:");
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
            }

            // Task 3: Resource Redistribution
            
            List<ResourceRedistribution.Warehouse> redistributionWarehouses = new ArrayList<>();
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                redistributionWarehouses.add(new ResourceRedistribution.Warehouse(warehouse.id, warehouse.remainingCapacity, warehouse.name));
            }
            ResourceRedistribution redistribution = new ResourceRedistribution(redistributionWarehouses);

            //ResourceRedistribution redistribution = new ResourceRedistribution(warehouses);
            List<ResourceRedistribution.Transfer> transfers = redistribution.redistributeResources();
            System.out.println("Resource Transfers:");
            for (ResourceRedistribution.Transfer transfer : transfers) {
                System.out.println(transfer);
            }

            System.out.println("Final Resource Levels:");
            for (EmergencySupplyNetwork.Warehouse warehouse : warehouses) {
                System.out.println("  Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity + " units");
            }

            // Task 4: Dynamic Resource Sharing
            List<Integer> cityIds = Arrays.asList(1, 2, 3);
            DynamicResourceSharing sharing = new DynamicResourceSharing(cityIds);
            sharing.union(1, 2);

            System.out.println("Clusters:");
            Map<Integer, List<Integer>> clusters = sharing.getClusters();
            for (Map.Entry<Integer, List<Integer>> entry : clusters.entrySet()) {
                System.out.println("Cluster Root: " + entry.getKey() + " Cities: " + entry.getValue());
            }

            System.out.println("Are City 1 and City 3 in the same cluster? " + sharing.areInSameCluster(1, 3));
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
