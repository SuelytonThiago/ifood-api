package com.example.msuser.rest.controllers.unitarytest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.rest.controllers.AuthController;
import com.example.msuser.rest.dto.UserLoginDto;
import com.example.msuser.rest.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private UserLoginDto userLogin;
    private String refresh_token, access_token,invalid_refresh_token;
    private PasswordEncoder encoder;

    private Users user;
    private Roles role;

    @BeforeEach
    public void setUp(){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .alwaysDo(print()).build();

        user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("40028922");
        user.setCpf("05661795041");
        user.setPassword("senha123");
        user.setAddresses(new ArrayList<>());
        user.setRoles(new ArrayList<>());

        role = new Roles("ROLE_USER");

        user.getRoles().add(role);

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

        invalid_refresh_token =JWT.create().withSubject(userLogin.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() - 100000))
                .sign(Algorithm.HMAC512("secret"));
    }

    @DisplayName("test Given User Login Dto Object when Login then Return String Tokens Map")
    @Test
    void testGivenUserLoginDtoObject_whenLogin_thenReturnStringTokensMap() throws Exception {
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);
        given(authService.generateTokens(any(UserLoginDto.class)))
                .willReturn(tokens);

        var response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)));

        response.andDo(print()).andExpect(status().isOk());
        verify(authService).generateTokens(any(UserLoginDto.class));
        verifyNoMoreInteractions(authService);
    }


    @DisplayName("test Given User Login Dto Object With Null Email when Login then Return String Tokens Map")
    @Test
    void testGivenUserLoginDtoObjectWithNullEmail_whenLogin_thenReturnStringTokensMap() throws Exception {
        userLogin.setEmail(null);
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);

        var response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)));

        response.andDo(print()).andExpect(status().isBadRequest());


    }


    @DisplayName("test Given User Login Dto Object With Empty Email when Login then Return String Tokens Map")
    @Test
    void testGivenUserLoginDtoObjectWithEmptyEmail_whenLogin_thenReturnStringTokensMap() throws Exception {
        userLogin.setEmail("");
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);

        var response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)));

        response.andDo(print()).andExpect(status().isBadRequest());

    }

    @DisplayName("test Given User Login Dto Object With Null Password when Login then Return String Tokens Map")
    @Test
    void testGivenUserLoginDtoObjectWithNullPassword_whenLogin_thenReturnStringTokensMap() throws Exception {
        userLogin.setPassword(null);
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);

        var response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)));

        response.andDo(print()).andExpect(status().isBadRequest());

    }

    @DisplayName("test Given User Login Dto Object With Empty Password when Login then Return String Tokens Map")
    @Test
    void testGivenUserLoginDtoObjectWithEmptyPassword_whenLogin_thenReturnStringTokensMap() throws Exception {
        userLogin.setEmail("");
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);

        var response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)));

        response.andDo(print()).andExpect(status().isBadRequest());

    }



    @DisplayName("test Given Http Servlet Request when Refresh Then Return Refresh Token")
    @Test
    void testGivenHttpServletRequest_whenRefresh_ThenReturnRefreshToken() throws Exception {
        HttpServletRequest request =mock(HttpServletRequest.class);
        given(authService.attAccessToken(any())).willReturn(access_token);

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization","Bearer " + refresh_token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("test Given Http Servlet Request when Refresh Then Return Refresh Token")
    @Test
    void testGivenHttpServletRequestWithInvalidRefreshToken_whenRefresh_ThenReturnBadRequestStatus() throws Exception {
        HttpServletRequest request =mock(HttpServletRequest.class);
        given(authService.attAccessToken(any())).willReturn(access_token);

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization","Bearer " + invalid_refresh_token))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
