package com.freighthub.core.entity;

import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VerifyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicleid", nullable = false)
    private Integer id;

    @Column(name = "license_no", nullable = false)
    private String licenseNo;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "make", nullable = false)
    private String make;

    @Column(name = "crane_flag")
    private Boolean craneFlag;

    @Column(name = "refrig_flag")
    private Boolean refrigFlag;

    @Column(name = "availability", columnDefinition = "VARCHAR(255) DEFAULT 'unavailable'")
    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.unavailable;

    @Column(name = "verify_status", columnDefinition = "VARCHAR(255) DEFAULT 'pending'")
    @Enumerated(EnumType.STRING)
    private VerifyStatus verifyStatus = VerifyStatus.pending;

    @Column(name = "reg_pic")
    private String registrationPic;

    @Column(name = "v_pic1")
    private String vehiclePic1;

    @Column(name = "v_pic2")
    private String vehiclePic2;

    @Column(name = "v_pic3")
    private String vehiclePic3;

    @Column(name = "v_pic4")
    private String vehiclePic4;

    @ManyToOne
    @JoinColumn(name = "v_typeid", referencedColumnName = "v_typeid")
    private VehicleType vTypeId;

    @ManyToOne
    @JoinColumn(name = "fleet_owner_uid", referencedColumnName = "uid")
    private FleetOwner fleetOwnerId;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "uid")
    private Driver driverId;

    @ManyToOne
    @JoinColumn(name = "verified_by", referencedColumnName = "uid")
    private ReviewBoard reviewBoardId;

    @Column(name = "verify_time", updatable = false)
    private LocalDateTime verifyTime;

    @ManyToMany
    @JoinTable(
            name = "vehicle_reject_reason",
            joinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicleid"),
            inverseJoinColumns = @JoinColumn(name = "reason_id", referencedColumnName = "reason_id")
    )
    private Set<RejectReason> vehicleRejectReasons = new HashSet<>();

}