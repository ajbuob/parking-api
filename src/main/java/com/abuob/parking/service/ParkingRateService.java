package com.abuob.parking.service;

import com.abuob.parking.dto.HourlyPriceDTO;

import java.time.ZonedDateTime;
import java.util.List;

public interface ParkingRateService {

    Integer findCurrentRateForDateTimeInterval(ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws ParkingServiceException;

    boolean addHourlyPrices(List<HourlyPriceDTO> hourlyPriceDTOList);
}
