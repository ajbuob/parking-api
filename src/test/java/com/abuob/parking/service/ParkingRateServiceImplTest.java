package com.abuob.parking.service;

import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.enums.DayOfWeekEnum;
import com.abuob.parking.repository.ParkingRateRepository;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParkingRateServiceImplTest {

    private ParkingRateService parkingRateService;
    private ParkingRateRepository parkingRateRepository;

    @Before
    public void init() {
        parkingRateRepository = mock(ParkingRateRepository.class);
        parkingRateService = new ParkingRateServiceImpl(parkingRateRepository);
    }

    @Test
    public void test_findCurrentRateForDateTimeInterval_MissingStartDate() {
        assertThatThrownBy(() -> {
            parkingRateService.findCurrentRateForDateTimeInterval(null, null);
        }).isInstanceOf(ParkingServiceException.class)
                .hasMessage(ParkingApiErrorEnum.START_DATE_MISSING.getErrorMessage());
    }

    @Test
    public void test_findCurrentRateForDateTimeInterval_MissingEndDate() {
        ZonedDateTime startDateTime =
                ZonedDateTime.of(2019, 8, 4, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        assertThatThrownBy(() -> {
            parkingRateService.findCurrentRateForDateTimeInterval(startDateTime, null);
        }).isInstanceOf(ParkingServiceException.class)
                .hasMessage(ParkingApiErrorEnum.END_DATE_MISSING.getErrorMessage());
    }

    @Test
    public void test_findCurrentRateForDateTimeInterval_DifferentTimeZone() {
        ZonedDateTime startDateTime =
                ZonedDateTime.of(2019, 8, 4, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endDateTime =
                ZonedDateTime.of(2019, 8, 4, 14, 0, 0, 0, ZoneId.of("America/New_York"));
        assertThatThrownBy(() -> {
            parkingRateService.findCurrentRateForDateTimeInterval(startDateTime, endDateTime);
        }).isInstanceOf(ParkingServiceException.class)
                .hasMessage(ParkingApiErrorEnum.DATE_DIFFERENT_TZ.getErrorMessage());
    }

    @Test
    public void test_findCurrentRateForDateTimeInterval_DateRangeMultipleDays() {
        ZonedDateTime startDateTime =
                ZonedDateTime.of(2019, 8, 4, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endDateTime =
                ZonedDateTime.of(2019, 8, 5, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        assertThatThrownBy(() -> {
            parkingRateService.findCurrentRateForDateTimeInterval(startDateTime, endDateTime);
        }).isInstanceOf(ParkingServiceException.class)
                .hasMessage(ParkingApiErrorEnum.DATE_RANGE_SPANS_MULTIPLE_DAYS.getErrorMessage());
    }

    @Test
    public void test_findCurrentRateForDateTimeInterval_DifferentParkingPrices() {
        ZonedDateTime startDateTime =
                ZonedDateTime.of(2019, 8, 4, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endDateTime =
                ZonedDateTime.of(2019, 8, 4, 15, 0, 0, 0, ZoneId.of("America/Chicago"));

        HourlyPriceDTO hourlyPriceDTO1 = new HourlyPriceDTO();
        hourlyPriceDTO1.setDayOfWeek(DayOfWeekEnum.SUNDAY);
        hourlyPriceDTO1.setHourOfDay(10);
        hourlyPriceDTO1.setPrice(100);

        HourlyPriceDTO hourlyPriceDTO2 = new HourlyPriceDTO();
        hourlyPriceDTO2.setDayOfWeek(DayOfWeekEnum.SUNDAY);
        hourlyPriceDTO2.setHourOfDay(11);
        hourlyPriceDTO2.setPrice(200);

        when(parkingRateRepository.getDailyRateByHour(any(DayOfWeekEnum.class), anyInt())).thenReturn(hourlyPriceDTO1, hourlyPriceDTO2);

        assertThatThrownBy(() -> {
            parkingRateService.findCurrentRateForDateTimeInterval(startDateTime, endDateTime);
        }).isInstanceOf(ParkingServiceException.class)
                .hasMessage(ParkingApiErrorEnum.DATE_RANGE_SPANS_MULTIPLE_RATE.getErrorMessage());
    }

    @Test
    public void test_findCurrentRateForDateTimeInterval_Success() throws Exception {
        ZonedDateTime startDateTime =
                ZonedDateTime.of(2019, 8, 4, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endDateTime =
                ZonedDateTime.of(2019, 8, 4, 15, 0, 0, 0, ZoneId.of("America/Chicago"));

        HourlyPriceDTO hourlyPriceDTO1 = new HourlyPriceDTO();
        hourlyPriceDTO1.setDayOfWeek(DayOfWeekEnum.SUNDAY);
        hourlyPriceDTO1.setHourOfDay(10);
        hourlyPriceDTO1.setPrice(100);
        //Return the same price for every call to the mock
        when(parkingRateRepository.getDailyRateByHour(any(DayOfWeekEnum.class), anyInt())).thenReturn(hourlyPriceDTO1);
        assertThat(parkingRateService.findCurrentRateForDateTimeInterval(startDateTime, endDateTime)).isEqualTo(100);
    }

    @Test
    public void test_updateRate_Success() {
        HourlyPriceDTO hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.FRIDAY, 10, 3700);
        when(parkingRateRepository.updateAllRates(anyList())).thenReturn(Boolean.TRUE);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isTrue();
    }

    @Test
    public void test_addHourlyPrices_validate_Nulls() {
        HourlyPriceDTO hourlyPriceDTO = new HourlyPriceDTO(null, null, null);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.SATURDAY, null, null);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.SATURDAY, 4, null);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(null, 4, 5600);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.MONDAY, null, 5600);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(null, 20, null);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();
    }

    @Test
    public void test_updateRate_HoursBounds() {
        HourlyPriceDTO hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.FRIDAY, -1, 3200);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.FRIDAY, 24, 3200);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.FRIDAY, 432, 3200);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();
    }

    @Test
    public void test_updateRate_RateBounds() {
        HourlyPriceDTO hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.FRIDAY, 10, -1);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();

        hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.FRIDAY, 10, -14);
        assertThat(parkingRateService.addHourlyPrices(Lists.newArrayList(hourlyPriceDTO))).isFalse();
    }
}
