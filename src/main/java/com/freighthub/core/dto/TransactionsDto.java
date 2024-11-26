package com.freighthub.core.dto;

import com.freighthub.core.entity.User;
import com.freighthub.core.enums.OrderStatus;
import com.freighthub.core.enums.TransactionType;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.freighthub.core.entity.Transactions}
 */
@Value
public class TransactionsDto implements Serializable {
    Integer id;
    BigDecimal amount;
    BigDecimal profit;
    TransactionType type;
    OrderStatus state;
    LocalDateTime transactionTime;
    Integer userId;
    Integer routeId;

}