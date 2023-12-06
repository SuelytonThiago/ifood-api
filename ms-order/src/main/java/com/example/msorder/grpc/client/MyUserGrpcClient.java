package com.example.msorder.grpc.client;

import com.example.user.RequestEmail;
import com.example.user.UserResponse;
import com.example.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class MyUserGrpcClient {

    public UserResponse findUserByEmail(String email){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-user",9090).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub userClient = UserServiceGrpc.newBlockingStub(channel);

        RequestEmail request = RequestEmail.newBuilder().setEmail(email).build();
        var response = userClient.findByEmail(request);
        channel.shutdown();
        return response;
    }
}
