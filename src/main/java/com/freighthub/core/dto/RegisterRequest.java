package com.freighthub.core.dto;

import com.freighthub.core.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RegisterRequest implements Serializable {

    private int id;

    private String username;

    private String password;

    private UserRole role;

    private String fName;

    private String lName;
}
