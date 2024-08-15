package com.freighthub.core.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drivers")
@PrimaryKeyJoinColumn(name = "uid")
public class Driver extends User {

    @Column(name = "nic")
    private String nic;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "fname")
    private String fName;

    @Column(name = "lname")
    private String lName;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "li_front_pic")
    private String liFrontPic;

    @Column(name = "li_rear_pic")
    private String liRearPic;

//    @Type(type = "org.hibernate.spatial.JTSGeometryType")
//    @Convert(converter = GeometryConverter.class)
    @Column(name = "hometown_location", columnDefinition = "geography(Point, 4326)")
    private Point hometownLocation;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "verify_status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean verifyStatus = false;

    @Column(name = "availability", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean availability = false;

    @Column(name = "completion")
    private int completion;

    // Many-to-one relationship with FleetOwner
    @ManyToOne
    @JoinColumn(name = "fleet_owner_uid", referencedColumnName = "uid")
    private FleetOwner fleetOwnerId;

    @ManyToOne
    @JoinColumn(name = "verified_by", referencedColumnName = "uid")
    private ReviewBoard userId;

    @Column(name = "verify_time", updatable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

}
