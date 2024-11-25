package com.freighthub.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freighthub.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "orderid", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_time", updatable = false, insertable = false)
    private LocalDateTime orderTime;

    @Column(name = "pickup_date")
    private LocalDate pickupDate;

    @Column(name = "from_time")
    private LocalTime fromTime;

    @Column(name = "to_time")
    private LocalTime toTime;

    @Column(name = "pickup_location", columnDefinition = "geography(Point, 4326)")
    private Point pickupLocation;

    @Column(name = "status", insertable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User userId;
}
