package com.freighthub.core.dto;

import com.freighthub.core.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderStatusDto implements Serializable {
    Integer item_id;
    Integer order_id;
    Integer route_id;
    Integer po_id;
    Integer otp;
    OrderStatus status;
}
