package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetAnyId implements Serializable {
    private int id;

    // No-argument constructor needed for deserialization
    public GetAnyId() {}

    // Constructor with ID for convenience
    public GetAnyId(int id) {
        this.id = id;
    }
}
