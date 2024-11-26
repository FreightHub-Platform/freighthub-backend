package com.freighthub.core.service;

import com.freighthub.core.dto.TransactionsDto;
import com.freighthub.core.entity.*;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.enums.TransactionType;
import com.freighthub.core.repository.*;
import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Transactional
    public List<?> getAllTransactions() {
        return transactionsRepository.findAll();
    }

    @Transactional
    public Transactions getTransactionById(int id) {
        return transactionsRepository.findById(id).orElse(null);
    }

    @Transactional
    public Transactions creditTransaction(TransactionsDto transaction) {

        Driver driver = driverRepository.findById(Long.valueOf(transaction.getUserId())).orElse(null);
        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }

        Route route = routeRepository.findById(transaction.getRouteId()).orElse(null);
        if (route == null) {
            throw new RuntimeException("route not found");
        }

        Transactions newTransaction = new Transactions();
        newTransaction.setAmount(transaction.getAmount());
//        newTransaction.setProfit(transaction.getProfit());
        newTransaction.setType(TransactionType.credit);
        newTransaction.setState(OrderStatus.completed);
        newTransaction.setUserId(driver);
        newTransaction.setRouteId(route);
        return transactionsRepository.save(newTransaction);
    }

    @Transactional
    public Transactions debitTransaction(TransactionsDto transaction) {

            Driver driver = driverRepository.findById(Long.valueOf(transaction.getUserId())).orElse(null);
            if (driver == null) {
                throw new RuntimeException("Driver not found");
            }

            Transactions newTransaction = new Transactions();
            newTransaction.setAmount(transaction.getAmount());
            newTransaction.setType(TransactionType.debit);
            newTransaction.setState(OrderStatus.pending);
            newTransaction.setUserId(driver);
            return transactionsRepository.save(newTransaction);
    }

    @Transactional
    public Transactions approveTransaction(int id) {
        Transactions transaction = transactionsRepository.findById(id).orElse(null);
        if (transaction == null) {
            throw new RuntimeException("Transaction not found");
        }
        System.out.println(transaction.getAmount());
        transaction.setState(OrderStatus.completed);
        return transactionsRepository.save(transaction);
    }

    @Transactional
    public Transactions rejectTransaction(int id) {
        Transactions transaction = transactionsRepository.findById(id).orElse(null);
        if (transaction == null) {
            throw new RuntimeException("Transaction not found");
        }
        transaction.setState(OrderStatus.unfulfilled);
        return transactionsRepository.save(transaction);
    }

    @Transactional
    public Map<String, Object> getDriverTransactions(int id) {
        User driver = userRepository.findById((long) id).orElse(null);
        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }

        List<Transactions> transactionsList = transactionsRepository.findByUserId(driver);

        BigDecimal total = new BigDecimal(0);
        for (Transactions transaction : transactionsList) {
            if (transaction.getState() == OrderStatus.completed) {
                if (transaction.getType() == TransactionType.credit) {
                    total = total.add(transaction.getAmount());
                } else if (transaction.getType() == TransactionType.debit) {
                    total = total.subtract(transaction.getAmount());
                }
            }
        }

        // Create JSON-like map for response
        Map<String, Object> result = new HashMap<>();
        result.put("transactions", transactionsList);
        result.put("total", total);

        return result;
    }
}
