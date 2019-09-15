package com.abuob.parking.enums;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DayOfWeekEnumTest {

    @Test
    public void test_getDayOfWeekByAbbreviation() {
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("sun")).isEqualTo(DayOfWeekEnum.SUNDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("mon")).isEqualTo(DayOfWeekEnum.MONDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("tues")).isEqualTo(DayOfWeekEnum.TUESDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("wed")).isEqualTo(DayOfWeekEnum.WEDNESDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("thurs")).isEqualTo(DayOfWeekEnum.THURSDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("fri")).isEqualTo(DayOfWeekEnum.FRIDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("sat")).isEqualTo(DayOfWeekEnum.SATURDAY);

        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("ThURs")).isEqualTo(DayOfWeekEnum.THURSDAY);
        assertThat(DayOfWeekEnum.getDayOfWeekByAbbreviation("otherDay")).isNull();

    }
}
