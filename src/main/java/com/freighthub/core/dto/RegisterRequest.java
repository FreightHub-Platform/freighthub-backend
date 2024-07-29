package com.freighthub.core.dto;

import com.freighthub.core.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotNull
    private int id;

    private String username;

    private String password;

    private UserRole role;
}
