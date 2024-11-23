package com.freighthub.core.repository;

import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    @Query("SELECT COUNT(i) = 0 FROM PurchaseOrder i WHERE i.id = :poId AND i.status <> :status")
    @Transactional
    boolean areAllPurchaseOrdersCompleted(@Param("poId") Integer poId, @Param("status") OrderStatus status);

    @Transactional
    List<PurchaseOrder> findByOrderId(Order orderId);
}
