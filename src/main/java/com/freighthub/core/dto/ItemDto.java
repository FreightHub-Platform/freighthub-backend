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
    Integer poId;
    Integer iTypeId;
    Integer routeId;

    public ItemDto(Integer id, String itemName, BigDecimal weight, BigDecimal cbm, OrderStatus status, Integer sequenceNumber) {
        this.id = id;
        this.itemName = itemName;
        this.weight = weight;
        this.cbm = cbm;
        this.status = status;
        this.sequenceNumber = sequenceNumber;
    }
}