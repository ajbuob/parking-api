package com.abuob.parking.web.response;

public abstract class BaseResponse {

    private Integer statusCode;

    private String message;

    public BaseResponse() {
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
