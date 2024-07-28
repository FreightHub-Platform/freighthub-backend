package com.freighthub.core.dto;

import com.freighthub.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link com.freighthub.core.entity.PurchaseOrder}
 */
@Value
public class PurchaseOrderDto implements Serializable {
    int id;
    String poNumber;
    String storeName;
    @NotNull
    LocalDate dropDate;
    LocalTime dropTime;
    @NotNull
    String contactNumber;
    String email;
    OrderStatus status;
    String address;
    boolean ltlFlag;
    @NotNull
    Point dropLocation;
    OrderDto orderId;
}