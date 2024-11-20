package com.freighthub.core.service;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderStatusDto;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.repository.PurchaseOrderRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public List<?> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PurchaseOrder getPurchaseOrderById(int id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }

    public void completePurchaseOrder(@Valid OrderStatusDto purchaseOrderDto) {
        // Find the Purchase Order by ID
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getPo_id())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        // Check if the OTP matches
        if (!purchaseOrder.getOtp().equals(purchaseOrderDto.getOtp())) {
            throw new RuntimeException("Invalid OTP for the Purchase Order");
        }

        // Update the status if OTP matches
        purchaseOrder.setStatus(OrderStatus.completed);
        purchaseOrderRepository.save(purchaseOrder);


    }
}
