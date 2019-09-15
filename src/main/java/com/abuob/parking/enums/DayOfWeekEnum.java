package com.abuob.parking.enums;

public enum DayOfWeekEnum {

    SUNDAY("sun"),
    MONDAY("mon"),
    TUESDAY("tues"),
    WEDNESDAY("wed"),
    THURSDAY("thurs"),
    FRIDAY("fri"),
    SATURDAY("sat");

    private String abbreviation;

    DayOfWeekEnum(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static DayOfWeekEnum getDayOfWeekByAbbreviation(String abbreviation) {
        for (DayOfWeekEnum dayOfWeekEnum : values()) {
            if (dayOfWeekEnum.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return dayOfWeekEnum;
            }
        }
        return null;
    }
}

