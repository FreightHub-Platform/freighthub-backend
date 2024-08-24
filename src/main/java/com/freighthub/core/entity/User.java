// src/main/java/com/freighthub/core/entity/User.java

package com.freighthub.core.entity;

import com.freighthub.core.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // Use JOINED strategy for separate tables
public class User {
    @Id
    @Column(name = "uid", unique = true, nullable = false)
    private int id;

    @Column(name = "email", unique = true, nullable = false)
    private String username;

//    @Column(name = "password", nullable = false)
//    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @ManyToMany
    @JoinTable(
            name = "user_reject_reason",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "uid"),
            inverseJoinColumns = @JoinColumn(name = "reason_id", referencedColumnName = "reason_id")
    )
    private Set<RejectReason> userRejectReasons = new HashSet<>();

}
