package com.abuob.parking.repository;

import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.enums.DayOfWeekEnum;
import com.abuob.parking.utils.ParkingRateUtil;
import com.abuob.parking.web.request.ParkingRateCreateRequest;
import com.abuob.parking.web.request.ParkingRateListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class InMemoryParkingRateRepository implements ParkingRateRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryParkingRateRepository.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<DayOfWeekEnum, Map<Integer, Integer>> parkingRateMap;

    @PostConstruct
    public void initializeParkingRates() {
        parkingRateMap = new HashMap<>();
        Map<Integer, Integer> hourOfDayToRateMap;
        for (DayOfWeekEnum dayOfWeekEnum : DayOfWeekEnum.values()) {
            //Initialize all hourly rates to 0
            hourOfDayToRateMap = new HashMap<>();
            for (int i = 0; i <= 23; i++) {
                hourOfDayToRateMap.put(i, 0);
            }
            //Initialize all daily rates for all hours to 0
            parkingRateMap.put(dayOfWeekEnum, hourOfDayToRateMap);
        }

        logger.debug("Loading initial rates from JSON file");
        Resource resource = new ClassPathResource("json/init_rates.json");

        try {
            InputStream resourceInputStream = resource.getInputStream();
            ParkingRateListWrapper parkingRateWrapper = objectMapper.readValue(resourceInputStream, ParkingRateListWrapper.class);
            List<ParkingRateCreateRequest> initialParkingDaysTimeRatesListRequest = parkingRateWrapper.getRates();

            List<HourlyPriceDTO> hourlyPriceDTOList = ParkingRateUtil.convertToRateDTO(initialParkingDaysTimeRatesListRequest);
            Boolean isInitialized = updateAllRates(hourlyPriceDTOList);
            logger.debug("isInitialized: {}", isInitialized);

        } catch (IOException e) {
            logger.error("An error occurred while trying obtain input stream for init_rates.json:" + e.getMessage());
        }
    }

    @Override
    public HourlyPriceDTO getDailyRateByHour(DayOfWeekEnum dayOfWeekEnum, Integer hourOfDay) {
        HourlyPriceDTO hourlyPriceDTO = new HourlyPriceDTO(dayOfWeekEnum, hourOfDay, 0);
        Integer currentPrice;
        //Add the current rate if the input is valid
        if (!Objects.isNull(dayOfWeekEnum) && !Objects.isNull(hourOfDay) && hourOfDay > 0 && hourOfDay <= 23) {
            currentPrice = parkingRateMap.get(dayOfWeekEnum).get(hourOfDay);
            logger.debug("getDailyRateByHour - dayOfWeek: {} hourOfDay: {} price: {}", dayOfWeekEnum, hourOfDay, currentPrice);
            hourlyPriceDTO.setPrice(currentPrice);
        }
        return hourlyPriceDTO;
    }

    public boolean updateAllRates(List<HourlyPriceDTO> hourlyPriceDTOList) {
        DayOfWeekEnum dayOfWeek;
        Integer hourOfDay;
        Integer price;
        Map<Integer, Integer> dailyRateMap;

        for (HourlyPriceDTO hourlyPriceDTO : hourlyPriceDTOList) {
            dayOfWeek = hourlyPriceDTO.getDayOfWeek();
            hourOfDay = hourlyPriceDTO.getHourOfDay();
            price = hourlyPriceDTO.getPrice();

            logger.debug("updateAllRates - dayOfWeek: {} hourOfDay: {} price: {}", dayOfWeek, hourOfDay, price);
            dailyRateMap = parkingRateMap.get(dayOfWeek);
            dailyRateMap.put(hourOfDay, price);
        }
        return Boolean.TRUE;
    }
}
