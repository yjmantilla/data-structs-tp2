// DynamicResourceSharing.java
import java.util.*;

/**
 * The DynamicResourceSharing class provides a way to manage and share resources
 * dynamically among different cities using the union-find data structure. It 
 * supports operations to find the root parent of a city, merge sets of cities, 
 * check if two cities are in the same cluster, and determine if two cities share 
 * the same set of warehouses.
 * 
 * This class is initialized with a mapping of cities to their respective 
 * warehouses and uses union-find with path compression and union by rank to 
 * efficiently manage the clusters of cities.
 * 
 * Methods:
 *   {DynamicResourceSharing(Map)}: Constructs a DynamicResourceSharing object with the given city-to-warehouse mapping.
 *   {find(int)}: Finds the root parent of the given city using path compression.
 *   {union(int, int)}: Merges the sets containing the two specified cities.
 *   {areInSameCluster(int, int)}: Determines if two cities are in the same cluster.
 *   {shareResources(int, int)}: Determines if two cities share the same set of warehouses.
 *   {getClusters()}: Retrieves the clusters of cities based on their root parent.
 * 
 * Fields:
 *   {private Map<Integer, Integer> parent}: A map to store the parent of each city for union-find operations.
 *   {private Map<Integer, Integer> rank}: A map to store the rank (depth) of each city for union-find operations.
 *   {private Map<Integer, List<Integer>> cityToWarehouses}: A map to store the city-to-warehouse mapping.
 */
public class DynamicResourceSharing {
    private Map<Integer, Integer> parent;
    private Map<Integer, Integer> rank;
    private Map<Integer, List<Integer>> cityToWarehouses; // Store city-to-warehouse mapping

    /**
     * Constructs a DynamicResourceSharing object with the given city-to-warehouse mapping.
     * Initializes the parent and rank maps for union-find operations.
     *
     * @param cityToWarehouses A map where the key is a city ID and the value is a list of warehouse IDs associated with that city.
     */
    public DynamicResourceSharing(Map<Integer, List<Integer>> cityToWarehouses) {
        this.cityToWarehouses = cityToWarehouses; // Store city-to-warehouse mapping
        parent = new HashMap<>(); // Initialize parent map
        rank = new HashMap<>(); // Initialize rank map

        // Initialize parent and rank for all city IDs
        for (Integer cityId : cityToWarehouses.keySet()) {
            parent.put(cityId, cityId);
            rank.put(cityId, 0);
        }
    }

    /**
     * Finds the root parent of the given city using path compression.
     * 
     * @param cityId the ID of the city to find the root parent for
     * @return the ID of the root parent city
     */
    public int find(int cityId) {
        if (parent.get(cityId) != cityId) { // If the city is not its own parent
            parent.put(cityId, find(parent.get(cityId))); // Recursively find the root parent, this is path compression
        }
        return parent.get(cityId);
    }

    /**
     * Merges the sets containing the two specified cities. If the cities are 
     * already in the same set, no changes are made. This method uses path 
     * compression to find the root representatives of the sets and union by 
     * rank to maintain a balanced tree structure.
     *
     * @param cityId1 the identifier of the first city
     * @param cityId2 the identifier of the second city
     */
    public void union(int cityId1, int cityId2) {
    // Find the root representatives of both cities using path compression
    int root1 = find(cityId1);
    int root2 = find(cityId2);

    // Only merge if cities are from different sets/clusters
    if (root1 != root2) {
        // If tree 1 is deeper, make it the parent to maintain balance
        if (rank.get(root1) > rank.get(root2)) {
            parent.put(root2, root1);
        } 
        // If tree 2 is deeper, make it the parent to maintain balance
        else if (rank.get(root1) < rank.get(root2)) {
            parent.put(root1, root2);
        }
        // If both trees have same depth
        else {
            // Arbitrarily choose root1 as parent
            parent.put(root2, root1);
            // Increment rank of root1 since tree depth increases by 1
            rank.put(root1, rank.get(root1) + 1);
            }
        }
    }

    /**
     * Determines if two cities are in the same cluster.
     *
     * @param cityId1 the ID of the first city
     * @param cityId2 the ID of the second city
     * @return true if both cities are in the same cluster, false otherwise
     */
    public boolean areInSameCluster(int cityId1, int cityId2) {
        return find(cityId1) == find(cityId2);
    }

    /**
     * Determines if two cities share the same set of warehouses.
     *
     * @param cityId1 the ID of the first city
     * @param cityId2 the ID of the second city
     * @return true if both cities have exactly the same warehouses assigned, false otherwise
     */
    public boolean shareResources(int cityId1, int cityId2) {
        // Retrieve the list of warehouses assigned to each city
        List<Integer> warehousesCity1 = cityToWarehouses.get(cityId1);
        List<Integer> warehousesCity2 = cityToWarehouses.get(cityId2);

        // Check if the cities have no warehouses assigned
        if (warehousesCity1 == null || warehousesCity2 == null) {
            return false; // No warehouses assigned
        }

        // Check for any shared (set of) warehouses
        // We consider that two cities share the same resources if they have the same set of warehouses
        // regardless of the order in which they are assigned
        // We can check if two lists are equal by comparing their sizes and checking if one list contains all elements of the other
        if (warehousesCity1.size() == warehousesCity2.size() && warehousesCity1.containsAll(warehousesCity2)) {
            return true;
        }
        return false; // No shared resources
    }

    /**
     * Retrieves the clusters of cities based on their root parent.
     * Each cluster is represented as a list of city IDs, grouped by their root parent ID.
     *
     * @return A map where the key is the root parent ID and the value is a list of city IDs belonging to that cluster.
     */
    public Map<Integer, List<Integer>> getClusters() {
        Map<Integer, List<Integer>> clusters = new HashMap<>(); // Store clusters of cities

        for (int cityId : parent.keySet()) { // Iterate over all city IDs
            int root = find(cityId); // Find the root parent of the city ID
            clusters.putIfAbsent(root, new ArrayList<>()); // Initialize the cluster if it doesn't exist
            clusters.get(root).add(cityId); // Add the city ID to the cluster
        }
        return clusters; // Return the clusters
    }
}
