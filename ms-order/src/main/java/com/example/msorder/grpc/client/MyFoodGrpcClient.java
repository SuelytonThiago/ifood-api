package com.example.msorder.grpc.client;

import com.example.food.FoodResponse;
import com.example.food.FoodServiceGrpc;
import com.example.food.RequestId;
import com.example.msorder.rest.services.exceptions.ObjectNotFoundException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class MyFoodGrpcClient {

    public FoodResponse findById(Long id){
        try{
            ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-store",9292).usePlaintext().build();
            FoodServiceGrpc.FoodServiceBlockingStub foodClient =FoodServiceGrpc.newBlockingStub(channel);

            RequestId request = RequestId.newBuilder().setId(id).build();
            FoodResponse response = foodClient.findById(request);
            channel.shutdown();

            return response;
        }
        catch (StatusRuntimeException e){
            throw new ObjectNotFoundException("The food is not found");
        }
    }
}
