package com.ift2015.tp2;
// DynamicResourceSharing.java
import java.util.*;

public class DynamicResourceSharing {
    private Map<Integer, Integer> parent;
    private Map<Integer, Integer> rank;

    public DynamicResourceSharing(List<Integer> cityIds) {
        parent = new HashMap<>();
        rank = new HashMap<>();
        for (int cityId : cityIds) {
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
