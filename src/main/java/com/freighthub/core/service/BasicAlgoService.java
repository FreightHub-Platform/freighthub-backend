package com.freighthub.core.service;

import com.freighthub.core.entity.*;
import com.freighthub.core.enums.ContainerType;
import com.freighthub.core.repository.ItemRepository;
import com.freighthub.core.repository.OrderRepository;
import com.freighthub.core.repository.PurchaseOrderRepository;
import com.freighthub.core.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BasicAlgoService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Need new class for route splitting here.
    // class will contain a list of PoId and a list of containerAssignment objects

    // For container bucketing
    class containerAssignment {
        ContainerType containerType;
        Integer containerTypeIndex;
        List<Item> items;
        List<VehicleAssignment> vehicleAssignments;
    }

    // For vehicle assignment on cbm
    class VehicleAssignment {
        VehicleType vehicleType;
        Integer vehicleIndex;
        List<Item> items;
    }

    ////////////COMPUTE ROUTES////////////////////////////////////////////////////////////////////
    public void computeRoutes() {
        Order order = orderRepository.findById(53L) // This is for testing only
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Actual po list will come from route splitting
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findByOrderId(order);
        divideContainerTypes(purchaseOrderList);
    }


    ////////////CONTAINER TYPE DIVIDE////////////////////////////////////////////////////////////////////

    public void divideContainerTypes(List<PurchaseOrder> purchaseOrderList) {

        List<containerAssignment> containerAssignments = new ArrayList<>(); // List of container assignments

        List<Item> items = new ArrayList<>(); // List of items

        for (PurchaseOrder po : purchaseOrderList) {
            items.addAll((List<Item>) itemRepository.getItemsByPoId(po)); // Get items by purchase order
        }

        items.sort(Comparator.comparing(Item::getCbm).reversed()); // Sort items by cbm in descending order

        // Create a map to track compatible groups
        // Key: Pair of container type and compatibility, Value: List of items
        Map<String, List<Item>> compatibilityBuckets = new HashMap<>();

        for (Item item : items) {
            ItemType itemType = item.getITypeId();
            String bucketKey = itemType.getContainerType().name() + "-" + itemType.getCompatibility();

            // Add the item to the corresponding bucket
            compatibilityBuckets.computeIfAbsent(bucketKey, k -> new ArrayList<>()).add(item);
        }

        // Create container assignments from the buckets
        for (Map.Entry<String, List<Item>> entry : compatibilityBuckets.entrySet()) {
            String[] parts = entry.getKey().split("-");
            ContainerType containerType = ContainerType.valueOf(parts[0]);
            Integer compatibilityIndex = Integer.valueOf(parts[1]);

            // Create a new container assignment
            containerAssignment assignment = new containerAssignment();
            assignment.containerType = containerType;
            assignment.containerTypeIndex = compatibilityIndex;
            assignment.items = entry.getValue();

            // Add the assignment to the list
            containerAssignments.add(assignment);
        }

        for (containerAssignment assignment : containerAssignments) {
            assignment.vehicleAssignments = getItemsAndDivide(assignment.items);
        }

        System.out.println("/////////// Whole Assigment ////////////\n");

        // print container assignments with vehicle assignments
        for (containerAssignment assignment : containerAssignments) {
            System.out.println("\nContainer Assignment: " + assignment.containerType + "_" + assignment.containerTypeIndex);
            for (VehicleAssignment vehicleAssignment : assignment.vehicleAssignments) {
                System.out.println("-Vehicle Assignment: " + vehicleAssignment.vehicleType.getId() + "_" + vehicleAssignment.vehicleIndex);
                for (Item item : vehicleAssignment.items) {
                    System.out.println("--Assigned Item: " + item.getId() + " - " + item.getCbm());
                }
            }
        }

    }

    ////////////CBM DIVIDE////////////////////////////////////////////////////////////////////

    public List<VehicleAssignment> getItemsAndDivide(List<Item> items) {
        // Get vehicle types
        List<VehicleType> vehicleTypes = vehicleTypeRepository.findAll();
        // Sort vehicle types by capacity
        vehicleTypes.sort(Comparator.comparing(VehicleType::getMaxCapacity));

        // Create a new list for this invocation
        List<VehicleAssignment> vehicleAssignments = new ArrayList<>();

        System.out.println("\n///////////Items to be divided into vehicles////////////");
        if (divideGoods(items, vehicleTypes, vehicleAssignments) == 1) {
            System.out.println("Items cannot be divided into vehicles");
        } else {
            System.out.println("///////////Items divided into vehicles////////////\n\n");
            return vehicleAssignments;
        }
        return List.of();
    }


    public int divideGoods(List<Item> items, List<VehicleType> vehicleTypes, List<VehicleAssignment> vehicleAssignments) {

        // get total of item cbm
        BigDecimal totalCbm = new BigDecimal("0.00");

        for (Item item : items) {
            System.out.println("-Item: " + item.getId() + " - " + item.getCbm());
            totalCbm = totalCbm.add(item.getCbm());
        }

        for (VehicleType vehicleType : vehicleTypes) {
            System.out.println("Vehicle Type: " + vehicleType.getId() + " - " + vehicleType.getMaxCapacity() + "......");
            if (totalCbm.compareTo(vehicleType.getMaxCapacity()) <= 0) {
                createVehicleAssignment(vehicleType, items, vehicleAssignments);
                System.out.println("*Vehicle selected for bulk: " + vehicleType.getId() + " - " + totalCbm);
                return 0;
            }
        }

        BigDecimal currentWeight = vehicleTypes.getLast().getMaxCapacity();
        int i = 0;

        // Iterate through items while currentWeight - item's cbm > 0
        while (i < items.size() && currentWeight.compareTo(items.get(i).getCbm()) >= 0) {
            Item item = items.get(i);
            System.out.println("*vehicle selected for item (exceeded): " + item.getId() + " - CBM: " + item.getCbm());
            currentWeight = currentWeight.subtract(item.getCbm());
            i++;
        }

        if (i == 0) {
            return 1;
        }

        createVehicleAssignment(vehicleTypes.getLast(), items.subList(0, i), vehicleAssignments);

        // get items in item list after index i
        List<Item> remainingItems = items.subList(i, items.size());
        if (remainingItems.size() > 0) {
            divideGoods(remainingItems, vehicleTypes, vehicleAssignments);
        }

        return 0;
    }

    ////////////FIN////////////////////////////////////////////////////////////////////



    // Helper method to create a new VehicleAssignment
    private void createVehicleAssignment(VehicleType vehicleType, List<Item> items, List<VehicleAssignment> vehicleAssignments) {
        // Determine the next index for this vehicle type
        int vehicleIndex = getNextIndexForVehicleType(vehicleType, vehicleAssignments);

        // Create and add the assignment
        VehicleAssignment assignment = new VehicleAssignment();
        assignment.vehicleType = vehicleType;
        assignment.vehicleIndex = vehicleIndex;
        assignment.items = new ArrayList<>(items); // Copy the items list

        vehicleAssignments.add(assignment);
    }

    // Helper method to get the next index for a vehicle type
    private int getNextIndexForVehicleType(VehicleType vehicleType, List<VehicleAssignment> vehicleAssignments) {
        int maxIndex = vehicleAssignments.stream()
                .filter(va -> va.vehicleType.equals(vehicleType))
                .mapToInt(va -> va.vehicleIndex)
                .max()
                .orElse(0); // Start with 0 if no vehicles of this type exist
        return maxIndex + 1;
    }
}
