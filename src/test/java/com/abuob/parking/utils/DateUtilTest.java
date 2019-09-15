package com.abuob.parking.utils;

import com.abuob.parking.enums.DayOfWeekEnum;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilTest {

    @Test
    public void test_convertToUTC() {
        ZonedDateTime chicagoTime =
                ZonedDateTime.of(2019, 9, 12, 22, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime utcTime =
                ZonedDateTime.of(2019, 9, 13, 3, 0, 0, 0, ZoneId.of("UTC"));
        assertThat(DateUtil.convertToUTC(chicagoTime)).isEqualTo(utcTime);
    }

    @Test
    public void test_withStartOfDay() {
        ZonedDateTime zonedDateTime =
                ZonedDateTime.of(2019, 8, 4, 3, 3, 2, 1, ZoneId.of("America/Chicago"));
        ZonedDateTime zonedDateTimeStart =
                ZonedDateTime.of(2019, 8, 4, 0, 0, 0, 0, ZoneId.of("America/Chicago"));
        assertThat(DateUtil.withStartOfDay(zonedDateTime)).isEqualTo(zonedDateTimeStart);

        ZonedDateTime zonedDateTimeUTC =
                ZonedDateTime.of(2019, 10, 10, 1, 2, 2, 3, ZoneId.of("UTC"));
        ZonedDateTime zonedDateTimeUTCStart =
                ZonedDateTime.of(2019, 10, 10, 0, 0, 0, 0, ZoneId.of("UTC"));
        assertThat(DateUtil.withStartOfDay(zonedDateTimeUTC)).isEqualTo(zonedDateTimeUTCStart);
    }

    @Test
    public void test_initializeStartOfDayMapForTimezone() {
        Map<DayOfWeekEnum, ZonedDateTime> resultMap = DateUtil.initializeStartOfDayMapForTimezone("Australia/Sydney");

        assertThat(resultMap.isEmpty()).isFalse();
        assertThat(resultMap.size()).isEqualTo(7);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Australia/Sydney"));
        ZonedDateTime nowStartOfDay = DateUtil.withStartOfDay(now);

        System.out.println(now);

        ZonedDateTime currentStartOfDay;
        DayOfWeekEnum currentDayOfWeekEnum;

        for (int i = 0; i <= 6; i++) {
            currentStartOfDay = nowStartOfDay.plusDays(i);
            currentDayOfWeekEnum = DayOfWeekEnum.valueOf(currentStartOfDay.getDayOfWeek().name());
            assertThat(resultMap.get(currentDayOfWeekEnum)).isEqualTo(currentStartOfDay);
        }
    }

    @Test
    public void test_zonedDateTimeDifference() {
        ZonedDateTime zonedDateTime1 =
                ZonedDateTime.of(2019, 8, 4, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime zonedDateTime2 =
                ZonedDateTime.of(2019, 8, 6, 10, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime zonedDateTime3 =
                ZonedDateTime.of(2019, 8, 6, 14, 0, 0, 0, ZoneId.of("America/Chicago"));

        assertThat(DateUtil.zonedDateTimeDifference(zonedDateTime1, zonedDateTime2, ChronoUnit.MONTHS)).isEqualTo(0);
        assertThat(DateUtil.zonedDateTimeDifference(zonedDateTime1, zonedDateTime2, ChronoUnit.DAYS)).isEqualTo(2);
        assertThat(DateUtil.zonedDateTimeDifference(zonedDateTime1, zonedDateTime2, ChronoUnit.HOURS)).isEqualTo(48);

        assertThat(DateUtil.zonedDateTimeDifference(zonedDateTime2, zonedDateTime3, ChronoUnit.MONTHS)).isEqualTo(0);
        assertThat(DateUtil.zonedDateTimeDifference(zonedDateTime2, zonedDateTime3, ChronoUnit.DAYS)).isEqualTo(0);
        assertThat(DateUtil.zonedDateTimeDifference(zonedDateTime2, zonedDateTime3, ChronoUnit.HOURS)).isEqualTo(4);
    }
}
