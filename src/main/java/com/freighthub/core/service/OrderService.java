package com.freighthub.core.service;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.ItemDto;
import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.dto.PurchaseOrderDto;
import com.freighthub.core.entity.*;
import com.freighthub.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @Transactional
    public void saveOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setPickupDate(orderDto.getPickupDate());
        order.setFromTime(orderDto.getFromTime());
        order.setToTime(orderDto.getToTime());
        order.setPickupLocation(orderDto.getPickupLocation());

        User user = userRepository.findById((long) orderDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        order.setUserId(user);
        order = orderRepository.save(order);

        for (PurchaseOrderDto purchaseOrderDto : orderDto.getPurchaseOrders()) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPoNumber(purchaseOrderDto.getPoNumber());
            purchaseOrder.setStoreName(purchaseOrderDto.getStoreName());
            purchaseOrder.setDropDate(purchaseOrderDto.getDropDate());
            purchaseOrder.setDropTime(purchaseOrderDto.getDropTime());
            purchaseOrder.setContactNumber(purchaseOrderDto.getContactNumber());
            purchaseOrder.setEmail(purchaseOrderDto.getEmail());
            purchaseOrder.setAddress(purchaseOrderDto.getAddress());
            purchaseOrder.setLtlFlag(purchaseOrderDto.isLtlFlag());
            purchaseOrder.setDropLocation(purchaseOrderDto.getDropLocation());
            purchaseOrder.setOrderId(order);

            Integer otp = new Random().nextInt(9000) + 1000;
            purchaseOrder.setOtp(otp);

            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

            for (ItemDto itemDto : purchaseOrderDto.getItems()) {
                Item item = new Item();
                item.setItemName(itemDto.getItemName());
                item.setWeight(itemDto.getWeight());
                item.setCbm(itemDto.getCbm());
                item.setRefrigerated(itemDto.getRefrigerated());
                item.setHazardous(itemDto.getHazardous());
                item.setPerishable(itemDto.getPerishable());
                item.setFragile(itemDto.getFragile());
                item.setSequenceNumber(itemDto.getSequenceNumber());
                item.setSafeDelivery(itemDto.getSafeDelivery());
                item.setPoId(purchaseOrder); // Set foreign key

                ItemType type = itemTypeRepository.findById((int) itemDto.getITypeId()).orElseThrow(() -> new RuntimeException("Item Type not found"));
                item.setITypeId(type);

                itemRepository.save(item);
            }
        }

    }

    @Transactional(readOnly = true)
    public Object getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersForConsigner(GetAnyId userRequest) {
        // Fetch the orders for the consigner using the user ID from the request
        User user = new User();
        user.setId(userRequest.getId());
        return orderRepository.findByUserId(user);
    }

    @Transactional(readOnly = true)
    public Object getOrderById(GetAnyId order) {
        return orderRepository.findById((long) order.getId());
    }
}
