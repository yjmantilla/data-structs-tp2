package com.ift2015.tp2;
// EmergencySupplyNetwork.java
import java.util.*;

public class EmergencySupplyNetwork {
    private double[][] costMatrix;
    private List<City> cities;
    private List<Warehouse> warehouses;

    public EmergencySupplyNetwork(List<City> cities, List<Warehouse> warehouses) {
        this.cities = cities;
        this.warehouses = warehouses;
        this.costMatrix = new double[cities.size()][warehouses.size()];
        calculateCostMatrix();
    }

    private void calculateCostMatrix() {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < warehouses.size(); j++) {
                double distance = euclideanDistance(
                        cities.get(i).x, cities.get(i).y,
                        warehouses.get(j).x, warehouses.get(j).y);
                int coefficient = getTransportCoefficient(distance);
                costMatrix[i][j] = distance * coefficient;
            }
        }
    }

    private double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private int getTransportCoefficient(double distance) {
        if (distance <= 10) return 1;
        else if (distance <= 20) return 2;
        else return 3;
    }

    public double[][] getCostMatrix() {
        return costMatrix;
    }

    public Map<City, List<ResourceAllocation>> allocateResources() {
        //PriorityQueue<City> cityQueue = new PriorityQueue<>((c1, c2) -> c2.priority.compareTo(c1.priority));

        PriorityQueue<City> cityQueue = new PriorityQueue<>((c1, c2) -> {
            int result = c2.priority.compareTo(c1.priority); // Descending order
            //System.out.println("Comparing City " + c1.id + " (Priority: " + c1.priority + ") with City " + c2.id + " (Priority: " + c2.priority + ") -> " + result);
            return result;
        });
        cityQueue.addAll(cities);
        Map<City, List<ResourceAllocation>> allocations = new HashMap<>();

        for (Warehouse warehouse : warehouses) {
            warehouse.remainingCapacity = warehouse.capacity;
        }

        while (!cityQueue.isEmpty()) {
            City city = cityQueue.poll();
            //System.out.println("Polled City " + city.id + " Priority: " + city.priority);

            System.out.println("Allocating resources for City " + city.id + " (Priority: " + city.priority + ")");

            // Sort warehouses by transportation cost for the current city
            warehouses.sort((w1, w2) -> {
                double cost1 = calculateTransportationCost(city, w1);
                double cost2 = calculateTransportationCost(city, w2);
                return Double.compare(cost1, cost2);
            });

            for (Warehouse warehouse : warehouses) {
                // If city demand is already met, stop allocating
                if (city.demand == 0) break;
            
                // If the warehouse has remaining capacity, allocate resources
                if (warehouse.remainingCapacity > 0) {
                    int allocatedUnits = Math.min(city.demand, warehouse.remainingCapacity);
                    allocations.putIfAbsent(city, new ArrayList<>());
                    allocations.get(city).add(new ResourceAllocation(warehouse, allocatedUnits));
                    warehouse.remainingCapacity -= allocatedUnits;
                    city.demand -= allocatedUnits;
                    System.out.println("  Allocated " + allocatedUnits + " units from Warehouse " + warehouse.id);
        
                    // Debugging logs
                    //System.out.println("Allocating " + allocatedUnits + " units from Warehouse " + warehouse.id + " to City " + city.id);
                    //System.out.println("Remaining capacity of Warehouse " + warehouse.id + ": " + warehouse.remainingCapacity);
                    //System.out.println("Remaining demand for City " + city.id + ": " + city.demand);
                }
            }

        }
        // sort the allocations by city id
        Map<City, List<ResourceAllocation>> sortedAllocations = new TreeMap<>((c1, c2) -> Integer.compare(c1.id, c2.id));
        sortedAllocations.putAll(allocations);
        return sortedAllocations;
    }

    public Map<City, List<ResourceAllocation>> allocateResourcesVersion1() {
        PriorityQueue<City> cityQueue = new PriorityQueue<>((c1, c2) -> c2.priority.compareTo(c1.priority));
        cityQueue.addAll(cities);
        System.out.println("cityQueue: " + cityQueue);
        Map<City, List<ResourceAllocation>> allocations = new HashMap<>();
    
        for (Warehouse warehouse : warehouses) {
            warehouse.remainingCapacity = warehouse.capacity; // Reset capacities
        }
    
        while (!cityQueue.isEmpty()) {
            City city = cityQueue.poll();
    
            // Sort warehouses by transportation cost for the current city
            warehouses.sort((w1, w2) -> {
                double cost1 = calculateTransportationCost(city, w1);
                double cost2 = calculateTransportationCost(city, w2);
                return Double.compare(cost1, cost2);
            });
    
            allocations.putIfAbsent(city, new ArrayList<>());
    
            // Allocate resources from warehouses
            for (Warehouse warehouse : warehouses) {
                if (city.demand == 0) break;
    
                if (warehouse.remainingCapacity > 0) {
                    int allocatedUnits = Math.min(city.demand, warehouse.remainingCapacity);
                    warehouse.remainingCapacity -= allocatedUnits;
                    city.demand -= allocatedUnits;
    
                    allocations.get(city).add(new ResourceAllocation(warehouse, allocatedUnits));
                }
            }
        }
    
        return allocations;
    }

    public Map<City, List<ResourceAllocation>> allocateResourcesVersion2() {
        PriorityQueue<City> cityQueue = new PriorityQueue<>((c1, c2) -> c2.priority.compareTo(c1.priority));
        cityQueue.addAll(cities);
        Map<City, List<ResourceAllocation>> allocations = new HashMap<>();
    
        for (Warehouse warehouse : warehouses) {
            warehouse.remainingCapacity = warehouse.capacity; // Reset capacities
        }
    
        while (!cityQueue.isEmpty()) {
            City city = cityQueue.poll();
    
            // Sort warehouses by transportation cost for the current city
            warehouses.sort((w1, w2) -> {
                double cost1 = calculateTransportationCost(city, w1);
                double cost2 = calculateTransportationCost(city, w2);
                return Double.compare(cost1, cost2);
            });
    
            allocations.putIfAbsent(city, new ArrayList<>());
    
            // Allocate resources from warehouses
            for (Warehouse warehouse : warehouses) {
                if (city.demand == 0) break;
    
                if (warehouse.remainingCapacity > 0) {
                    // Allocate as much as possible from the current warehouse
                    int allocatedUnits = Math.min(city.demand, warehouse.remainingCapacity);
                    warehouse.remainingCapacity -= allocatedUnits;
                    city.demand -= allocatedUnits;
    
                    allocations.get(city).add(new ResourceAllocation(warehouse, allocatedUnits));
                }
            }
        }
    
        return allocations;
    }
    
    
    private double calculateTransportationCost(City city, Warehouse warehouse) {
        double distance = Math.sqrt(Math.pow(city.x - warehouse.x, 2) + Math.pow(city.y - warehouse.y, 2));
        int coefficient = (distance <= 10) ? 1 : (distance <= 20) ? 2 : 3;
        return distance * coefficient;
    }
    
    public static class City {
        int id, x, y, demand;
        Priority priority;
        String name; // New field to store the name
    
        public City(int id, int x, int y, int demand, Priority priority) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.demand = demand;
            this.priority = priority;
        }
    
        // New method to set the name
        public void setName(String name) {
            this.name = name;
        }
    
        // Optional: Add a getter for the name
        public String getName() {
            return name;
        }
    
        @Override
        public String toString() {
            return "City{name='" + name + "', id=" + id + ", x=" + x + ", y=" + y +
                   ", demand=" + demand + ", priority=" + priority + "}";
        }
    }


    public static class Warehouse {
        int id, x, y, capacity, remainingCapacity;
        String name; // New field to store the name
    
        public Warehouse(int id, int x, int y, int capacity) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.capacity = capacity;
            this.remainingCapacity = capacity; // Initialize remaining capacity
        }
    
        // New method to set the name
        public void setName(String name) {
            this.name = name;
        }
    
        // Optional: Add a getter for the name
        public String getName() {
            return name;
        }
    
        @Override
        public String toString() {
            return "Warehouse{name='" + name + "', id=" + id + ", x=" + x + ", y=" + y +
                   ", capacity=" + capacity + ", remainingCapacity=" + remainingCapacity + "}";
        }
    }
    
    public static class ResourceAllocation {
        Warehouse warehouse;
        int units;

        public ResourceAllocation(Warehouse warehouse, int units) {
            this.warehouse = warehouse;
            this.units = units;
        }

        @Override
        public String toString() {
            return "ResourceAllocation{" +
                    "warehouseId=" + this.warehouse.id +
                    ", units=" + units +
                    '}';
        }
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}
