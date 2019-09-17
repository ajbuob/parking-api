package com.abuob.parking.utils;


import com.abuob.parking.dto.RateDTO;
import com.abuob.parking.enums.DayOfWeekEnum;
import com.abuob.parking.web.request.ParkingRateCreateRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParkingRateUtil {

    private ParkingRateUtil() {
    }

    public static List<RateDTO> convertToRateDTO(List<ParkingRateCreateRequest> parkingRateCreateRequestList) {
        //Assumes the input parking rate list can have multiple timezones,
        //each day of week and start of day map will be stored as value with timezone as key
        Map<String, Map<DayOfWeekEnum, ZonedDateTime>> tzToDayOfWeekMap = new HashMap<>();

        //The day of week to start of day mapping for a specific timezone
        Map<DayOfWeekEnum, ZonedDateTime> startOfEachDayOfWeekMap;
        String tz;
        Integer price;
        List<String> daysList;
        String startTime, endTime;
        Integer startHour, endHour;

        List<RateDTO> rateDTOList = Lists.newArrayList();

        //Process each parking rate by converting to UTC
        for (ParkingRateCreateRequest parkingRateCreateRequest : parkingRateCreateRequestList) {
            tz = parkingRateCreateRequest.getTz();
            price = parkingRateCreateRequest.getPrice();

            daysList = parkingRateCreateRequest.getDaysAsList();

            startTime = parkingRateCreateRequest.getStartTime();
            endTime = parkingRateCreateRequest.getEndTime();

            startHour = Integer.valueOf(startTime) / 100;
            endHour = Integer.valueOf(endTime) / 100;

            if (MapUtils.isEmpty(tzToDayOfWeekMap.get(tz))) {
                startOfEachDayOfWeekMap = DateUtil.initializeStartOfDayMapForTimezone(tz);
                tzToDayOfWeekMap.put(tz, startOfEachDayOfWeekMap);
            } else {
                startOfEachDayOfWeekMap = tzToDayOfWeekMap.get(tz);
            }

            //Process each day in the parking rate (start and end time are constant)
            //to construct a RateDTO in UTC
            DayOfWeekEnum dayOfWeekEnum;
            ZonedDateTime currentStartDay;
            for (String day : daysList) {
                dayOfWeekEnum = DayOfWeekEnum.getDayOfWeekByAbbreviation(day);
                //Start of day
                currentStartDay = startOfEachDayOfWeekMap.get(dayOfWeekEnum);

                ZonedDateTime currentDay, currentDayUTC;
                RateDTO rateDTO;
                for (int i = startHour; i <= endHour - 1; i++) {
                    rateDTO = new RateDTO();
                    rateDTO.setPrice(price);

                    currentDay = currentStartDay.plusHours(i);
                    currentDayUTC = DateUtil.convertToUTC(currentDay);

                    rateDTO.setHourOfDay(currentDayUTC.getHour());
                    rateDTO.setDayOfWeek(DayOfWeekEnum.valueOf(currentDayUTC.getDayOfWeek().name()));
                    rateDTOList.add(rateDTO);
                }
            }
        }
        return rateDTOList;
    }
}
