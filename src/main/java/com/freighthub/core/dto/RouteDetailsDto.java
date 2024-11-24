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
    private List<PurchaseOrder> purchaseOrder;
    private Order order;
    private ItemSummaryDto itemSummary;
    private String consignerBusinessName;

    public RouteDetailsDto(Route route, List<PurchaseOrder> purchaseOrders, ItemSummaryDto itemSummary, String consignerBusinessName, Order order) {
        this.route = route;
        this.purchaseOrder = purchaseOrders;
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
