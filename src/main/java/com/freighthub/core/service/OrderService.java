package com.freighthub.core.service;

import com.freighthub.core.dto.ItemDto;
import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.dto.PurchaseOrderDto;
import com.freighthub.core.dto.RegisterRequest;
import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.repository.ItemRepository;
import com.freighthub.core.repository.OrderRepository;
import com.freighthub.core.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ItemRepository itemRepository;

    public void saveOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setPickupDate(orderDto.getPickupDate());
        order.setFromTime(orderDto.getFromTime());
        order.setToTime(orderDto.getToTime());
        order.setPickupLocation(orderDto.getPickupLocation());

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

                itemRepository.save(item);
            }
        }

    }

    public Object getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersForConsigner(RegisterRequest user) {
        // Fetch the orders for the consigner using the user ID from the request
        return orderRepository.findByUserId(user.getId());
    }

    public Object getOrderById(OrderDto order) {
        return orderRepository.findById(order.getId());
    }
}
