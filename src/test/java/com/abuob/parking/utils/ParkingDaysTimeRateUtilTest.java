package com.abuob.parking.utils;

import com.abuob.parking.dto.RateDTO;
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

        List<RateDTO> rateDTOList = ParkingRateUtil.convertToRateDTO(parkingDaysTimeRateList);
        assertThat(rateDTOList).isNotNull();
        assertThat(rateDTOList).isNotEmpty();
        assertThat(rateDTOList).hasSize(7);

        for (RateDTO rateDTO : rateDTOList) {
            assertThat(rateDTO.getDayOfWeek()).isEqualTo(DayOfWeekEnum.WEDNESDAY);
        }
    }

    @Test
    public void test_convertToRateDTO_DifferentDaysInUTC() {
        List<ParkingRateCreateRequest> parkingDaysTimeRateList = Lists.newArrayList();

        ParkingRateCreateRequest parkingDaysTimeRate = new ParkingRateCreateRequest("wed", "1600-2200", "America/Chicago", 3250);
        parkingDaysTimeRateList.add(parkingDaysTimeRate);

        List<RateDTO> rateDTOList = ParkingRateUtil.convertToRateDTO(parkingDaysTimeRateList);
        assertThat(rateDTOList).isNotNull();
        assertThat(rateDTOList).isNotEmpty();
        assertThat(rateDTOList).hasSize(6);

        for (RateDTO rateDTO : rateDTOList) {
            assertThat(rateDTO.getDayOfWeek().equals(DayOfWeekEnum.WEDNESDAY) ||
                    rateDTO.getDayOfWeek().equals(DayOfWeekEnum.THURSDAY)).isTrue();
        }
    }
}
