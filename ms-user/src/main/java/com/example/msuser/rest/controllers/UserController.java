package com.example.msuser.rest.controllers;

import com.example.msuser.rest.dto.AddressResponseDto;
import com.example.msuser.rest.dto.CardResponseDto;
import com.example.msuser.rest.dto.UserRequestDto;
import com.example.msuser.rest.dto.UserRequestUpdate;
import com.example.msuser.rest.services.TokenService;
import com.example.msuser.rest.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "serves to create a user, this method does not need authentication")
    public ResponseEntity<Void> createNewUser(@RequestBody @Valid UserRequestDto requestDto){
        userService.createNewUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PatchMapping("/update")
    @Operation(summary = "serves to update some of the user data. You need to be authenticated")
    public ResponseEntity<Void> update(@RequestBody @Valid UserRequestUpdate requestDto,
                                       HttpServletRequest request){
        userService.update(requestDto,request);
        return ResponseEntity.noContent().build();
    }
}
