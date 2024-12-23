package com.ift2015.tp2;
// ResourceRedistribution.java
import java.util.*;
import com.ift2015.tp2.EmergencySupplyNetwork.Warehouse;


/**
 * The ResourceRedistribution class is responsible for managing the redistribution
 * of resources between warehouses with surplus and those in need. It uses two
 * priority queues (heaps) to categorize warehouses based on their remaining capacity.
 * 
 * The surplusHeap is a max heap that stores warehouses with surplus resources,
 * sorted by their remaining capacity in descending order.
 * 
 * The needHeap is a min heap that stores warehouses that need resources,
 * sorted by their remaining capacity in ascending order.
 * 
 * Warehouses with a remaining capacity greater than 50 are added to the surplusHeap.
 * Warehouses with a remaining capacity less than 50 are added to the needHeap.
 * 
 * The redistributeResources method processes the two heaps and transfers resources
 * from surplus warehouses to those in need until either heap is empty. It returns
 * a list of transfers performed during the redistribution process.
 * 
 * The Transfer class represents a transfer of resources between two warehouses.
 * It contains information about the source warehouse, the destination warehouse,
 * and the number of units being transferred.
 */
public class ResourceRedistribution {
    private PriorityQueue<Warehouse> surplusHeap;
    private PriorityQueue<Warehouse> needHeap;

    /**
     * Constructs a ResourceRedistribution object that categorizes warehouses into
     * surplus and need heaps based on their remaining capacity.
     *
     * @param warehouses the list of warehouses to be categorized
     *
     * The surplusHeap is a max heap that stores warehouses with surplus resources,
     * sorted by their remaining capacity in descending order.
     *
     * The needHeap is a min heap that stores warehouses that need resources,
     * sorted by their remaining capacity in ascending order.
     *
     * Warehouses with a remaining capacity greater than 50 are added to the surplusHeap.
     * Warehouses with a remaining capacity less than 50 are added to the needHeap.
     */
    public ResourceRedistribution(List<Warehouse> warehouses) {
        // Max heap for surplus warehouses (sorts by units in descending order)
        // Here we use a subtraction through a lambda expression to reverse the order of the comparison
        surplusHeap = new PriorityQueue<>((w1, w2) -> w2.remainingCapacity - w1.remainingCapacity);

        // Min heap for warehouses that need resources (sorts by units in ascending order)
        needHeap = new PriorityQueue<>(Comparator.comparingInt(w -> w.remainingCapacity));

        // Categorize warehouses into surplus and need heaps
        for (Warehouse warehouse : warehouses) {
            if (warehouse.remainingCapacity > 50) {
                surplusHeap.add(warehouse);
            } else if (warehouse.remainingCapacity < 50) {
                needHeap.add(warehouse);
            }
        }
    }

    /**
     * Redistributes resources between warehouses with surplus and those in need.
     * 
     * This method processes two heaps: one containing warehouses with surplus resources
     * and another containing warehouses with resource needs. It transfers resources from
     * surplus warehouses to those in need until either heap is empty.
     * 
     * @return a list of transfers performed during the redistribution process.
     */
    public List<Transfer> redistributeResources() {
        // List to collect the transfers done
        List<Transfer> transfers = new ArrayList<>();

        while (!surplusHeap.isEmpty() && !needHeap.isEmpty()) {
            // Get the warehouses with the most surplus and the most need
            // Note that poll() removes the warehouse from the heap
            Warehouse surplusWarehouse = surplusHeap.poll();
            Warehouse needWarehouse = needHeap.poll();

            // Calculate the amount of units that can be transferred
            // The amount of units that can be transferred is the minimum
            // between the surplus that can be given and what is missing
            int surplusCanGive = surplusWarehouse.remainingCapacity - 50;
            int needIsMissing = 50 - needWarehouse.remainingCapacity;
            int transferableAmount = Math.min(surplusCanGive, needIsMissing);

            // Update the remaining capacity of the warehouses
            surplusWarehouse.remainingCapacity -= transferableAmount;
            needWarehouse.remainingCapacity += transferableAmount;

            // Add the transfer to the list of transfers
            transfers.add(new Transfer(surplusWarehouse, needWarehouse, transferableAmount));

            // Add the warehouses back to their respective heaps
            if (surplusWarehouse.remainingCapacity > 50) {
                surplusHeap.add(surplusWarehouse);
            }
            if (needWarehouse.remainingCapacity < 50) {
                needHeap.add(needWarehouse);
            }
        }

        return transfers;
    }

    /**
     * Represents a transfer of resources between two warehouses.
     */
    public static class Transfer {
        EmergencySupplyNetwork.Warehouse fromWarehouse; // The warehouse from which resources are being transferred.
        EmergencySupplyNetwork.Warehouse toWarehouse; // The warehouse to which resources are being transferred.
        int units; // The number of units being transferred.

        
        /**
         * Constructs a new Transfer object.
         *
         * @param fromWarehouse the warehouse from which resources are being transferred
         * @param toWarehouse the warehouse to which resources are being transferred
         * @param units the number of units being transferred
         */
        public Transfer(EmergencySupplyNetwork.Warehouse fromWarehouse, EmergencySupplyNetwork.Warehouse toWarehouse, int units) {
            this.fromWarehouse = fromWarehouse;
            this.toWarehouse = toWarehouse;
            this.units = units;
        }
        
        /**
         * Returns a string representation of the transfer.
         *
         * @return a string describing the transfer
         */
        @Override
        public String toString() {
            return "Transferred " + units + " units from Warehouse " + fromWarehouse.name + " to Warehouse " + toWarehouse.name + ".";
        }
    }
    
}
