package com.freighthub.core.dto;

import com.freighthub.core.entity.User;
import com.freighthub.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for {@link com.freighthub.core.entity.Order}
 */
@Value
public class OrderDto implements Serializable {
    int id;
    LocalDateTime orderTime;
    @NotNull
    LocalDate pickupDate;
    @NotNull
    LocalTime fromTime;
    LocalTime toTime;
    @NotNull
    Point pickupLocation;
    OrderStatus status;
    User userId;
}