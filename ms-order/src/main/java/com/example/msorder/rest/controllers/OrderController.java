package com.example.msorder.rest.controllers;

import com.example.msorder.rest.dto.OrderRequestDto;
import com.example.msorder.rest.dto.OrderResponseDto;
import com.example.msorder.rest.dto.OrderStatusUpdate;
import com.example.msorder.rest.services.OrderService;
import com.example.msorder.rest.services.TokenService;
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
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final TokenService tokenService;

    @PostMapping("/create")
    @Operation(summary = "Used to create a new order. You need to be authenticated")
    public ResponseEntity<Void> create(@RequestBody @Valid OrderRequestDto orderRequestDto,
                                       HttpServletRequest request){
        orderService.createNewOrder(orderRequestDto,request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getAll")
    @Operation(summary = "Used to collect all user orders. You need to be authenticated")
    public ResponseEntity<List<OrderResponseDto>> findAllOrdersByUserId(HttpServletRequest request){
        var userId = tokenService.getClaimId(request);
        return ResponseEntity.ok(orderService.findAllOrdersByUserId(userId));
    }

    @PatchMapping("/update")
    @Operation(summary = "Used to update the order status. Can only be used by the administrator")
    public ResponseEntity<Void>update(@RequestBody @Valid OrderStatusUpdate update){
        orderService.updateOrderStatus(update);
        return ResponseEntity.noContent().build();
    }

}
