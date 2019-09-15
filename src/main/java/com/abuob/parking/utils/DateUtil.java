package com.abuob.parking.utils;

import com.abuob.parking.enums.DayOfWeekEnum;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class DateUtil {

    private DateUtil() {
    }

    public static ZonedDateTime convertToUTC(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    }

    public static ZonedDateTime withStartOfDay(ZonedDateTime zonedDateTime) {
        return zonedDateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static Map<DayOfWeekEnum, ZonedDateTime> initializeStartOfDayMapForTimezone(String timezone) {
        Map<DayOfWeekEnum, ZonedDateTime> resultMap = new HashMap<>();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezone));
        ZonedDateTime startOfDay = withStartOfDay(now);

        ZonedDateTime currentDayStart;
        DayOfWeek dayOfWeek;

        for (int i = 0; i <= 6; i++) {
            currentDayStart = startOfDay.plusDays(i);
            dayOfWeek = currentDayStart.getDayOfWeek();
            resultMap.put(DayOfWeekEnum.valueOf(dayOfWeek.name()), currentDayStart);
        }
        return resultMap;
    }

    public static Long zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit) {
        return unit.between(d1, d2);
    }
}
