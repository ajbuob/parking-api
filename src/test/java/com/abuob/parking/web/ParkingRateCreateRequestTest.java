package com.abuob.parking.web;

import com.abuob.parking.web.request.ParkingRateCreateRequest;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingRateCreateRequestTest {

    @Test
    public void test_getDaysAsList_nullDay() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest();
        List<String> dayList = parkingRateCreateRequest.getDaysAsList();
        assertThat(dayList).isNotNull();
        assertThat(dayList).isEmpty();
    }

    @Test
    public void test_getDaysAsList_singleDay() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest("mon", "0600-1000", "America/Chicago", 5400);
        List<String> dayList = parkingRateCreateRequest.getDaysAsList();
        assertThat(dayList).isNotNull();
        assertThat(dayList).isNotEmpty();
        assertThat(dayList).containsExactly("mon");
    }

    @Test
    public void test_getDaysAsList_MultipleDays() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest("thurs,sat", "0600-1000", "America/Chicago", 5400);
        List<String> dayList = parkingRateCreateRequest.getDaysAsList();
        assertThat(dayList).isNotNull();
        assertThat(dayList).isNotEmpty();
        assertThat(dayList).containsExactly("thurs", "sat");
    }


    @Test
    public void test_getStartTime_Empty() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest();
        assertThat(parkingRateCreateRequest.getStartTime()).isNotNull();
        assertThat(parkingRateCreateRequest.getStartTime()).isEmpty();
    }

    @Test
    public void test_getStartTime_Success() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest("thurs,sat", "0200-0600", "America/Chicago", 5400);
        assertThat(parkingRateCreateRequest.getStartTime()).isEqualTo("0200");
    }

    @Test
    public void test_getEndTime_Empty() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest();
        assertThat(parkingRateCreateRequest.getEndTime()).isNotNull();
        assertThat(parkingRateCreateRequest.getEndTime()).isEmpty();
    }

    @Test
    public void test_getEndTime_Success() {
        ParkingRateCreateRequest parkingRateCreateRequest = new ParkingRateCreateRequest("thurs,sat", "0200-0600", "America/Chicago", 5400);
        assertThat(parkingRateCreateRequest.getEndTime()).isEqualTo("0600");
    }
}
