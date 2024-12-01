package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetAnyId implements Serializable {
    private int id;
    private String yearMonth;

    // No-argument constructor needed for deserialization
    public GetAnyId() {}

    // Constructor with ID and Year-Month for convenience
    public GetAnyId(int id, String yearMonth) {
        this.id = id;
        this.yearMonth = yearMonth;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
}
