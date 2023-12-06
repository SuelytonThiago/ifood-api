package com.example.msstore.rest.controllers;

import com.example.msstore.rest.dto.CategoryDto;
import com.example.msstore.rest.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "Used to create a new category. Can only be used by the administrator")
    public ResponseEntity<Void> createNewCategory(@RequestBody @Valid CategoryDto categoryDto){
        categoryService.createNewCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Used to retrieve all categories in the system")
    public ResponseEntity<List<CategoryDto>> findAll(){
        return  ResponseEntity.ok(categoryService.findAll());
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete a category. Can only be used by the administrator")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
