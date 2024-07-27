package com.freighthub.core.dto;

import com.freighthub.core.enums.UserRole;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.freighthub.core.entity.FleetOwner}
 */
@Value
public class FleetOwnerDto implements Serializable {
    int id;
    String username;
    UserRole role;
    String companyName;
    String brn;
    String mainNumber;
    String altNumber;
    String addressLine1;
    String addressLine2;
    String city;
    String province;
    String postalCode;
    Boolean verifyStatus;
    int completion;
}