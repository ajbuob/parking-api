package com.abuob.parking.service;

import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.enums.DayOfWeekEnum;
import com.abuob.parking.repository.InMemoryParkingRateRepository;
import com.abuob.parking.repository.ParkingRateRepository;
import com.abuob.parking.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class ParkingRateServiceImpl implements ParkingRateService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingRateServiceImpl.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private ParkingRateRepository parkingRateRepository;

    @Autowired
    public ParkingRateServiceImpl(ParkingRateRepository parkingRateRepository) {
        this.parkingRateRepository = parkingRateRepository;
    }

    public Integer findCurrentRateForDateTimeInterval(ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws ParkingServiceException {
        ParkingApiErrorEnum parkingApiErrorEnum;

        //No start date
        if (Objects.isNull(startDateTime)) {
            parkingApiErrorEnum = ParkingApiErrorEnum.START_DATE_MISSING;
            throw new ParkingServiceException(parkingApiErrorEnum.getErrorMessage(), parkingApiErrorEnum.getErrorCode());
        }

        //No end date
        if (Objects.isNull(endDateTime)) {
            parkingApiErrorEnum = ParkingApiErrorEnum.END_DATE_MISSING;
            throw new ParkingServiceException(parkingApiErrorEnum.getErrorMessage(), parkingApiErrorEnum.getErrorCode());
        }

        //Start Date and End Date are the same
        if (startDateTime.isEqual(endDateTime)) {
            parkingApiErrorEnum = ParkingApiErrorEnum.START_DATE_END_DATE_SAME;
            throw new ParkingServiceException(parkingApiErrorEnum.getErrorMessage(), parkingApiErrorEnum.getErrorCode());
        }

        //Different timezones for start and end dates
        if (startDateTime.getZone().getId() != endDateTime.getZone().getId()) {
            parkingApiErrorEnum = ParkingApiErrorEnum.DATE_DIFFERENT_TZ;
            throw new ParkingServiceException(parkingApiErrorEnum.getErrorMessage(), parkingApiErrorEnum.getErrorCode());
        }

        //User input spans multiple dates (above check guarantees same timezone so no conversion is needed)
        if ((startDateTime.getYear() != endDateTime.getYear()) ||
                (startDateTime.getDayOfYear() != endDateTime.getDayOfYear())) {
            parkingApiErrorEnum = ParkingApiErrorEnum.DATE_RANGE_SPANS_MULTIPLE_DAYS;
            throw new ParkingServiceException(parkingApiErrorEnum.getErrorMessage(), parkingApiErrorEnum.getErrorCode());
        }

        logger.debug("findCurrentRateForDateTimeInterval  - startDate: {} endDate: {} ",
                formatter.format(startDateTime), formatter.format(endDateTime));

        ZonedDateTime startDateTimeUTC = DateUtil.convertToUTC(startDateTime);
        ZonedDateTime endDateTimeUTC = DateUtil.convertToUTC(endDateTime);

        Integer hoursBetween = DateUtil.zonedDateTimeDifference(startDateTimeUTC, endDateTimeUTC, ChronoUnit.HOURS).intValue();

        logger.debug("findCurrentRateForDateTimeInterval  - startDateUTC: {} endDateUTC: {} hoursBetween: {} ",
                formatter.format(startDateTimeUTC), formatter.format(endDateTimeUTC), hoursBetween);

        Integer candidatePrice = null;
        Integer currentPrice;
        ZonedDateTime currentDateTimeUTC;
        DayOfWeekEnum currentDateWeek;
        Integer currentHourOfDay;

        //Check if date range spans multiple rates
        for (int i = 0; i <= hoursBetween - 1; i++) {
            if (i == 0) {
                //Initialize the candidate price based on the start date
                currentDateWeek = DayOfWeekEnum.valueOf(startDateTimeUTC.getDayOfWeek().name());
                currentHourOfDay = startDateTimeUTC.getHour();
                candidatePrice = parkingRateRepository.getDailyRateByHour(currentDateWeek, currentHourOfDay).getPrice();
                logger.debug("findCurrentRateForDateTimeInterval  - currentDateWeek: {} currentHourOfDay: {} candidatePrice: {} ",
                        currentDateWeek, currentHourOfDay, candidatePrice);
            } else {
                //Find the current price and compare it to the candidate price
                currentDateTimeUTC = startDateTimeUTC.plusHours(i);
                currentDateWeek = DayOfWeekEnum.valueOf(currentDateTimeUTC.getDayOfWeek().name());
                currentHourOfDay = currentDateTimeUTC.getHour();
                currentPrice = parkingRateRepository.getDailyRateByHour(currentDateWeek, currentHourOfDay).getPrice();
                logger.debug("findCurrentRateForDateTimeInterval  - currentDateWeek: {} currentHourOfDay: {} currentPrice: {} ",
                        currentDateWeek, currentHourOfDay, currentPrice);

                if (candidatePrice != currentPrice) {
                    parkingApiErrorEnum = ParkingApiErrorEnum.DATE_RANGE_SPANS_MULTIPLE_RATE;
                    throw new ParkingServiceException(parkingApiErrorEnum.getErrorMessage(), parkingApiErrorEnum.getErrorCode());
                }
            }
        }
        return candidatePrice;
    }

    @Override
    public boolean addHourlyPrices(List<HourlyPriceDTO> hourlyPriceDTOList) {
        Boolean isValid = validateHourlyPriceList(hourlyPriceDTOList);
        return isValid ? parkingRateRepository.updateAllRates(hourlyPriceDTOList) : Boolean.FALSE;
    }

    private boolean validateHourlyPriceList(List<HourlyPriceDTO> hourlyPriceDTOList) {
        Integer price;
        Integer hourOfDay;

        for (HourlyPriceDTO hourlyPriceDTO : hourlyPriceDTOList) {
            price = hourlyPriceDTO.getPrice();
            hourOfDay = hourlyPriceDTO.getHourOfDay();

            //Validate all the input
            if (Objects.isNull(hourlyPriceDTO) || Objects.isNull(hourlyPriceDTO.getDayOfWeek())) {
                return Boolean.FALSE;
            }

            if (Objects.isNull(hourOfDay) || hourOfDay < 0 || hourOfDay >= 23) {
                return Boolean.FALSE;
            }

            if (Objects.isNull(price) || price < 0) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }


}
