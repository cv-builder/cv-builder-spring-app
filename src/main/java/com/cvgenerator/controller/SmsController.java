package com.cvgenerator.controller;

import com.cvgenerator.service.implementation.SmsTokenServiceImpl;
import com.cvgenerator.utils.JsonProcessingService;
import com.cvgenerator.utils.service.implementation.SmsServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = {"${settings.cors_origin}"})
@Api(tags = "Sms controller")
@Slf4j
@RestController
@RequestMapping("/api")
public class SmsController {

    private final SmsServiceImpl smsService;
    private final JsonProcessingService jsonProcessingService;
    private final SmsTokenServiceImpl smsTokenService;

    @Autowired
    public SmsController(SmsServiceImpl smsService, JsonProcessingService jsonProcessingService, SmsTokenServiceImpl smsTokenService) {
        this.smsService = smsService;
        this.jsonProcessingService = jsonProcessingService;
        this.smsTokenService = smsTokenService;
    }

    @ApiOperation(value = "Send SMS to user using user's email")
    @PostMapping("/sms")
    @ResponseStatus(HttpStatus.OK)
    public void sendSmsToken(@ApiParam(value = "Request with Json object that has to contain 'email' field. Example: { \"email\": \"jankowalski@gmail.com\" }")
                       @RequestBody JsonNode request) throws Throwable {
        String email =jsonProcessingService.processJsonString(request, "email");
        smsService.sendSms(email);
    }

    @ApiOperation(value ="Process and validate provided sms token by user")
    @PostMapping("/validate-sms")
    @ResponseStatus(HttpStatus.OK)
    public void validateSmsToken(@ApiParam(value = "Request with Json object that has to contain 'smsToken' field. Example: { \"smsToken\": \"657452\" }")
                                 @RequestBody JsonNode request) throws MissingServletRequestParameterException {

        String smsToken = jsonProcessingService.processJsonString(request, "smsToken");
        smsTokenService.findSmsTokenByValue(smsToken);
    }
}
