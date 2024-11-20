package com.freighthub.core.service;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.repository.ItemRepository;
import com.freighthub.core.repository.PurchaseOrderRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public List<Item> getAllItems() { return itemRepository.findAll(); }

    @Transactional(readOnly = true)
    public Item getItemById(int id) { return itemRepository.findById(id).orElse(null); }

    @Transactional
    public void completeItem(@Valid OrderStatusDto itemDto) {
        // Find the item by ID
        Item item = itemRepository.findById(itemDto.getItem_id())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Fetch the associated PurchaseOrder
        PurchaseOrder purchaseOrder = item.getPoId();
        if (purchaseOrder == null) {
            throw new RuntimeException("Purchase Order not found for the item");
        }

        // Check if the OTP matches
        if (!purchaseOrder.getOtp().equals(itemDto.getOtp())) {
            throw new RuntimeException("Invalid OTP for the Purchase Order");
        }

        // Update the status if OTP matches
        item.setStatus(OrderStatus.completed);
        itemRepository.save(item);

        // Check if all items under the same PurchaseOrder are completed
        Integer poId = item.getPoId().getId();
        boolean allCompleted = itemRepository.areAllItemsCompleted(poId, OrderStatus.completed);

        if (allCompleted) {
            // Update PurchaseOrder status
            purchaseOrder = item.getPoId();
            purchaseOrder.setStatus(OrderStatus.completed); // Assuming PurchaseOrder has a status field
            purchaseOrderRepository.save(purchaseOrder);
        }
    }
}
