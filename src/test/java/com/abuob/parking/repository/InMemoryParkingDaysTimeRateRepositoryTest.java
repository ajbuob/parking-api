package com.abuob.parking.repository;

import com.abuob.parking.dto.RateDTO;
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
        RateDTO result = inMemoryParkingRateRepository.getDailyRateByHour(null, null);
        assertThat(result.getPrice()).isEqualTo(0);
    }

    @Test
    public void test_getDailyRateByHour_NullHour() {
        RateDTO result = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.SATURDAY, null);
        assertThat(result.getPrice()).isEqualTo(0);
    }

    @Test
    public void test_getDailyRateByHour_NegativeHour() {
        RateDTO result = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.SATURDAY, -4);
        assertThat(result.getPrice()).isEqualTo(0);
    }

    @Test
    public void test_getDailyRateByHour_Success() {
        RateDTO result = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.SATURDAY, 23);
        assertThat(result.getPrice()).isEqualTo(2000);
    }

    @Test
    public void test_getDailyRateByHour_updateAllRates() {
        RateDTO rateDTO = new RateDTO(DayOfWeekEnum.TUESDAY, 12, 5400);
        Boolean result = inMemoryParkingRateRepository.updateAllRates(Lists.newArrayList(rateDTO));
        assertThat(result).isTrue();

        RateDTO rateDTOResult = inMemoryParkingRateRepository.getDailyRateByHour(DayOfWeekEnum.TUESDAY, 12);
        assertThat(rateDTOResult.getPrice()).isEqualTo(5400);
    }
}
