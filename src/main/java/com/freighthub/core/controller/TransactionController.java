package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.TransactionsDto;
import com.freighthub.core.entity.Route;
import com.freighthub.core.entity.Transactions;
import com.freighthub.core.service.ConsignerService;
import com.freighthub.core.service.OrderService;
import com.freighthub.core.service.TransactionService;
import com.freighthub.core.util.ApiResponse;
import jakarta.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions/")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConsignerService consignerService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllTransactions() {
        try {
            List<?> transactions = transactionService.getAllTransactions();
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users", transactions);
            logger.info("Transactions: {}", transactions);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getTransactionById(@RequestBody GetAnyId transactionId) {
        try {
            Transactions transaction = transactionService.getTransactionById(transactionId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get transaction", transaction);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<?>> creditTransaction(@RequestBody TransactionsDto transaction) {
        try {
            Transactions transactions = transactionService.creditTransaction(transaction);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Credit transaction", transactions);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error crediting transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<?>> debitTransaction(@RequestBody TransactionsDto transaction) {
        try {
            Transactions transactions = transactionService.debitTransaction(transaction);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Debit transaction", transactions);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error debiting transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<?>> approveTransaction(@RequestBody GetAnyId transactionId) {
        try {
            Transactions transactions = transactionService.approveTransaction(transactionId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Approve transaction", transactions);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error approving transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<?>> rejectTransaction(@RequestBody GetAnyId transactionId) {
        try {
            Transactions transactions = transactionService.rejectTransaction(transactionId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Reject transaction", transactions);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error rejecting transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all transactions for driver
    @PostMapping("/wallet")
    public ResponseEntity<ApiResponse<?>> getDriverTransactions(@RequestBody GetAnyId driverId) {
        try {
            Map<String, Object> data = transactionService.getDriverTransactions(driverId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get driver transactions", data);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting driver transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all transactions for an order
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<?>> getOrderTransactions(@RequestBody GetAnyId orderId) {
        try {
            List<Route> data = orderService.getOrderTransactions(orderId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get order transactions", data);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting order transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all transactions for a consigner
    @PostMapping("/consigner")
    public ResponseEntity<ApiResponse<?>> getConsignerTransactions(@RequestBody GetAnyId consignerId) {
        try {
            List<Route> data = consignerService.getConsignerTransactions(consignerId.getId());
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get consigner transactions", data);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Error getting consigner transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
