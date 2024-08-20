package com.freighthub.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reject_reason")
public class RejectReason {

    @Id
    @Column(name = "reason_id", unique = true, nullable = false)
    private int id;

    @Column(name = "reason", nullable = false)
    private String reason;

    @ManyToMany(mappedBy = "userRejectReasons")
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "vehicleRejectReasons")
    private Set<Vehicle> vehicles = new HashSet<>();

}
