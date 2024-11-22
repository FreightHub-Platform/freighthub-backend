package com.freighthub.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "admin_details")
@PrimaryKeyJoinColumn(name = "uid")
public class AdminDetails extends User {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mobile_number", nullable = false, unique = true, length = 15)
    private String mobileNumber;

    @Column(name = "created_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "active_status", nullable = false)
    private Boolean activeStatus;

    @Column(name = "last_login_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    // Automatically set timestamps
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
        if (activeStatus == null) {
            activeStatus = Boolean.TRUE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }
}
