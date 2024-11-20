package com.freighthub.core.repository;

import com.freighthub.core.entity.Item;
import com.freighthub.core.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("SELECT COUNT(i) = 0 FROM Item i WHERE i.poId.id = :poId AND i.status <> :status")
    @Transactional
    boolean areAllItemsCompleted(@Param("poId") Integer poId, @Param("status") OrderStatus status);
}