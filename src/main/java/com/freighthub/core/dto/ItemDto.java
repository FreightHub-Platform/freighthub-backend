package com.freighthub.core.dto;

import com.freighthub.core.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.freighthub.core.entity.Item}
 */
@Value
@Getter
@Setter
public class ItemDto implements Serializable {
    Integer id;
    String itemName;
    BigDecimal weight;
    BigDecimal cbm;
    Boolean refrigerated;
    Boolean hazardous;
    Boolean perishable;
    Boolean fragile;
    OrderStatus status;
    Integer sequenceNumber;
    Boolean safeDelivery;
    int poId;
    int iTypeId;
    int routeId;
}