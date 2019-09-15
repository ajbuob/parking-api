package com.abuob.parking.repository;

import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.enums.DayOfWeekEnum;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryParkingDaysTimeRateRepositoryTest {

    private InMemoryParkingRateRepository inMemoryParkingRateRepository;

    @Before
    public void init() {
        inMemoryParkingRateRepository = new InMemoryParkingRateRepository();
        inMemoryParkingRateRepository.initializeParkingRates();
    }

    @Test
    public void test_getDailyRateByHour_Nulls() {
        HourlyPriceDTO result = inMemoryParkingRateRepository.getDailyRateByHour(null, null);
        assertThat(result.getPrice()).isEqualTo(0);
    }

    @Test
    public void test_getDailyRateByHour_NullHour() {
        HourlyPriceDTO result = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.SATURDAY, null);
        assertThat(result.getPrice()).isEqualTo(0);
    }

    @Test
    public void test_getDailyRateByHour_NegativeHour() {
        HourlyPriceDTO result = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.SATURDAY, -4);
        assertThat(result.getPrice()).isEqualTo(0);
    }

    @Test
    public void test_getDailyRateByHour_Success() {
        HourlyPriceDTO result = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.SATURDAY, 23);
        assertThat(result.getPrice()).isEqualTo(2000);
    }

    @Test
    public void test_getDailyRateByHour_updateAllRates() {
        HourlyPriceDTO hourlyPriceDTO = new HourlyPriceDTO(DayOfWeekEnum.TUESDAY, 12, 5400);
        Boolean result = inMemoryParkingRateRepository.updateAllRates(Lists.newArrayList(hourlyPriceDTO));
        assertThat(result).isTrue();

        HourlyPriceDTO hourlyPriceDTOResult = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.TUESDAY, 12);
        assertThat(hourlyPriceDTOResult.getPrice()).isEqualTo(5400);
    }
}
