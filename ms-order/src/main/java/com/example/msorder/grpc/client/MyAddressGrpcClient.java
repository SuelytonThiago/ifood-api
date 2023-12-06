package com.example.msorder.grpc.client;

import com.example.address.AddressResponse;
import com.example.address.AddressServiceGrpc;
import com.example.address.RequestId;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyAddressGrpcClient {

    public AddressResponse findAddressById(Long id){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-user",9090).usePlaintext().build();
        AddressServiceGrpc.AddressServiceBlockingStub  addressClient = AddressServiceGrpc.newBlockingStub(channel);

        RequestId requestId = RequestId.newBuilder().setId(id).build();
        var response = addressClient.findById(requestId);
        channel.shutdown();
        return response;
    }
}
