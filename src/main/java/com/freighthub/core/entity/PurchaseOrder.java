package com.freighthub.core.entity;

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
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @Column(name = "poid", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "drop_date")
    private LocalDate dropDate;

    @Column(name = "drop_time")
    private LocalTime dropTime;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "status", insertable = false)
    private OrderStatus status;

    @Column(name = "address")
    private String address;

    @Column(name = "ltl_flag")
    private boolean ltlFlag;

    @Column(name = "drop_location", columnDefinition = "geography(Point, 4326)")
    private Point dropLocation;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderid")
    private Order orderId;

}
