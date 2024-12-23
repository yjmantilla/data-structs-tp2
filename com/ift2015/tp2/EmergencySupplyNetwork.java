package com.ift2015.tp2;
// EmergencySupplyNetwork.java
import java.util.*;

/**
 * The EmergencySupplyNetwork class represents a network of cities and warehouses
 * for managing the transportation and allocation of emergency supplies.
 * It calculates transportation costs between cities and warehouses and allocates
 * resources based on city priorities and warehouse capacities.
 * 
 * The class provides methods to:
 * - Construct the network with a list of cities and warehouses.
 * - Calculate the cost matrix for transporting supplies.
 * - Retrieve the cost matrix.
 * - Allocate resources from warehouses to cities based on priority and cost.
 * 
 * The class also includes nested static classes to represent City, Warehouse,
 * and ResourceAllocation, as well as an enum for Priority levels.
 * 
 * Methods:
 * - EmergencySupplyNetwork(List<City> cities, List<Warehouse> warehouses):
 *   Constructs the network with the specified list of cities and warehouses.
 * - calculateCostMatrix(): Calculates the cost matrix for transporting supplies.
 * - euclideanDistance(int x1, int y1, int x2, int y2): Calculates the Euclidean distance between two points.
 * - getTransportCoefficient(double distance): Determines the transport coefficient based on the distance.
 * - getCostMatrix(): Retrieves the cost matrix for the network.
 * - allocateResources(): Allocates resources from warehouses to cities based on priority and cost.
 * - calculateTransportationCost(City city, Warehouse warehouse): Calculates the transportation cost between a city and a warehouse.
 * 
 * Nested Classes:
 * - City: Represents a city in the network with id, coordinates, demand, priority, and name.
 * - Warehouse: Represents a warehouse in the network with id, coordinates, capacity, remaining capacity, and name.
 * - ResourceAllocation: Represents a resource allocation from a warehouse with the warehouse and number of units allocated.
 * 
 * Enum:
 * - Priority: Represents priority levels (LOW, MEDIUM, HIGH) for cities.
 */
public class EmergencySupplyNetwork {
    private double[][] costMatrix;
    private List<City> cities;
    private List<Warehouse> warehouses;
    private boolean costReady = false;

    /**
     * Constructs an EmergencySupplyNetwork with the specified list of cities and warehouses.
     * Initializes the cost matrix and calculates the costs between cities and warehouses.
     *
     * @param cities      the list of cities in the network
     * @param warehouses  the list of warehouses in the network
     */
    public EmergencySupplyNetwork(List<City> cities, List<Warehouse> warehouses) {
        this.cities = cities;
        this.warehouses = warehouses;
        this.costMatrix = new double[cities.size()][warehouses.size()];
        calculateCostMatrix();
    }

    /**
     * Calculates the cost matrix for transporting supplies from warehouses to cities.
     * The cost matrix is a 2D array where each element represents the transportation cost
     * from a specific warehouse to a specific city.
     * 
     * This method iterates over all cities and warehouses, calculates the transportation cost
     * for each pair, and stores the result in the cost matrix. Once the cost matrix is fully
     * populated, it sets the costReady flag to true.
     */
    private void calculateCostMatrix() {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < warehouses.size(); j++) {
                this.costMatrix[i][j] = calculateTransportationCost(cities.get(i), warehouses.get(j));
            }
        }
        this.costReady = true;
    }

    /**
     * Calculates the Euclidean distance between two points (x1, y1) and (x2, y2).
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the Euclidean distance between the two points
     */
    private double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Determines the transport coefficient based on the given distance.
     * 
     * @param distance the distance to be covered
     * @return the transport coefficient:
     *         1 if the distance is less than or equal to 10 (Drone),
     *         2 if the distance is greater than 10 and less than or equal to 20 (Truck),
     *         3 if the distance is greater than 20 (Rail)
     */
    private int getTransportCoefficient(double distance) {
        if (distance <= 10) return 1; // Drone
        else if (distance <= 20) return 2; // Truck
        else return 3; // Rail
    }

    /**
     * Retrieves the cost matrix for the emergency supply network.
     * If the cost matrix is not ready, it calculates the cost matrix first.
     *
     * @return a 2D array representing the cost matrix.
     */
    public double[][] getCostMatrix() {
        if (!this.costReady) {
            this.calculateCostMatrix();
        }
        return this.costMatrix;
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

        // Make sure cost matrix is ready, otherwise calculate it
        if (!this.costReady) {
            this.calculateCostMatrix();
        }

        while (!cityQueue.isEmpty()) {
            City city = cityQueue.poll();

            System.out.println("Allocating resources for City " + city.id + " (Priority: " + city.priority + ")");

            // Sort warehouses by transportation cost for the current city
            // Create a copy of the warehouses list to avoid modifying the original list
            // This is crucial to use the cost matrix calculated based on the original order of cities and warehouses
            List<Warehouse> warehouses_for_city = new ArrayList<>(this.warehouses);
            warehouses_for_city.sort((w1, w2) -> {
                int cityIndex = this.cities.indexOf(city);
                int warehouseIndex1 = this.warehouses.indexOf(w1);
                int warehouseIndex2 = this.warehouses.indexOf(w2);
                double cost1 = this.getCostMatrix()[cityIndex][warehouseIndex1];
                double cost2 = this.getCostMatrix()[cityIndex][warehouseIndex2];
                // Alternatively, we could calculate the transportation cost again
                // might be faster to recalculate it than to look it up in the matrix
                // if there are many cities and warehouses
                // double cost1 = calculateTransportationCost(city, w1);
                // double cost2 = calculateTransportationCost(city, w2);
                return Double.compare(cost1, cost2);
            });

            for (Warehouse warehouse : warehouses_for_city) {
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

    /**
     * Enum representing the priority levels for the Emergency Supply Network.
     */
    public enum Priority {
        LOW, MEDIUM, HIGH // Priority levels
    }
}
