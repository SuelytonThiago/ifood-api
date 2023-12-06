package com.example.msuser.rest.controllers;
import com.example.msuser.rest.dto.CardRequestDto;
import com.example.msuser.rest.dto.CardRequestUpdate;
import com.example.msuser.rest.dto.CardResponseDto;
import com.example.msuser.rest.services.CardService;
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
@RequestMapping("/api/cards")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class CardController {

    private final CardService cardService;


    @PostMapping("/create")
    @Operation(summary = "Serves to register a card for the authenticated user")
    public ResponseEntity<Void> createNewCard(@RequestBody @Valid CardRequestDto dto,
                              HttpServletRequest request){
        cardService.createNewCard(request, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/paymentMethods")
    @Operation(summary = "Used to retrieve the payment methods that the authenticated user has registered")
    public ResponseEntity<List<CardResponseDto>> findAllPaymentMethods(HttpServletRequest request){
        return ResponseEntity.ok(cardService.findAllPaymentMethod(request));
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete an authenticated user's card with card id")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request){
        cardService.deleteById(id,request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Serves to update the data of an authenticated user card with card id")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid CardRequestUpdate update){
        cardService.updateUserCard(id, update);
        return ResponseEntity.noContent().build();
    }
}
