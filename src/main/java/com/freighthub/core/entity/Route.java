package com.freighthub.core.entity;

import com.freighthub.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
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

    @Column(name = "time_minutes")
    private Integer timeMinutes;

    @Column(name = "status", insertable = false)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "v_typeid", referencedColumnName = "v_typeid")
    private VehicleType vTypeId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicleid")
    private Vehicle vehicleId;
}