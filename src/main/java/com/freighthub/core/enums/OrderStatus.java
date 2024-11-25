package com.freighthub.core.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    created, //done
    pending, //not
    accepted, //done
    ongoing, //done
    completed, //done
    unfulfilled //ugh.. done
}
