package com.freighthub.core.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fleet_owners")
@PrimaryKeyJoinColumn(name = "uid") // This will make 'uid' the primary key and foreign key
public class FleetOwner extends User{

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "brn")
    private String brn;

    @Column(name = "main_number")
    private String mainNumber;

    @Column(name = "alt_number")
    private String altNumber;

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

    @Column(name = "completion")
    private int completion;
}
