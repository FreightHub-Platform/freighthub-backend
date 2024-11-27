package com.freighthub.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("iTypeId")
    Integer iTypeId;
    Integer routeId;

    public Integer getITypeId() {
        return iTypeId;
    }

    public void setITypeId(Integer iTypeId) {
        this.iTypeId = iTypeId;
    }


    public ItemDto() {
        // Default constructor required for Jackson
    }

    public ItemDto(String itemName, BigDecimal weight, BigDecimal cbm, Boolean refrigerated, Boolean hazardous, Boolean perishable, Boolean fragile, Integer iTypeId) {
        this.itemName = itemName;
        this.weight = weight;
        this.cbm = cbm;
        this.refrigerated = refrigerated;
        this.hazardous = hazardous;
        this.perishable = perishable;
        this.fragile = fragile;
        this.iTypeId = iTypeId;
    }

    public ItemDto(Integer id, String itemName, BigDecimal weight, BigDecimal cbm, OrderStatus status, Integer sequenceNumber) {
        this.id = id;
        this.itemName = itemName;
        this.weight = weight;
        this.cbm = cbm;
        this.status = status;
        this.sequenceNumber = sequenceNumber;
    }
}