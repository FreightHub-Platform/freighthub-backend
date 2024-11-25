package com.freighthub.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionid", nullable = false)
    private Integer id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "profit", nullable = true)
    private BigDecimal profit;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus state;

    @Column(name = "transaction_time", nullable = false, insertable = false)
    private LocalDateTime transactionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private Driver userId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "orderid")
    private Order orderId;
}