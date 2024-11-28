package com.freighthub.core.service;

import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.entity.VehicleType;
import com.freighthub.core.repository.ItemRepository;
import com.freighthub.core.repository.PurchaseOrderRepository;
import com.freighthub.core.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BasicAlgoService {

//    private BigDecimal lightWeight = new BigDecimal("5.00");
//    private BigDecimal mediumWeight = new BigDecimal("10.00");
//    private BigDecimal heavyWeight = new BigDecimal("50.00");

    private Map<VehicleType, List<Item>> itemForVehicles = new HashMap<>();

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private ItemService itemService;

    public void getItemsAndDivide() {
        PurchaseOrder po = purchaseOrderRepository.findById(51)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        // Get all items
        List<Item> items = (List<Item>) itemRepository.getItemsByPoId(po);
        //sort items by cmb desc
        items.sort(Comparator.comparing(Item::getCbm).reversed());
        // Get vehicle types
        List<VehicleType> vehicleTypes = vehicleTypeRepository.findAll();
        //sort vehicle types by capacity
        vehicleTypes.sort(Comparator.comparing(VehicleType::getMaxCapacity));


        if (divideGoods(items, vehicleTypes) == 1) {
            System.out.println("Items cannot be divided into vehicles");
        } else {
            System.out.println("Items divided into vehicles");
            //print itemForVehicles map
            for (Map.Entry<VehicleType, List<Item>> entry : itemForVehicles.entrySet()) {
                System.out.println("Vehicle Type: " + entry.getKey().getId());
                for (Item item : entry.getValue()) {
                    System.out.println("Item: " + item.getId() + " - " + item.getCbm());
                }
            }
        }
    }


    public int divideGoods(List<Item> items, List<VehicleType> vehicleTypes) {

        // get total of item cbm
        BigDecimal totalCbm = new BigDecimal("0.00");

        for (Item item : items) {
            System.out.println("Item: " + item.getId() + " - " + item.getCbm());
            totalCbm = totalCbm.add(item.getCbm());
        }
//        boolean flag = false;
        for (VehicleType vehicleType : vehicleTypes) {
            System.out.println("Vehicle Type: " + vehicleType.getId() + " - " + vehicleType.getMaxCapacity() + "......");
            if (totalCbm.compareTo(vehicleType.getMaxCapacity()) <= 0) {
                itemForVehicles.put(vehicleType, items);
                System.out.println("Vehicle Type: " + vehicleType.getId() + " - " + totalCbm);
                return 0;
            }
        }

        BigDecimal currentWeight = vehicleTypes.getLast().getMaxCapacity();
        int i = 0;

        // Iterate through items while currentWeight - item's cbm > 0
        while (i < items.size() && currentWeight.compareTo(items.get(i).getCbm()) >= 0) {
            Item item = items.get(i);
            System.out.println("Item within weight: " + item.getId() + " - CBM: " + item.getCbm());
            currentWeight = currentWeight.subtract(item.getCbm());
            i++;
        }

        if (i == 0) {
            return 1;
        }

        itemForVehicles.put(vehicleTypes.getLast(), items.subList(0, i));

        // get items in item list after index i
        List<Item> remainingItems = items.subList(i, items.size());
        if (remainingItems.size() > 0) {
            divideGoods(remainingItems, vehicleTypes);
        }

        return 0;


//        public void dividingAlgo(List<Item> items) {
//            // get total of item cbm
//            BigDecimal totalCbm = new BigDecimal("0.00");
//
//            for (Item item : items) {
//                System.out.println("Item: " + item.getId() + " - " + item.getCbm());
//                totalCbm = totalCbm.add(item.getCbm());
//            }
//
//            if (totalCbm.compareTo(lightWeight) < 0) {
//                itemForVehicles.put("light", items);
//                System.out.println("Light Weight");
//            } else if (totalCbm.compareTo(mediumWeight) < 0) {
//                itemForVehicles.put("medium", items);
//                System.out.println("Medium Weight");
//            } else if (totalCbm.compareTo(heavyWeight) < 0) {
//                itemForVehicles.put("heavy", items);
//                System.out.println("Heavy Weight");
//            } else {
//                BigDecimal currentWeight = heavyWeight;
//                int i = 0;
//                System.out.println(items.size());
//
//                // Iterate through items while currentWeight - item's cbm > 0
//                while (i < items.size() && currentWeight.compareTo(items.get(i).getCbm()) >= 0) {
//                    Item item = items.get(i);
//                    System.out.println("Item within weight: " + item.getId() + " - CBM: " + item.getCbm());
//
//                    currentWeight = currentWeight.subtract(item.getCbm());
//                    i++;
//                }
//
//                itemForVehicles.put("heavy", items.subList(0, i));
//
//                // get items in item list after index i
//                List<Item> remainingItems = items.subList(i, items.size());
//                if (remainingItems.size() > 0) {
//                    dividingAlgo(remainingItems);
//                }
//            }

    }
}
