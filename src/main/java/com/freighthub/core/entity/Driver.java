package com.freighthub.core.entity;

import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VerifyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

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

    @Column(name = "verify_status", columnDefinition = "VARCHAR(255) DEFAULT 'pending'")
    @Enumerated(EnumType.STRING)
    private VerifyStatus verifyStatus = VerifyStatus.pending;

    @Column(name = "availability", columnDefinition = "VARCHAR(255) DEFAULT 'unavailable'")
    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.unavailable;

    @Column(name = "completion")
    private int completion;

    // Many-to-one relationship with FleetOwner
    @ManyToOne
    @JoinColumn(name = "fleet_owner_uid", referencedColumnName = "uid")
    private FleetOwner fleetOwnerId;

    @ManyToOne
    @JoinColumn(name = "verified_by", referencedColumnName = "uid")
    private ReviewBoard reviewBoardId;

    @Column(name = "verify_time", updatable = false)
    private LocalDateTime verifyTime;
}
