package com.abuob.parking.controller;

import com.abuob.parking.service.ParkingApiErrorEnum;
import com.abuob.parking.service.ParkingRateService;
import com.abuob.parking.service.ParkingServiceException;
import com.abuob.parking.web.request.ParkingRateCreateRequest;
import com.abuob.parking.web.request.ParkingRateListWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class ParkingControllerTest {

    @MockBean
    private ParkingRateService parkingRateService;

    @Autowired
    private MockMvc mockMvc;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Test
    public void test_findCurrentRate_Success() throws Exception {

        ZonedDateTime startTime =
                ZonedDateTime.of(2019, 9, 12, 12, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endTime =
                ZonedDateTime.of(2019, 9, 12, 14, 0, 0, 0, ZoneId.of("America/Chicago"));

        String startTimeString = formatter.format(startTime);
        String endTimeString = formatter.format(endTime);

        when(parkingRateService.findCurrentRateForDateTimeInterval(ArgumentMatchers.any(ZonedDateTime.class), ArgumentMatchers.any(ZonedDateTime.class))).thenReturn(4320);

        mockMvc.perform(get("/api/parking/rate/start/" + startTimeString + "/end/" + endTimeString).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.statusCode").value(0))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.price").value(4320))
                .andExpect(jsonPath("$.startTime").value("2019-09-12T12:00:00-05:00"))
                .andExpect(jsonPath("$.endTime").value("2019-09-12T14:00:00-05:00"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_findCurrentRate_RateOverlap() throws Exception {

        ZonedDateTime startTime =
                ZonedDateTime.of(2019, 9, 12, 12, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endTime =
                ZonedDateTime.of(2019, 9, 12, 20, 0, 0, 0, ZoneId.of("America/Chicago"));

        String startTimeString = formatter.format(startTime);
        String endTimeString = formatter.format(endTime);

        ParkingApiErrorEnum parkingApiErrorEnum = ParkingApiErrorEnum.DATE_RANGE_SPANS_MULTIPLE_RATE;
        Integer errorCode = parkingApiErrorEnum.getErrorCode();
        String message = parkingApiErrorEnum.getErrorMessage();

        when(parkingRateService.findCurrentRateForDateTimeInterval(ArgumentMatchers.any(ZonedDateTime.class), ArgumentMatchers.any(ZonedDateTime.class))).
                thenThrow(new ParkingServiceException(message, errorCode));

        mockMvc.perform(get("/api/parking/rate/start/" + startTimeString + "/end/" + endTimeString).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.statusCode").value(errorCode))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.price").value(ParkingController.UNAVAILABLE_PRICE))
                .andExpect(jsonPath("$.startTime").value("2019-09-12T12:00:00-05:00"))
                .andExpect(jsonPath("$.endTime").value("2019-09-12T20:00:00-05:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_findCurrentRate_BadRequest() throws Exception {

        ZonedDateTime startTime =
                ZonedDateTime.of(2019, 9, 12, 12, 0, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime endTime =
                ZonedDateTime.of(2019, 9, 12, 12, 0, 0, 0, ZoneId.of("America/Chicago"));

        String startTimeString = formatter.format(startTime);
        String endTimeString = formatter.format(endTime);

        ParkingApiErrorEnum parkingApiErrorEnum = ParkingApiErrorEnum.START_DATE_END_DATE_SAME;
        Integer errorCode = parkingApiErrorEnum.getErrorCode();
        String message = parkingApiErrorEnum.getErrorMessage();

        when(parkingRateService.findCurrentRateForDateTimeInterval(ArgumentMatchers.any(ZonedDateTime.class), ArgumentMatchers.any(ZonedDateTime.class))).
                thenThrow(new ParkingServiceException(message, errorCode));

        mockMvc.perform(get("/api/parking/rate/start/" + startTimeString + "/end/" + endTimeString).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.statusCode").value(errorCode))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.startTime").value("2019-09-12T12:00:00-05:00"))
                .andExpect(jsonPath("$.endTime").value("2019-09-12T12:00:00-05:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_createParkingRates_Success() throws Exception {
        ParkingRateListWrapper parkingRateListWrapper = new ParkingRateListWrapper();
        List<ParkingRateCreateRequest> inputRates = Lists.newArrayList();

        ParkingRateCreateRequest parkingRateCreateRequest1 = new ParkingRateCreateRequest();
        parkingRateCreateRequest1.setDays("sat,sun");
        parkingRateCreateRequest1.setTz("America/Chicago");
        parkingRateCreateRequest1.setTimes("0900-1700");
        parkingRateCreateRequest1.setPrice(5320);
        inputRates.add(parkingRateCreateRequest1);

        ParkingRateCreateRequest parkingRateCreateRequest2 = new ParkingRateCreateRequest();
        parkingRateCreateRequest2.setDays("tues,thurs");
        parkingRateCreateRequest2.setTz("America/Chicago");
        parkingRateCreateRequest2.setTimes("0900-1700");
        parkingRateCreateRequest2.setPrice(5320);
        inputRates.add(parkingRateCreateRequest2);

        parkingRateListWrapper.setRates(inputRates);

        when(parkingRateService.addHourlyPrices(anyList())).thenReturn(Boolean.TRUE, Boolean.FALSE);


        mockMvc.perform(put("/api/parking/rates").contentType(MediaType.APPLICATION_JSON)
                .content(getParkingRateListWrapperAsJson(parkingRateListWrapper)).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.rates", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.rates[0].days", Matchers.equalTo("sat,sun")))
                .andExpect(jsonPath("$.rates[0].times", Matchers.equalTo("0900-1700")))
                .andExpect(jsonPath("$.rates[0].tz", Matchers.equalTo("America/Chicago")))
                .andExpect(jsonPath("$.rates[0].price", Matchers.equalTo(5320)))
                .andExpect(status().isCreated());
    }

    private static String getParkingRateListWrapperAsJson(ParkingRateListWrapper ParkingRateListWrapper) {
        //Tells the mapper to represent a Date as a String in JSON.
        ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(ParkingRateListWrapper);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
