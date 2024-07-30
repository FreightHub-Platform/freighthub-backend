package com.freighthub.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_types")
public class ItemType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_typeid", nullable = false)
    private Integer id;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "refrigerated")
    private Boolean refrigerated;

    @Column(name = "hazardous")
    private Boolean hazardous;

    @Column(name = "perishable")
    private Boolean perishable;

    @Column(name = "fragile")
    private Boolean fragile;



}