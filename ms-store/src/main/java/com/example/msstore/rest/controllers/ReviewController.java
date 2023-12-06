package com.example.msstore.rest.controllers;

import com.example.msstore.rest.dto.ReviewRequestDto;
import com.example.msstore.rest.dto.ReviewRequestUpdate;
import com.example.msstore.rest.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reviews")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create/{id}")
    @Operation(summary = "Used to add a review to the store with store id and review request. Can only be used by registered users")
    public ResponseEntity<Void> create(@RequestBody @Valid ReviewRequestDto dto,
                                       @PathVariable Long id,
                                       HttpServletRequest request){
        reviewService.createNewReview(id,request,dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete the store review with review id. Can only be used by administrators and registered users")
    public ResponseEntity<Void>delete(@PathVariable Long id){
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Used to update the store's rating with review id and review request update. Can only be used by authenticated users")
    public ResponseEntity<Void>update(@PathVariable Long id,
                                      @RequestBody @Valid ReviewRequestUpdate requestUpdate){
        reviewService.updateReviewData(id,requestUpdate);
        return ResponseEntity.noContent().build();
    }

}
