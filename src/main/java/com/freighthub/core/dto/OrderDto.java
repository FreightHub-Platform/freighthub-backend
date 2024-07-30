package com.freighthub.core.dto;

import com.freighthub.core.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Value
@Getter
@Setter
public class OrderDto implements Serializable {
    int id;
    LocalDateTime orderTime;
    LocalDate pickupDate;
    LocalTime fromTime;
    LocalTime toTime;
    Point pickupLocation;
    OrderStatus status;
    int userId;
    List<PurchaseOrderDto> purchaseOrders;
}