package com.freighthub.core.repository;

import com.freighthub.core.dto.TransactionsDto;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.Transactions;
import com.freighthub.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {

    @Transactional
    @Query("SELECT t FROM Transactions t WHERE t.userId = :driver ORDER BY t.transactionTime DESC")
    List<Transactions> findByUserId(User driver);

    @Transactional
    @Query("SELECT t FROM Transactions t WHERE t.orderId = :order ORDER BY t.transactionTime DESC")
    List<Transactions> findByOrderId(Order order);

}