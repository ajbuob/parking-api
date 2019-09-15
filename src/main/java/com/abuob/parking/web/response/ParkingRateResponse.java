package com.abuob.parking.web.response;

import java.time.ZonedDateTime;

public class ParkingRateResponse extends BaseResponse {

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private String price;

    public ParkingRateResponse() {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
