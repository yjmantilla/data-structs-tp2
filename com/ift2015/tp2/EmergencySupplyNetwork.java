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
