package com.example.msstore.rest.controllers;
import com.example.msstore.rest.dto.*;
import com.example.msstore.rest.services.StoreService;
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
@RequestMapping("/api/store")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/create")
    @Operation(summary = "Used to create a new store. Can only be used by the administrator")
    public ResponseEntity<Void>createNewStore(@RequestBody @Valid StoreRequestDto request){
        storeService.createNewStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/pharmacy")
    @Operation(summary = "Used to search for all pharmacies registered in the database")
    public ResponseEntity<List<StoreResponseDto>> findAllByPharmacy(){
        return ResponseEntity.ok(storeService.findAllPharmacy());
    }

    @GetMapping("/supermarket")
    @Operation(summary = "Used to search for all supermarkets registered in the database")
    public ResponseEntity<List<StoreResponseDto>> findAllBySupermarket(){
        return ResponseEntity.ok(storeService.findAllSupermarket());
    }

    @GetMapping("/restaurant")
    @Operation(summary = "Used to search for all restaurants registered in the database")
    public ResponseEntity<List<StoreResponseDto>> findAllByRestaurant(){
        return ResponseEntity.ok(storeService.findAllRestaurant());
    }

    @GetMapping("/search")
    @Operation(summary = "Used to find the store by name")
    public ResponseEntity<StoreResponseDto>findByName(@RequestParam String storeName){
        return ResponseEntity.ok(new StoreResponseDto(storeService.findByName(storeName)));
    }

    @GetMapping("/reviews/{id}")
    @Operation(summary = "Used to find the ReviewsList by store name")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long id){
       return ResponseEntity.ok(storeService.getReviews(id));
    }
    @GetMapping("/menu/{id}")
    @Operation(summary = "Used to get all store reviews")
    public ResponseEntity<List<FoodDto>> getStoreMenu(@PathVariable Long id){
        return ResponseEntity.ok(storeService.showStoreMenu(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete the establishment by id. Can only be used by the administrator")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        storeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "It is used to update some store data with the store ID. Can only be used by the owner of the registered store")
    public ResponseEntity<Void> update(@RequestBody @Valid StoreRequestUpdate requestUpdate,
                                       @PathVariable Long id,
                                       HttpServletRequest request){
        storeService.updateStoreData(id,requestUpdate,request);
        return ResponseEntity.noContent().build();
    }


}
