package com.abuob.parking.service;

public enum ParkingApiErrorEnum {

    START_DATE_MISSING(-5, "startDate is missing"),
    END_DATE_MISSING(-10, "endDate is missing"),
    START_DATE_END_DATE_SAME(-15, "startDate and endDate are the same."),
    DATE_DIFFERENT_TZ(-20, "startDate and endDate have different timezones specified."),
    DATE_RANGE_SPANS_MULTIPLE_DAYS(-30, "startDate to endDate spans multiple days. Please narrow the date interval"),
    DATE_RANGE_SPANS_MULTIPLE_RATE(-40, "Multiple parking rates found for startDate to endDate. Please narrow the date interval");

    private Integer errorCode;

    private String errorMessage;

    ParkingApiErrorEnum(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
