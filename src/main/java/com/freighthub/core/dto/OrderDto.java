package com.freighthub.core.dto;

import com.freighthub.core.entity.User;
import com.freighthub.core.enums.OrderStatus;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for {@link com.freighthub.core.entity.Order}
 */
@Value
public class OrderDto implements Serializable {
    int id;
    LocalDateTime orderTime;
    LocalDate pickupDate;
    LocalTime fromTime;
    LocalTime toTime;
    Point pickupLocation;
    OrderStatus status;
    User userId;
    List<PurchaseOrderDto> purchaseOrders;
}