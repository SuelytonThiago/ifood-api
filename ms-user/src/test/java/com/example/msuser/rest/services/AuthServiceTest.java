package com.example.msuser.rest.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.repositories.UserRepository;
import com.example.msuser.rest.dto.UserLoginDto;
import com.example.msuser.rest.services.exceptions.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    private PasswordEncoder encoder;

    private UserLoginDto userLogin;

    private String access_token, refresh_token;

    @BeforeEach
    public void beforeEach(){
        encoder = new BCryptPasswordEncoder();

        userLogin = new UserLoginDto();
        userLogin.setEmail("ana@example.com");
        userLogin.setPassword(encoder.encode("senha123"));

        access_token = JWT.create().withSubject(userLogin.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));
        refresh_token = JWT.create().withSubject(userLogin.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));
    }

    @DisplayName("test Given User Login Dto Object when Generate Tokens then Return Map String Of Tokens")
    @Test
    void testGivenUserLoginDtoObject_whenGenerateTokens_thenReturnMapStringOfTokens(){
        Users simulatedUser = new Users();
        UsernamePasswordAuthenticationToken authResult =
                new UsernamePasswordAuthenticationToken(
                        simulatedUser,null);
        given(authenticationManager.authenticate(any())).willReturn(authResult);
        given(tokenService.generateAccessToken(any(Users.class))).willReturn(access_token);
        given(tokenService.generateRefreshToken(any(Users.class))).willReturn(refresh_token);

        var tokens = authService.generateTokens(userLogin);

        assertThat(tokens.get("refresh_token")).isEqualTo(refresh_token);
        assertThat(tokens.get("access_token")).isEqualTo(access_token);
        verify(authenticationManager).authenticate(any());
        verify(tokenService).generateAccessToken(any(Users.class));
        verify(tokenService).generateRefreshToken(any(Users.class));
        verifyNoMoreInteractions(authenticationManager);
        verifyNoMoreInteractions(tokenService);
    }

    @DisplayName("test Given User Login Dto Object With Invalid Email when Generate Tokens then Throw User NotFound")
    @Test
    void testGivenUserLoginDtoObjectWithInvalidEmail_whenGenerateTokens_thenThrowUserNotFound(){
        given(authenticationManager.authenticate(any())).willThrow(new BadCredentialsException("the email or password is incorrect"));

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> authService.generateTokens(userLogin))
                .withMessage("the email or password is incorrect");

        verify(authenticationManager).authenticate(any());
        verifyNoMoreInteractions(authenticationManager);

    }

    @DisplayName("test Given HttpServletRequest then AttAccessToken then Return Access Token")
    @Test
    void testGivenHttpServletRequest_thenAttAccessToken_thenReturnAccessToken(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        Users user = new Users();
        user.setEmail(userLogin.getEmail());
        user.setPassword(userLogin.getPassword());
        given(request.getHeader(anyString())).willReturn("Bearer " + access_token);
        given(tokenService.getSubject(anyString())).willReturn(userLogin.getEmail());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(tokenService.isTokenValid(anyString(),any(Users.class))).willReturn(true);
        given(tokenService.generateAccessToken(any(Users.class))).willReturn(access_token);

        var outputString = authService.attAccessToken(request);

        assertThat(outputString).isEqualTo(access_token);
        verify(request).getHeader(anyString());
        verify(tokenService).getSubject(anyString());
        verify(userRepository).findByEmail(anyString());
        verify(tokenService).isTokenValid(anyString(),any(Users.class));
        verify(tokenService).generateAccessToken(any(Users.class));
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(request);
    }

    @DisplayName("test Given HttpServletRequest then AttAccessToken then Throw Custom Exception")
    @Test
    void testGivenHttpServletRequest_thenAttAccessToken_thenThrowCustomException(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        Users user = new Users();
        user.setEmail(userLogin.getEmail());
        user.setPassword(userLogin.getPassword());
        given(request.getHeader(anyString())).willReturn("Bearer " + access_token);
        given(tokenService.getSubject(anyString())).willReturn(userLogin.getEmail());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(tokenService.isTokenValid(anyString(),any(Users.class))).willReturn(false);

        var ex = assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> authService.attAccessToken(request)).withMessage("invalid token");

        verify(request).getHeader(anyString());
        verify(tokenService).getSubject(anyString());
        verify(userRepository).findByEmail(anyString());
        verify(tokenService).isTokenValid(anyString(),any(Users.class));
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(request);
    }
}
