package com.example.msuser.rest.controllers;

import com.example.msuser.rest.dto.AddressRequestDto;
import com.example.msuser.rest.dto.AddressRequestUpdate;
import com.example.msuser.rest.dto.AddressResponseDto;
import com.example.msuser.rest.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;
    @PostMapping("/create")
    @Operation(summary = "Used to register a new address for the authenticated user")
    public ResponseEntity<Void> createNewAddress(HttpServletRequest request,
                                                 @RequestBody @Valid AddressRequestDto objectDto){
        addressService.createNewAddress(request,objectDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/myAddresses")
    @Operation(summary = "serves to get the addresses that the authenticated user has registered")
    public ResponseEntity<List<AddressResponseDto>> findAllAddressesMethods(HttpServletRequest request){
        return ResponseEntity.ok(addressService.findAllAddressMethod(request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete the authenticated user's address with address id")
    public ResponseEntity<Void> delete(HttpServletRequest request,@PathVariable Long id){
        addressService.deleteById(request, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Used to updated the authenticated user's address with address id")
    public ResponseEntity<Void> update(@RequestBody @Valid AddressRequestUpdate requestUpdate,
                                       @PathVariable Long id){
        addressService.updateAddressData(requestUpdate,id);
        return ResponseEntity.noContent().build();
    }
}
