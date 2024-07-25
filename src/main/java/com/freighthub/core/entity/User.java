// src/main/java/com/freighthub/core/entity/User.java

package com.freighthub.core.entity;

import com.freighthub.core.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private UserRole role;

}
