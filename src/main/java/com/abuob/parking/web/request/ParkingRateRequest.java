package com.abuob.parking.web.request;

import java.time.ZonedDateTime;

public class ParkingRateRequest {

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    public ParkingRateRequest() {
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }
}
