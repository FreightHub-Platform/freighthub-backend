package com.freighthub.core.dto;

import com.freighthub.core.entity.Order;
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
    Integer otp;
    boolean ltlFlag;
    @NotNull
    LocationPoint dropLocation;
    Integer orderId;
    List<ItemDto> items;

    public PurchaseOrderDto() {
        // Default constructor required for Jackson
    }

    public PurchaseOrderDto(String poNumber, String storeName, LocalDate dropDate, LocalTime dropTime, String contactNumber, String email, boolean ltlFlag, String address, LocationPoint dropLocation, List<ItemDto> items) {
        this.poNumber = poNumber;
        this.storeName = storeName;
        this.dropDate = dropDate;
        this.dropTime = dropTime;
        this.contactNumber = contactNumber;
        this.email = email;
        this.ltlFlag = ltlFlag;
        this.address = address;
        this.dropLocation = dropLocation;
        this.items = items;
    }

    public PurchaseOrderDto(int id, String poNumber, String storeName, OrderStatus status, Double latitude, Double longitude) {
        this.id = id;
        this.poNumber = poNumber;
        this.storeName = storeName;
        this.status = status;
        this.dropLocation = new LocationPoint(latitude, longitude);
    }

    public PurchaseOrderDto(int id, String poNumber, String storeName, LocalDate dropDate, LocalTime dropTime, String contactNumber, String email, OrderStatus status, String address, Integer otp, boolean ltlFlag, Double latitude, Double longitude, Integer integer) {
        this.id = id;
        this.poNumber = poNumber;
        this.storeName = storeName;
        this.dropDate = dropDate;
        this.dropTime = dropTime;
        this.contactNumber = contactNumber;
        this.email = email;
        this.status = status;
        this.address = address;
        this.otp = otp;
        this.ltlFlag = ltlFlag;
        this.dropLocation = new LocationPoint(latitude, longitude);
        this.orderId = integer;
    }

    public PurchaseOrderDto(int id, String poNumber, String storeName, LocalDate dropDate, LocalTime dropTime, String contactNumber, String email, OrderStatus status, String address, Integer otp, boolean ltlFlag, Double latitude, Double longitude, Order orderId) {
        this.id = id;
        this.poNumber = poNumber;
        this.storeName = storeName;
        this.dropDate = dropDate;
        this.dropTime = dropTime;
        this.contactNumber = contactNumber;
        this.email = email;
        this.status = status;
        this.address = address;
        this.otp = otp;
        this.ltlFlag = ltlFlag;
        this.dropLocation = new LocationPoint(latitude, longitude);
        this.orderId = orderId.getId();
    }

    public PurchaseOrderDto(int id, Double latitude, Double longitude) {
        this.id = id;
        this.dropLocation = new LocationPoint(latitude, longitude);
    }

    public PurchaseOrderDto(int id, String poNumber, String storeName, OrderStatus status, String address, Double latitude, Double longitude) {
        this.id = id;
        this.poNumber = poNumber;
        this.storeName = storeName;
        this.status = status;
        this.address = address;
        this.dropLocation = new LocationPoint(latitude, longitude);
    }
}