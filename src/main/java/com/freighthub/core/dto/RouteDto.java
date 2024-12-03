package com.freighthub.core.dto;

import com.freighthub.core.enums.ContainerType;
import com.freighthub.core.enums.OrderStatus;
import lombok.Value;
import org.locationtech.jts.geom.LineString;

import java.awt.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.freighthub.core.entity.Route}
 */
@Value
public class RouteDto implements Serializable {
    Integer id;
    String path;
    BigDecimal distanceKm;
    BigDecimal actualDistanceKm;
    BigDecimal cost;
    Integer timeMinutes;
    OrderStatus status;
    ContainerType containerType;
    Integer vTypeId;
    Integer vehicleId;
}