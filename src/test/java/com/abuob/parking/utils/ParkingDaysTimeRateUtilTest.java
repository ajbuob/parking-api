package com.abuob.parking.utils;

import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.enums.DayOfWeekEnum;
import com.abuob.parking.web.request.ParkingRateCreateRequest;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingDaysTimeRateUtilTest {

    @Test
    public void test_convertToRateDTO_SameDayAsUTC() {
        List<ParkingRateCreateRequest> parkingDaysTimeRateList = Lists.newArrayList();

        ParkingRateCreateRequest parkingDaysTimeRate = new ParkingRateCreateRequest("wed", "1000-1700", "America/Chicago", 3250);
        parkingDaysTimeRateList.add(parkingDaysTimeRate);

        List<HourlyPriceDTO> hourlyPriceDTOList = ParkingRateUtil.convertToRateDTO(parkingDaysTimeRateList);
        assertThat(hourlyPriceDTOList).isNotNull();
        assertThat(hourlyPriceDTOList).isNotEmpty();
        assertThat(hourlyPriceDTOList).hasSize(7);

        for (HourlyPriceDTO hourlyPriceDTO : hourlyPriceDTOList) {
            assertThat(hourlyPriceDTO.getDayOfWeek()).isEqualTo(DayOfWeekEnum.WEDNESDAY);
        }
    }

    @Test
    public void test_convertToRateDTO_DifferentDaysInUTC() {
        List<ParkingRateCreateRequest> parkingDaysTimeRateList = Lists.newArrayList();

        ParkingRateCreateRequest parkingDaysTimeRate = new ParkingRateCreateRequest("wed", "1600-2200", "America/Chicago", 3250);
        parkingDaysTimeRateList.add(parkingDaysTimeRate);

        List<HourlyPriceDTO> hourlyPriceDTOList = ParkingRateUtil.convertToRateDTO(parkingDaysTimeRateList);
        assertThat(hourlyPriceDTOList).isNotNull();
        assertThat(hourlyPriceDTOList).isNotEmpty();
        assertThat(hourlyPriceDTOList).hasSize(6);

        for (HourlyPriceDTO hourlyPriceDTO : hourlyPriceDTOList) {
            assertThat(hourlyPriceDTO.getDayOfWeek().equals(DayOfWeekEnum.WEDNESDAY) ||
                    hourlyPriceDTO.getDayOfWeek().equals(DayOfWeekEnum.THURSDAY)).isTrue();
        }
    }
}
