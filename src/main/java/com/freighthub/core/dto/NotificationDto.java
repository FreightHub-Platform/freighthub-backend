package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto implements Serializable {

    private int id;
    private String message;
    private boolean read;
    private String notificationTime;
    private int userId;
}
