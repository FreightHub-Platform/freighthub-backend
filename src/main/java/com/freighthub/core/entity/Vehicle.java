package com.freighthub.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.ContainerType;
import com.freighthub.core.enums.VerifyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @Column(name = "model")
    private String model;

    @Column(name = "make")
    private String make;

    @Column(name = "year")
    private String year;

    @Column(name = "color")
    private String color;

    @Column(name = "crane_flag")
    private Boolean craneFlag;

    @Column(name = "refrig_flag")
    private Boolean refrigFlag;

    @Column(name = "availability")
    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.unavailable;

    @Column(name = "verify_status")
    @Enumerated(EnumType.STRING)
    private VerifyStatus verifyStatus = VerifyStatus.pending;

    @Column(name = "container_type")
    @Enumerated(EnumType.STRING)
    private ContainerType containerType;

    @Column(name = "reg_pic")
    private String registrationPic;

    @Column(name = "v_pic1")
    private String frontPic;

    @Column(name = "v_pic2")
    private String rearPic;

    @Column(name = "v_pic3")
    private String side1Pic;

    @Column(name = "v_pic4")
    private String side2Pic;

    @Column(name = "v_pic5")
    private String trailerImage;

    @Column(name = "completion")
    private int completion;

    @Column(name = "revenue_li_pic")
    private String revenueLicensePic;

    @Column(name = "li_expiry")
    private LocalDate licenseExpiry;

    @Column(name = "insurance_pic")
    private String insurancePic;

    @Column(name = "insurance_expiry")
    private LocalDate insuranceExpiry;

    @Column(name = "registration_doc", columnDefinition = "bytea")
    private String registrationDoc;

    @ManyToOne
    @JoinColumn(name = "v_typeid", referencedColumnName = "v_typeid")
    private VehicleType vTypeId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "fleet_owner_uid", referencedColumnName = "uid")
    private FleetOwner fleetOwnerId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "driver_id", referencedColumnName = "uid")
    private Driver driverId;

    @ManyToOne
    @JsonIgnore
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