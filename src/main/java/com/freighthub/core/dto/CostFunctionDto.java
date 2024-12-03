package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CostFunctionDto implements Serializable {
    Integer id;
    BigDecimal dieselPrice;
    BigDecimal fixedCost;
    BigDecimal driverWage;
}
