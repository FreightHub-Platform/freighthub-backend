package com.freighthub.core.dto;

import com.freighthub.core.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsDto {
    private String name;
    private String id;
    private String username;
    private String email;
    private UserRole role;
    private String status;
}
