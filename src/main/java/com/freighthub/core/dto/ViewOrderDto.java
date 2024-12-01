package com.freighthub.core.dto;

import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.entity.VehicleType;
import com.freighthub.core.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ViewOrderDto implements Serializable {

    Integer routeId;
    LineString path;
    BigDecimal distanceKm;
    BigDecimal actualDistanceKm;
    BigDecimal cost;
    Integer timeMinutes;
    OrderStatus status;
    Vehicle vehicleId;

    List<PurchaseOrderDto> purchaseOrderDtos;


    public ViewOrderDto(Integer id, LineString path, BigDecimal distanceKm, BigDecimal actualDistanceKm, BigDecimal cost, Integer timeMinutes, OrderStatus status, String vType, Vehicle vehicleId) {
        this.routeId = id;
        this.path = path;
        this.distanceKm = distanceKm;
        this.actualDistanceKm = actualDistanceKm;
        this.cost = cost;
        this.timeMinutes = timeMinutes;
        this.status = status;
        this.vehicleId = vehicleId;
    }
}
