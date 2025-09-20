package com.bangvan.dto.response;


import lombok.*;

@Builder
public class ApiResponse {
    private int code;
    private String message;
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ApiResponse success(int code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    public static ApiResponse error(int code, String message) {
        return new ApiResponse(code, message, null);
    }


}
