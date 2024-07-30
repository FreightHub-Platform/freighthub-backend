package com.freighthub.core.repository;

import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(User userId);
}