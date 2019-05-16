package com.example.calorietracker.API;

public enum APIReturnStatus {
    SUCEESS(200, "Success"),
    EMAIL_EXIST(401, "duplicate_email_exists"),
    USERNAME_EXIST(402, "duplicate_username_exists");

    private Integer code;
    private String message;

    APIReturnStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
