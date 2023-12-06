package com.example.msorder.rest.controllers;

import com.example.msorder.rest.dto.BagRequestDto;
import com.example.msorder.rest.dto.BagRequestUpdate;
import com.example.msorder.rest.dto.BagResponseDto;
import com.example.msorder.rest.services.BagService;
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
@RequestMapping("/api/bag")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class BagController {

    private final BagService bagService;

    @PostMapping("/create")
    @Operation(summary = "Used to add an item to the bag. You need to be authenticated")
    public ResponseEntity<Void> create(@RequestBody @Valid BagRequestDto dto,
                                       HttpServletRequest request){
        bagService.addFoodToBag(request,dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Used to pick up all the items in your bag. You need to be authenticated")
    public ResponseEntity<List<BagResponseDto>> getBagItems(HttpServletRequest request){
        return ResponseEntity.ok(bagService.getBagItem(request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Used to delete an item from the bag. You need to be authenticated")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        bagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Used to update the number of items in the bag. You must be authenticated.")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid BagRequestUpdate bagRequestUpdate){
        bagService.updateBagItemData(id,bagRequestUpdate);
        return ResponseEntity.noContent().build();
    }
}
