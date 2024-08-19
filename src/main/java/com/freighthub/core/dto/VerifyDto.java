package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VerifyDto implements Serializable {
    private int id;
    private int reviewId;

    // No-argument constructor needed for deserialization
    public VerifyDto() {}

    // Constructor with ID for convenience
    public VerifyDto(int id, int reviewId) {
        this.id = id;
        this.reviewId = reviewId;
    }
}