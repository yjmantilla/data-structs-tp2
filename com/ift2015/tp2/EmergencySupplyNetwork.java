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

    /**
     * Allocates resources from warehouses to cities based on their priority and transportation cost.
     * 
     * The method performs the following steps:
     * 1. Sorts the cities by their priority in descending order.
     * 2. Initializes the remaining capacity of each warehouse.
     * 3. Iterates through each city and allocates resources from the warehouses based on the transportation cost.
     * 4. Updates the remaining capacity of the warehouses and the demand of the cities accordingly.
     * 5. Returns a map of cities to their respective resource allocations, sorted by city ID.
     * 
     * @return A map where the key is a City object and the value is a list of ResourceAllocation objects representing the resources allocated to that city.
     */
    public Map<City, List<ResourceAllocation>> allocateResources() {

        // First, sort the cities by priority
        PriorityQueue<City> cityQueue = new PriorityQueue<>((c1, c2) -> {
            int result = c2.priority.compareTo(c1.priority); // Descending order
            return result;
        });

        cityQueue.addAll(cities);
        Map<City, List<ResourceAllocation>> allocations = new HashMap<>(); // Map to store the allocations

        // Initialize remaining capacity of warehouses
        for (Warehouse warehouse : warehouses) {
            warehouse.remainingCapacity = warehouse.capacity;
        }

        while (!cityQueue.isEmpty()) {
            City city = cityQueue.poll();

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
            
                // If the warehouse has remaining capacity, allocate as much as possible in the warehouse
                if (warehouse.remainingCapacity > 0) {
                    int allocatedUnits = Math.min(city.demand, warehouse.remainingCapacity); // Allocate the minimum of the two
                    allocations.putIfAbsent(city, new ArrayList<>());
                    allocations.get(city).add(new ResourceAllocation(warehouse, allocatedUnits));
                    warehouse.remainingCapacity -= allocatedUnits; // Update remaining capacity of the warehouse
                    city.demand -= allocatedUnits; // Update demand of the city
                    System.out.println("  Allocated " + allocatedUnits + " units from Warehouse " + warehouse.id);
                }
            }

        }

        // sort the allocations by city id for consistent output
        Map<City, List<ResourceAllocation>> sortedAllocations = new TreeMap<>((c1, c2) -> Integer.compare(c1.id, c2.id));
        sortedAllocations.putAll(allocations);
        return sortedAllocations;
    }
    
    
    /**
     * Calculates the transportation cost between a city and a warehouse.
     *
     * @param city The city for which the transportation cost is being calculated.
     * @param warehouse The warehouse from which the transportation cost is being calculated.
     * @return The transportation cost based on the distance and a transport coefficient.
     */
    private double calculateTransportationCost(City city, Warehouse warehouse) {
        double distance = euclideanDistance(city.x, city.y, warehouse.x, warehouse.y);
        int coefficient = getTransportCoefficient(distance);
        return distance * coefficient;
    }
    
    public static class City {
        int id, x, y, demand; // Unique identifier, coordinates, and demand of the city.
        Priority priority; // Priority of the city.
        String name; // Name of the city.
        
        /**
         * Constructs a new City with the specified id, coordinates, demand, priority, and name.
         *
         * @param id       the unique identifier for the city
         * @param x        the x-coordinate of the city location
         * @param y        the y-coordinate of the city location
         * @param demand   the demand of the city
         * @param priority the priority of the city
         * @param name     the name of the city
         */
        public City(int id, int x, int y, int demand, Priority priority, String name) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.demand = demand;
            this.priority = priority;
            this.name = name;
        }
        
        /**
         * Returns a string representation of the city.
         *
         * @return a string representation of the city
         */
        @Override
        public String toString() {
            return "City{name='" + name + "', id=" + id + ", x=" + x + ", y=" + y +
                   ", demand=" + demand + ", priority=" + priority + "}";
        }
    }


    /**
     * Represents a warehouse in the emergency supply network.
     */
    public static class Warehouse {
        int id; // Unique identifier for the warehouse.
        int x;  // X-coordinate of the warehouse location.
        int y;  // Y-coordinate of the warehouse location.
        int capacity; // Total capacity of the warehouse.
        int remainingCapacity; // Remaining capacity of the warehouse.
        String name; // Name of the warehouse.

        /**
         * Constructs a new Warehouse with the specified id, coordinates, and capacity.
         *
         * @param id       the unique identifier for the warehouse
         * @param x        the x-coordinate of the warehouse location
         * @param y        the y-coordinate of the warehouse location
         * @param capacity the total capacity of the warehouse
         * @param name     the name of the warehouse
         */
        public Warehouse(int id, int x, int y, int capacity, String name) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.capacity = capacity;
            this.remainingCapacity = capacity;
            this.name = name;
        }

        /**
         * Returns a string representation of the warehouse.
         *
         * @return a string representation of the warehouse
         */
        @Override
        public String toString() {
            return "Warehouse{name='" + name + "', id=" + id + ", x=" + x + ", y=" + y +
                   ", capacity=" + capacity + ", remainingCapacity=" + remainingCapacity + "}";
        }
    }
    
    /**
     * Represents a resource allocation from a warehouse.
     */
    public static class ResourceAllocation {
        Warehouse warehouse; // The warehouse from which the resources are allocated.
        int units; // The number of units allocated.

        public ResourceAllocation(Warehouse warehouse, int units) {
            this.warehouse = warehouse; // The warehouse from which the resources are allocated.
            this.units = units; // The number of units allocated.
        }

        /**
         * Returns a string representation of the resource allocation.
         *
         * @return a string representation of the resource allocation
         */
        @Override
        public String toString() {
            return "ResourceAllocation{" +
                    "warehouseId=" + this.warehouse.id +
                    ", units=" + units +
                    '}';
        }
    }

    public enum Priority {
        LOW, MEDIUM, HIGH // Priority levels
    }
}
