package com.example.msstore.grpc.client;

import com.example.msstore.rest.services.exceptions.AlreadyExistException;
import com.example.msstore.rest.dto.OwnerRequestDto;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import com.example.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceGrpcClient {

    public UserResponse getUserByEmail(String email){
        try {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-user", 9090).usePlaintext().build();
            UserServiceGrpc.UserServiceBlockingStub userClient = UserServiceGrpc.newBlockingStub(channel);

            RequestEmail request = RequestEmail.newBuilder().setEmail(email).build();
            UserResponse response = userClient.findByEmail(request);
            channel.shutdown();

            return response;
        }
        catch (StatusRuntimeException e){
            throw new ObjectNotFoundException("This user is not found");
        }
    }

    public Long createOwner(OwnerRequestDto requestDto){
        try {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-user", 9090).usePlaintext().build();
            UserServiceGrpc.UserServiceBlockingStub userClient = UserServiceGrpc.newBlockingStub(channel);

            OwnerRequest ownerRequest = OwnerRequest.newBuilder()
                    .setName(requestDto.getName())
                    .setEmail(requestDto.getEmail())
                    .setContactNumber(requestDto.getContactNumber())
                    .setCpf(requestDto.getCpf())
                    .setPassword(requestDto.getPassword())
                    .build();

            var response = userClient.createOwner(ownerRequest);
            channel.shutdown();
            return response.getId();
        }
        catch(StatusRuntimeException e){
            throw new AlreadyExistException("This email or cpf already in use");
        }
    }

    public UserResponse findById(Long id){
        try{
            ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-user", 9090).usePlaintext().build();
            UserServiceGrpc.UserServiceBlockingStub userClient = UserServiceGrpc.newBlockingStub(channel);

            RequestId requestId = RequestId.newBuilder().setId(id).build();

            var response = userClient.findById(requestId);
            channel.shutdown();
            return response;
        }
        catch (StatusRuntimeException e){
            throw new ObjectNotFoundException("The user is not found");
        }
    }
}
