package com.example.msorder.rest.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msorder.rest.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {


    @InjectMocks
    private TokenService tokenService;

    private String email;
    private Long id;
    private String jwtSecret;
    private int expiresAccessToken;
    private String token;

    @BeforeEach
    public void beforeEach(){
        email = "user@example.com";
        id = 1L;
        expiresAccessToken = 600000;
        jwtSecret = "secret";

        token = JWT.create().withSubject(email)
                .withClaim("id",id)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiresAccessToken))
                .sign(Algorithm.HMAC512(jwtSecret));
    }


    @Test
    @DisplayName("test Given HttpServletRequest When GetClaimId Then Return UserId")
    public void testGetClaimId(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);
        ReflectionTestUtils.setField(tokenService,"jwtSecret", jwtSecret);

        var outputId = tokenService.getClaimId(request);
        assertThat(outputId).isNotNull();
        assertThat(outputId).isEqualTo(1L);
    }

    @Test
    @DisplayName("test Given HttpServletRequest When GetSubject Then Return UserEmail")
    public void testGetSubject(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);

        var outputEmail = tokenService.getSubject(token);
        assertThat(outputEmail).isNotNull();
        assertThat(outputEmail).isEqualTo(email);
    }
}
