package com.freighthub.core.dto;

import com.freighthub.core.enums.UserRole;
import com.freighthub.core.enums.VerifyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.freighthub.core.entity.Consigner}
 */
@Value
@Setter
@Getter
public class ConsignerDto implements Serializable {
    Integer id;
    String username;
    @Enumerated(EnumType.STRING)
    UserRole role;
    String businessName;
    String brn;
    String regDoc;
    String mainNumber;
    String altNumber;
    String addressLine1;
    String addressLine2;
    String city;
    String province;
    String postalCode;
    String logo;
    @Enumerated(EnumType.STRING)
    VerifyStatus verifyStatus;
    Integer completion;
}