package com.abuob.parking.controller;


import com.abuob.parking.dto.HourlyPriceDTO;
import com.abuob.parking.service.ParkingApiErrorEnum;
import com.abuob.parking.service.ParkingRateService;
import com.abuob.parking.service.ParkingServiceException;
import com.abuob.parking.utils.ParkingRateUtil;
import com.abuob.parking.web.request.ParkingRateCreateRequest;
import com.abuob.parking.web.request.ParkingRateListWrapper;
import com.abuob.parking.web.response.ParkingRateResponse;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("api/parking")
@Api(description = "Operations pertaining to parking rate/price functionality")
public class ParkingController {

    private static final Logger logger = LoggerFactory.getLogger(ParkingController.class);

    public static final String UNAVAILABLE_PRICE = "unavailable";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Autowired
    private ParkingRateService parkingRateService;

    @ApiOperation(value = "Retrieves a the current price for a time interval in the system")
    @GetMapping(value = "rate/start/{startDateTime}/end/{endDateTime}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ParkingRateResponse> findCurrentRate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDateTime,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDateTime) {
        ParkingRateResponse parkingRateResponse = new ParkingRateResponse();
        parkingRateResponse.setStatusCode(0);
        parkingRateResponse.setMessage("");

        Integer price;

        //No start date
        if (Objects.isNull(startDateTime)) {
            parkingRateResponse.setStatusCode(ParkingApiErrorEnum.START_DATE_MISSING.getErrorCode());
            parkingRateResponse.setMessage(ParkingApiErrorEnum.START_DATE_MISSING.getErrorMessage());
            return new ResponseEntity<>(parkingRateResponse, HttpStatus.BAD_REQUEST);
        }

        //No end date
        if (Objects.isNull(endDateTime)) {
            parkingRateResponse.setStatusCode(ParkingApiErrorEnum.END_DATE_MISSING.getErrorCode());
            parkingRateResponse.setMessage(ParkingApiErrorEnum.END_DATE_MISSING.getErrorMessage());
            return new ResponseEntity<>(parkingRateResponse, HttpStatus.BAD_REQUEST);
        }

        parkingRateResponse.setStartTime(startDateTime);
        parkingRateResponse.setEndTime(endDateTime);

        try {
            logger.debug("Looking for price for startDate: {} - endDate: {}", formatter.format(startDateTime), formatter.format(endDateTime));
            price = parkingRateService.findCurrentRateForDateTimeInterval(startDateTime, endDateTime);

            parkingRateResponse.setPrice(String.valueOf(price));
            logger.debug("Found price: {} for startDate: {} - endDate: {}", price, formatter.format(startDateTime), formatter.format(endDateTime));
            return new ResponseEntity<>(parkingRateResponse, HttpStatus.OK);
        } catch (ParkingServiceException e) {
            String message = e.getMessage();
            Integer errorCode = e.getErrorCode();

            logger.debug("for price for startDate: {} - endDate: {} got errorCode: {}",
                    formatter.format(startDateTime), formatter.format(endDateTime), errorCode);
            parkingRateResponse.setMessage(message);
            parkingRateResponse.setStatusCode(errorCode);

            //Check for date span over multiple rates
            if (errorCode == ParkingApiErrorEnum.DATE_RANGE_SPANS_MULTIPLE_RATE.getErrorCode()) {
                parkingRateResponse.setPrice(UNAVAILABLE_PRICE);
            }
            return new ResponseEntity<>(parkingRateResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Add/Update a list of parking rates in the system")
    @PutMapping(value = "rates", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ParkingRateListWrapper> createParkingRates(@RequestBody ParkingRateListWrapper parkingRateListWrapper) {

        List<ParkingRateCreateRequest> inputRatesList = parkingRateListWrapper.getRates();

        //Echo back all the successful rates persisted to the repository
        ParkingRateListWrapper responseWrapper = new ParkingRateListWrapper();
        List<ParkingRateCreateRequest> responseRatesList = Lists.newArrayList();
        responseWrapper.setRates(responseRatesList);

        List<HourlyPriceDTO> hourlyPriceDTOList;
        boolean result;

        //Process each rate request separately and add to the response if the add is successful
        for (ParkingRateCreateRequest parkingRateCreateRequest : inputRatesList) {
            hourlyPriceDTOList = ParkingRateUtil.convertToRateDTO(Lists.newArrayList(parkingRateCreateRequest));
            result = parkingRateService.addHourlyPrices(hourlyPriceDTOList);
            if (result) {
                responseRatesList.add(parkingRateCreateRequest);
            }
        }
        return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
    }
}
