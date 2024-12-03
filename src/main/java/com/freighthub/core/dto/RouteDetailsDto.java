package com.freighthub.core.dto;

import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.PurchaseOrder;
import com.freighthub.core.entity.Route;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RouteDetailsDto implements Serializable {

    private Route route;
    private List<PurchaseOrderDto> purchaseOrders;
    private OrderDto order;
    private ItemSummaryDto itemSummary;
    private String consignerBusinessName;

    public RouteDetailsDto(Route route, List<PurchaseOrderDto> purchaseOrders, ItemSummaryDto itemSummary, String consignerBusinessName, OrderDto order) {
        this.route = route;
        this.purchaseOrders = purchaseOrders;
        this.itemSummary = itemSummary;
        this.consignerBusinessName = consignerBusinessName;
        this.order = order;
    }

    @Getter
    @Setter
    public static class ItemSummaryDto {
        private BigDecimal totalWeight;
        private BigDecimal totalCbm;
        private List<String> itemTypeNames;
    }
}
