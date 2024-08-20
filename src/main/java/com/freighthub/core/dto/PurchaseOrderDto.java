package com.freighthub.core.dto;

import com.freighthub.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for {@link com.freighthub.core.entity.PurchaseOrder}
 */
@Value
@Getter
@Setter
public class PurchaseOrderDto implements Serializable {
    Integer id;
    String poNumber;
    String storeName;
    LocalDate dropDate;
    LocalTime dropTime;
    String contactNumber;
    String email;
    OrderStatus status;
    String address;
    boolean ltlFlag;
    @NotNull
    Point dropLocation;
    Integer orderId;
    List<ItemDto> items;
}