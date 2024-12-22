package com.ift2015.tp2;
// ResourceRedistribution.java
import java.util.*;


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

            transfers.add(new Transfer(surplusWarehouse.id, needWarehouse.id, transferableAmount, surplusWarehouse.name, needWarehouse.name));

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
        int fromWarehouseId;
        int toWarehouseId;
        int units;
        String fromName;
        String toName;

        public Transfer(int fromWarehouseId, int toWarehouseId, int units, String fromName, String toName) {
            this.fromWarehouseId = fromWarehouseId;
            this.toWarehouseId = toWarehouseId;
            this.units = units;
            this.fromName = fromName;
            this.toName = toName;
        }

        // @Override
        // public String toString() {
        //     return "Transfer {" +
        //             "from=" + fromWarehouseId +
        //             ", to=" + toWarehouseId +
        //             ", units=" + units +
        //             '}';
        // }
        @Override
        public String toString() {
            return "Transferred " + units + " units from Warehouse " + fromName + " to Warehouse " + toName + ".";
        }
    }

    public static class Warehouse {
        int id, remainingCapacity;
        String name;

        public Warehouse(int id, int remainingCapacity, String name) {
            this.id = id;
            this.remainingCapacity = remainingCapacity;
            this.name = name;
        }
    }
}
