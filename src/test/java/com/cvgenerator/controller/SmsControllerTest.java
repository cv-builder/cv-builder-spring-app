package com.cvgenerator.controller;

import com.cvgenerator.domain.entity.SmsToken;
import com.cvgenerator.service.implementation.SmsTokenServiceImpl;
import com.cvgenerator.utils.service.implementation.SmsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
@DisplayName("Request to sms controller")
public class SmsControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private SmsServiceImpl smsService;
    @MockBean private SmsTokenServiceImpl smsTokenService;
    private String jwt;

    @BeforeEach
    void setUp() {

        jwt = Jwts
                .builder()
                .claim("role", "USER")
                .setSubject("admin")
                .setExpiration(new Date(System.currentTimeMillis() + 86_400_000))
                .signWith(SignatureAlgorithm.HS256, "6fg28#h$h*uiq2con!%^&k5k()_mrj8")
                .compact();

    }

    @Test
    @DisplayName("should return status ok after request for sms token")
    void shouldReturnStatusOk_afterRequestForSmsToken() throws Exception {

        String content = "{ \"email\": \"jankowalski@gmail.com\" }";

        mockMvc.perform(post("/api/sms")
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
                    .header("Access-Control-Expose-Headers", "Authorization"))
                    .andExpect(status().isOk());

    }

    @Test
    @DisplayName("should return status ok after request for validate sms token")
    void shouldReturnStatusOk_afterRequestForValidateSmsToken() throws Exception {

        SmsToken smsToken = SmsToken.builder().value("654728").build();
        BDDMockito.given(smsTokenService.findSmsTokenByValue("654728")).willReturn(smsToken);
        String content = "{ \"smsToken\": \"654728\" }";

        mockMvc.perform(post("/api/validate-sms")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .header("Access-Control-Expose-Headers", "Authorization"))
                .andExpect(status().isOk());

    }
}
