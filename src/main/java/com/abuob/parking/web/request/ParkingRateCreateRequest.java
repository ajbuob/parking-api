package com.abuob.parking.web.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.StringTokenizer;

public class ParkingRateCreateRequest {

    private static final String DAY_SEPARATOR = ",";
    private static final String HOUR_SEPARATOR = "-";

    private String days;
    private String times;
    private String tz;
    private Integer price;

    public ParkingRateCreateRequest() {
    }

    public ParkingRateCreateRequest(String days, String times, String tz, Integer price) {
        this.days = days;
        this.times = times;
        this.tz = tz;
        this.price = price;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @JsonIgnore
    public List<String> getDaysAsList() {
        List<String> daysList = Lists.newArrayList();

        if (StringUtils.isNotEmpty(getDays())) {
            if (getDays().contains(",")) {
                StringTokenizer stringTokenizer = new StringTokenizer(getDays(), DAY_SEPARATOR);
                while (stringTokenizer.hasMoreTokens()) {
                    daysList.add(stringTokenizer.nextToken());
                }
            } else {
                daysList.add(getDays());
            }
        }
        return daysList;
    }

    @JsonIgnore
    public String getStartTime() {
        if (!StringUtils.isEmpty(getTimes())) {
            return StringUtils.substringBefore(getTimes(), HOUR_SEPARATOR);
        }
        return "";
    }

    @JsonIgnore
    public String getEndTime() {
        if (!StringUtils.isEmpty(getTimes())) {
            return StringUtils.substringAfter(getTimes(), HOUR_SEPARATOR);
        }
        return "";
    }

    @Override
    public String toString() {
        return "ParkingRateCreateRequest{" +
                "days='" + days + '\'' +
                ", daysList='" + getDaysAsList() + '\'' +
                ", times='" + times + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", tz='" + tz + '\'' +
                ", price=" + price +
                '}';
    }
}
