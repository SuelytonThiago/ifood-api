package com.example.msuser.rest.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.entities.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private Users user;
    private Roles role;


    private String jwtSecret;
    private int expiresAccessToken;
    private int expiresRefreshToken;
    private String token;


    @BeforeEach
    public void setUp(){
        user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("99940028922");
        user.setCpf("05661795041");
        user.setPassword("senha123");
        user.setAddresses(new ArrayList<>());
        user.setRoles(new ArrayList<>());

        role = new Roles("ROLE_USER");

        user.getRoles().add(role);

        jwtSecret = "Secret";
        expiresAccessToken = 100000;
        expiresRefreshToken = 180000;

        token = JWT.create().withSubject(user.getEmail())
                .withClaim("id",user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiresAccessToken))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @Test
    void testGivenUserObject_whenGenerateAccessToken_thenReturnStringToken(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);
        ReflectionTestUtils.setField(tokenService,"expiresAccessToken",expiresAccessToken);

        var token = tokenService.generateAccessToken(user);

        assertThat(token).isNotNull();
    }

    @Test
    void testGivenUserObject_whenGenerateRefreshToken_thenReturnStringToken(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);
        ReflectionTestUtils.setField(tokenService,"expiresRefreshToken",expiresRefreshToken);

        var token = tokenService.generateRefreshToken(user);

        assertThat(token).isNotNull();
    }

    @Test
    void testGivenStringToken_whenGetSubject_thenReturnStringUserEmail(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);

        var email = tokenService.getSubject(token);

        assertThat(token).isNotNull();
        assertThat(email).isEqualTo(user.getEmail());
    }

    @Test
    void testGivenStringToken_whenExpirationDate_thenReturnDate(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);

        var date = tokenService.getExpirationDate(token);

        assertThat(date).isNotNull();
    }

    @Test
    void testGivenStringTokenValid_whenIsTokenExpired_thenReturnFalse(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);

        var validation = tokenService.isTokenExpired(token);

        assertThat(validation).isNotNull();
        assertThat(validation).isEqualTo( false);
    }

    @Test
    void testGivenStringTokenValidAndUserObjectValid_whenIsTokenValid_thenReturnTrue(){
        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);

        var validation = tokenService.isTokenValid(token,user);

        assertThat(validation).isNotNull();
        assertThat(validation).isEqualTo( true);
    }

    @Test
    void testGivenHttpServletRequest_whenGetUserEmailAuthenticated_thenReturnUserEmail(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);

        ReflectionTestUtils.setField(tokenService,"jwtSecret",jwtSecret);

        var email = tokenService.getUserEmailAuthenticated(request);

        assertThat(email).isNotNull();
        assertThat(email).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("tet given HttpServletRequest when GetClaimId then return userId")
    void testGetClaimId(){
        ReflectionTestUtils.setField(tokenService, "jwtSecret",jwtSecret);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);

        var id = tokenService.getClaimId(request);

        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(user.getId());

    }
}
