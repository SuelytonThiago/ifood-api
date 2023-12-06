package com.example.msstore.rest.controllers;

import com.example.msstore.rest.dto.FoodDto;
import com.example.msstore.rest.dto.FoodRequestUpdate;
import com.example.msstore.rest.services.FoodService;
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
@RequestMapping("/api/foods")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping("/create/{idRestaurant}")
    @Operation(summary = "Used to register a new food in the restaurant with food request and restaurant id. Only authenticated establishment owners can use this endpoint.")
    public ResponseEntity<Void>create(@RequestBody @Valid FoodDto foodDto,
                                      @PathVariable Long idRestaurant,
                                      HttpServletRequest request){
        foodService.createNewFood(foodDto,idRestaurant,request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/category")
    @Operation(summary = "Used to search all foods by category name")
    public ResponseEntity<List<FoodDto>>findByCategory(@RequestParam String category){
        return ResponseEntity.ok(foodService.findByCategory(category));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete a food from the store. Can only be used by the owner of the authenticated establishment")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        foodService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Used to update the data of a food item in the store. Can only be used by the owner of the authenticated establishment")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid FoodRequestUpdate foodRequestUpdate){
        foodService.updateFoodData(id,foodRequestUpdate);
        return ResponseEntity.noContent().build();
    }
}
