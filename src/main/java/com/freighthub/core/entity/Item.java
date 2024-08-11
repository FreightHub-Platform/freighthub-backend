package com.freighthub.core.entity;

import com.freighthub.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemid", nullable = false)
    private Integer id;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(name = "cbm", precision = 10, scale = 2)
    private BigDecimal cbm;

    @Column(name = "refrigerated")
    private Boolean refrigerated;

    @Column(name = "hazardous")
    private Boolean hazardous;

    @Column(name = "perishable")
    private Boolean perishable;

    @Column(name = "fragile")
    private Boolean fragile;

    @Column(name = "status", insertable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "safe_delivery")
    private Boolean safeDelivery;

    @ManyToOne
    @JoinColumn(name = "po_id", referencedColumnName = "poid")
    private PurchaseOrder poId;

    @ManyToOne
    @JoinColumn(name = "i_type_id", referencedColumnName = "i_typeid")
    private ItemType iTypeId;

    @ManyToOne
    @JoinColumn(name = "route_id", referencedColumnName = "routeid")
    private Route routeId;
}