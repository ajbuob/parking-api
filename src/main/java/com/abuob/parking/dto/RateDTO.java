package com.abuob.parking.dto;

import com.abuob.parking.enums.DayOfWeekEnum;

import java.util.Objects;

public class RateDTO {

    private DayOfWeekEnum dayOfWeek;

    private Integer hourOfDay;

    private Integer price;

    public RateDTO() {
    }

    public DayOfWeekEnum getDayOfWeek() {
        return dayOfWeek;
    }

    public RateDTO(DayOfWeekEnum dayOfWeek, Integer hourOfDay, Integer price) {
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;
        this.price = price;
    }

    public void setDayOfWeek(DayOfWeekEnum dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(Integer hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateDTO rateDTO = (RateDTO) o;
        return dayOfWeek == rateDTO.dayOfWeek &&
                hourOfDay.equals(rateDTO.hourOfDay) &&
                price.equals(rateDTO.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, hourOfDay, price);
    }
}
