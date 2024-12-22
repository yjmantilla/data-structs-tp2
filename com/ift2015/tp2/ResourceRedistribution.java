package com.ift2015.tp2;
// ResourceRedistribution.java
import java.util.*;
import com.ift2015.tp2.EmergencySupplyNetwork.Warehouse;


public class ResourceRedistribution {
    private PriorityQueue<Warehouse> surplusHeap;
    private PriorityQueue<Warehouse> needHeap;

    public ResourceRedistribution(List<Warehouse> warehouses) {
        surplusHeap = new PriorityQueue<>((w1, w2) -> w2.remainingCapacity - w1.remainingCapacity);
        needHeap = new PriorityQueue<>(Comparator.comparingInt(w -> w.remainingCapacity));

        for (Warehouse warehouse : warehouses) {
            if (warehouse.remainingCapacity > 50) {
                surplusHeap.add(warehouse);
            } else if (warehouse.remainingCapacity < 50) {
                needHeap.add(warehouse);
            }
        }
    }

    public List<Transfer> redistributeResources() {
        List<Transfer> transfers = new ArrayList<>();

        while (!surplusHeap.isEmpty() && !needHeap.isEmpty()) {
            Warehouse surplusWarehouse = surplusHeap.poll();
            Warehouse needWarehouse = needHeap.poll();

            int transferableAmount = Math.min(surplusWarehouse.remainingCapacity - 50, 50 - needWarehouse.remainingCapacity);
            surplusWarehouse.remainingCapacity -= transferableAmount;
            needWarehouse.remainingCapacity += transferableAmount;

            transfers.add(new Transfer(surplusWarehouse, needWarehouse, transferableAmount));

            if (surplusWarehouse.remainingCapacity > 50) {
                surplusHeap.add(surplusWarehouse);
            }
            if (needWarehouse.remainingCapacity < 50) {
                needHeap.add(needWarehouse);
            }
        }

        return transfers;
    }

    public static class Transfer {
        EmergencySupplyNetwork.Warehouse fromWarehouse;
        EmergencySupplyNetwork.Warehouse toWarehouse;
        int units;
    
        public Transfer(EmergencySupplyNetwork.Warehouse fromWarehouse, EmergencySupplyNetwork.Warehouse toWarehouse, int units) {
            this.fromWarehouse = fromWarehouse;
            this.toWarehouse = toWarehouse;
            this.units = units;
        }
    
        @Override
        public String toString() {
            return "Transferred " + units + " units from Warehouse " + fromWarehouse.name + " to Warehouse " + toWarehouse.name + ".";
        }
    }
    

}
