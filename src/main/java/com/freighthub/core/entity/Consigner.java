package com.freighthub.core.entity;

import com.freighthub.core.enums.VerifyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "consigners")
@PrimaryKeyJoinColumn(name = "uid") // This will make 'uid' the primary key and foreign key
public class Consigner extends User{

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "brn", unique = true)
    private String brn;

    @Column(name = "reg_doc", columnDefinition = "bytea")
    private byte[] regDoc;

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

    @Column(name = "logo")
    private String logo;

    @Column(name = "verify_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerifyStatus verifyStatus = VerifyStatus.pending;

    @Column(name = "completion")
    private int completion;

//    @ManyToOne
//    @JoinColumn(name = "verified_by", referencedColumnName = "uid")
//    private ReviewBoard reviewBoardId;

    @Column(name = "verify_time", updatable = false)
    private LocalDateTime verifyTime;
}
