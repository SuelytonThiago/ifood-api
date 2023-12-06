package com.example.msuser.rest.services;

import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.repositories.UserRepository;
import com.example.msuser.rest.dto.UserLoginDto;
import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.NotAuthorizeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {


    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public Map<String,String> generateTokens(UserLoginDto loginDto){
        try {
            Authentication authentication = authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

            var user = (Users) authentication.getPrincipal();

            String access_token = tokenService.generateAccessToken(user);
            String refresh_token = tokenService.generateRefreshToken(user);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);
            return tokens;
        }
        catch(BadCredentialsException e){
            throw new CustomException("the email or password is incorrect");
        }
    }

    public String attAccessToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        var token = authHeader.replace("Bearer ","");
        var email = tokenService.getSubject(token);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotAuthorizeException("unauthorized user"));

        if(tokenService.isTokenValid(token,user)){
            return tokenService.generateAccessToken(user);
        }
        throw new CustomException("invalid token");
    }
}
