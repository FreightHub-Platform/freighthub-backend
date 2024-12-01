package com.freighthub.core.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    created, //
    pending, //
    accepted, //route
    arriving, //route
    loading, //route
    ongoing, //route
    unloading, //po - route
    completed, //po - route otp
    unfulfilled, //po - route otp
    cancelled, //order



}
