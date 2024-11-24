package com.freighthub.core.repository;

import com.freighthub.core.entity.Item;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.entity.Route;
import com.freighthub.core.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("SELECT COUNT(i) = 0 FROM Item i WHERE i.poId.id = :poId AND i.status <> :status")
    @Transactional
    boolean areAllItemsCompleted(@Param("poId") Integer poId, @Param("status") OrderStatus status);

    @Transactional
    List<Item> findByPoIdAndRouteId(PurchaseOrder poId, Route routeId);

    @Transactional
    List<Item> findByPoId(PurchaseOrder poId);

    @Transactional
    @Query("SELECT DISTINCT i.poId.id FROM Item i WHERE i.routeId.id = :routeId")
    List<Integer> findDistinctPoIdsByRouteId(@Param("routeId") Integer routeId);

    // Fetch items by route ID ordered by sequence number
    @Transactional
    @Query("SELECT DISTINCT i.poId.id, i.sequenceNumber " +
            "FROM Item i WHERE i.routeId.id = :routeId ORDER BY i.sequenceNumber ASC")
    List<Object[]> findDistinctPoIdsAndSequenceNumbersByRouteId(@Param("routeId") Integer routeId);

    @Transactional
    @Query("SELECT i FROM Item i WHERE i.poId.id IN :poIds")
    List<Item> findByPoIdIn(@Param("poIds") List<Integer> poIds);

    @Transactional
    @Query("SELECT COUNT(i) = 0 FROM Item i WHERE i.routeId.id = :routeId AND i.status <> :status")
    boolean areAllItemsCompletedForRoute(@Param("routeId") Integer routeId, @Param("status") OrderStatus status);

    @Transactional
    List<Item> findByRouteId(Route routeId);

    @Transactional
    List<?> getItemsByPoId(PurchaseOrder poId);

    @Transactional
    @Query("SELECT SUM(i.weight) FROM Item i WHERE i.routeId = :routeId")
    BigDecimal calculateTotalWeight(@Param("routeId") Route routeId);

    @Transactional
    @Query("SELECT SUM(i.cbm) FROM Item i WHERE i.routeId = :routeId")
    BigDecimal calculateTotalCbm(@Param("routeId") Route routeId);

    @Transactional
    @Query("SELECT DISTINCT it.typeName FROM Item i JOIN i.iTypeId it WHERE i.routeId = :routeId")
    List<String> findDistinctItemTypeNames(@Param("routeId") Route routeId);
}