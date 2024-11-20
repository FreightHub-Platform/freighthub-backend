package com.freighthub.core.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    created,
    pending,
    accepted,
    ongoing,
    completed,
    unfulfilled
}
