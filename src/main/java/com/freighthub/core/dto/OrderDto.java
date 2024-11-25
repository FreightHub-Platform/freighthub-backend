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


@Getter
@Setter
public class OrderDto implements Serializable {
    Integer id;
    LocalDateTime orderTime;
    LocalDate pickupDate;
    LocalTime fromTime;
    LocalTime toTime;
    LocationPoint pickupLocation;
    OrderStatus status;
    Integer userId;
    List<PurchaseOrderDto> purchaseOrders;

    public OrderDto(int id, LocalTime fromTime, LocalTime toTime, LocalDate pickupDate, OrderStatus status, Double latitude, Double longitude) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.pickupDate = pickupDate;
        this.status = status;
        this.pickupLocation = new LocationPoint(latitude, longitude);
    }

    public OrderDto(int id, LocalDateTime orderTime, LocalDate pickupDate, LocalTime fromTime, LocalTime toTime, LocationPoint locationPoint, OrderStatus status, Integer userId) {
        this.id = id;
        this.orderTime = orderTime;
        this.pickupDate = pickupDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.pickupLocation = locationPoint;
        this.status = status;
        this.userId = userId;
    }
}