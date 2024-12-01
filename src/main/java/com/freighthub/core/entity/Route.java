package com.freighthub.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freighthub.core.enums.ContainerType;
import com.freighthub.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routeid", nullable = false)
    private Integer id;

    @Column(name = "path", columnDefinition = "geography(LineString, 4326)")
//    @Type(type = "org.hibernate.spatial.JTSGeometryType")
    private LineString path;

    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "actual_distance_km", precision = 10, scale = 2)
    private BigDecimal actualDistanceKm;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "estd_cost", precision = 10, scale = 2)
    private BigDecimal estdCost;

    @Column(name = "profit", nullable = true)
    private BigDecimal profit;

    @Column(name = "crane_flag")
    private Boolean craneFlag;

    @Column(name = "refrig_flag")
    private Boolean refrigFlag;

    @Column(name = "time_minutes")
    private Integer timeMinutes;

    @Column(name = "status", insertable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "container_type")
    @Enumerated(EnumType.STRING)
    private ContainerType containerType;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "v_typeid", referencedColumnName = "v_typeid")
    private VehicleType vTypeId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicleid")
    private Vehicle vehicleId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "orderid")
    private Order orderId;
}