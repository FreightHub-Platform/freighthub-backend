package com.freighthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummaryDto {
        int totalUsers;
        int activeUsers;
        int inactiveUsers;
        int pendingUsers;
        String date;

}
