package com.freighthub.core.repository;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(User userId);

    @Transactional
    @Query("SELECT o FROM Order o WHERE o.userId = :consigner AND o.orderTime >= :startOfMonth AND o.orderTime <= :endOfMonth")
    Optional<List<Order>> findByUserIdThisMonth(
            @Param("consigner") User consigner,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );

    @Transactional
    @Query("SELECT DISTINCT o.pickupDate FROM Order o WHERE o.userId.id = :id")
    List<Object> findDistinctMonthsByConsignerId(int id);

    @Transactional
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Order findByid(Integer id);
}