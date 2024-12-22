package com.ift2015.tp2;
// DynamicResourceSharing.java
import java.util.*;

public class DynamicResourceSharing {
    private Map<Integer, Integer> parent;
    private Map<Integer, Integer> rank;
    private Map<Integer, List<Integer>> cityToWarehouses; // Store city-to-warehouse mapping

    public DynamicResourceSharing(Map<Integer, List<Integer>> cityToWarehouses) {
        this.cityToWarehouses = cityToWarehouses;
        parent = new HashMap<>();
        rank = new HashMap<>();

        // Initialize parent and rank for all city IDs
        for (Integer cityId : cityToWarehouses.keySet()) {
            parent.put(cityId, cityId);
            rank.put(cityId, 0);
        }
    }

    public int find(int cityId) {
        if (parent.get(cityId) != cityId) {
            parent.put(cityId, find(parent.get(cityId))); // Path compression
        }
        return parent.get(cityId);
    }

    public void union(int cityId1, int cityId2) {
        int root1 = find(cityId1);
        int root2 = find(cityId2);

        if (root1 != root2) {
            if (rank.get(root1) > rank.get(root2)) {
                parent.put(root2, root1);
            } else if (rank.get(root1) < rank.get(root2)) {
                parent.put(root1, root2);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank.get(root1) + 1);
            }
        }
    }

    public boolean areInSameCluster(int cityId1, int cityId2) {
        return find(cityId1) == find(cityId2);
    }

    public boolean shareResources(int cityId1, int cityId2) {
        List<Integer> warehousesCity1 = cityToWarehouses.get(cityId1);
        List<Integer> warehousesCity2 = cityToWarehouses.get(cityId2);

        if (warehousesCity1 == null || warehousesCity2 == null) {
            return false; // No warehouses assigned
        }

        // Check for any shared warehouses
        // check if the two arrays are exactly the same
        if (warehousesCity1.size() == warehousesCity2.size() && warehousesCity1.containsAll(warehousesCity2)) {
            return true;
        } 

        return false; // No shared resources
    }

    public Map<Integer, List<Integer>> getClusters() {
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int cityId : parent.keySet()) {
            int root = find(cityId);
            clusters.putIfAbsent(root, new ArrayList<>());
            clusters.get(root).add(cityId);
        }
        return clusters;
    }
}
