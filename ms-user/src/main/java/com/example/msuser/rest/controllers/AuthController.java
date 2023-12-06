package com.example.msuser.rest.controllers;

import com.example.msuser.rest.dto.UserLoginDto;
import com.example.msuser.rest.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Serves to authenticate and receive access and update tokens")
    public ResponseEntity<Map<String,String>> login(@RequestBody @Valid UserLoginDto userDto){
        Map<String,String> map = authService.generateTokens(userDto);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/refresh")
    @Operation(summary = "It is used to generate a new access token. To do this, use the update token you received when authenticating.")
    public ResponseEntity<String> refresh(HttpServletRequest request){
        return ResponseEntity.ok(authService.attAccessToken(request));
    }
}
