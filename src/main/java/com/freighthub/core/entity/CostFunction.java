package com.freighthub.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cost_function")
public class CostFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "costid", nullable = false)
    private Integer id;

    @Column(name = "diesel_price")
    private BigDecimal dieselPrice;

    @Column(name = "fixed_cost")
    private BigDecimal fixedCost;

    @Column(name = "driver_wage")
    private BigDecimal driverWage;
}
