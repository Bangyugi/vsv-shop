package com.bangvan.exception;

import org.springframework.http.HttpStatus;



public enum ErrorCode {
    INTERNAL_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User does not exist", HttpStatus.NOT_FOUND),
    USER_UNAUTHENTICATED(1003, "User is not authenticated", HttpStatus.UNAUTHORIZED),
    USER_LOGGED_OUT(1004, "User is logged out", HttpStatus.UNAUTHORIZED),
    USER_TOKEN_INCORRECT(1005, "User token is incorrect", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),

    EMAIL_EXISTED(1006, "Email already exists", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1007, "Email not found", HttpStatus.NOT_FOUND),
    EMAIL_CAN_NOT_UPDATE(1008, "Email can not update", HttpStatus.BAD_REQUEST),

    PHONE_EXISTED(1009, "Phone already exists", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_CAN_NOT_UPDATE(1010, "Phone number can not update", HttpStatus.BAD_REQUEST),

    USERNAME_EXISTED(1011, "Username already exists", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1012, "User already exists", HttpStatus.BAD_REQUEST),
    USER_EMAIL_OR_PHONE_CAN_NOT_CHANGE(1013, "User email or phone can not change", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1014, "Token expired", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1015, "Wrong password", HttpStatus.BAD_REQUEST),
    WRONG_OTP(1016, "Wrong OTP", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(1017, "User not verified", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1018, "Phone number already exists", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1019, "Invalid password", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1020, "Password not match", HttpStatus.BAD_REQUEST),
    PASSWORD_SHOULD_NOT_BE_SAME_AS_OLD(1021, "Password should not be same as old password", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(403,"User unauthenticated" , HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(403, "Role not found", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(403, "Invalid JWT token", HttpStatus.FORBIDDEN),
    INVALID_USER_TYPE(403, "Invalid user type", HttpStatus.FORBIDDEN),

    PROJECT_NOT_FOUND(404, "Project not found", HttpStatus.NOT_FOUND),
    EMAIL_REPEATED(1022,"You entered the same email",HttpStatus.BAD_REQUEST ),
    PHONE_REPEATED(1023,"You entered the same phone number",HttpStatus.BAD_REQUEST );


    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    private int code;
    private String message;
    private HttpStatus status;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
