package com.example.msstore.grpc.service;

import com.example.food.*;
import com.example.msstore.rest.services.FoodService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@AllArgsConstructor
public class MyFoodServiceGrpc extends FoodServiceGrpc.FoodServiceImplBase {

    private final FoodService foodService;

    @Override
    public void findById(RequestId request, StreamObserver<FoodResponse> responseObserver) {
        var food = foodService.findById(request.getId());
        CategoryResponse category = CategoryResponse.newBuilder()
                .setName(food.getCategory().getName()).build();
        StoreResponse store = StoreResponse.newBuilder()
                .setName(food.getFoodStore().getName())
                .setId(food.getFoodStore().getId()).build();

        FoodResponse response = FoodResponse.newBuilder()
                .setName(food.getName())
                .setCategory(category)
                .setPrice(food.getPrice())
                .setQuantityStock(food.getQuantityStock())
                .setStore(store)
                .setId(food.getId()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
