package com.abuob.parking.web.request;

import java.util.List;

public class ParkingRateListWrapper {

    List<ParkingRateCreateRequest> rates;

    public ParkingRateListWrapper() {
    }

    public List<ParkingRateCreateRequest> getRates() {
        return rates;
    }

    public void setRates(List<ParkingRateCreateRequest> rates) {
        this.rates = rates;
    }
}
