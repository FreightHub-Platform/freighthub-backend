package com.freighthub.core.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    pending,
    accepted,
    ongoing,
    completed,
    unfulfilled
}
