package com.abuob.parking.service;

public class ParkingServiceException extends Exception {

    private Integer errorCode;

    public ParkingServiceException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
