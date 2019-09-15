package com.abuob.parking.repository;

import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.enums.DayOfWeekEnum;

import java.util.List;

public interface ParkingRateRepository {

    /**
     * te
     * Finds the rate for the specified day of the week and hour of day
     *
     * @param dayOfWeekEnum the day of the week
     * @param hourOfDay     the hour of the day
     * @return the rate information
     **/
    HourlyPriceDTO getDailyRateByHour(DayOfWeekEnum dayOfWeekEnum, Integer hourOfDay);

    /**
     * Updates the rates for the specified days of the week and hours of day
     *
     * @param hourlyPriceDTOList the days, hours and prices to be updated
     * @return the success of the update
     **/
    boolean updateAllRates(List<HourlyPriceDTO> hourlyPriceDTOList);
}

