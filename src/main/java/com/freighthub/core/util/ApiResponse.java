// src/main/java/com/freighthub/core/util/ApiResponse.java
package com.freighthub.core.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
