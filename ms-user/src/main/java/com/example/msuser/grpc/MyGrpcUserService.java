package com.example.msuser.grpc;

import com.example.msuser.domain.entities.Users;
import com.example.msuser.rest.dto.UserRequestDto;
import com.example.msuser.rest.services.UserService;
import com.example.user.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class MyGrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserService userService;

    @Override
    public void createOwner(OwnerRequest request, StreamObserver<OwnerIdResponse> responseObserver) {
        var ownerObject = new UserRequestDto(
                request.getName(),
                request.getEmail(),
                request.getContactNumber(),
                request.getCpf(),
                request.getPassword());
        var user = userService.createNewOwner(ownerObject);
        OwnerIdResponse response = OwnerIdResponse.newBuilder().setId(user.getId()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findById(RequestId request, StreamObserver<UserResponse> responseObserver) {
        var user = userService.findById(request.getId());
        UserResponse userResponse = UserResponse.newBuilder()
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setContactNumber(user.getContactNumber())
                .setCpf(user.getCpf())
                .setPassword(user.getPassword())
                .setId(user.getId())
                .setRoles(convertRoles(user)).build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void findByEmail(RequestEmail request, StreamObserver<UserResponse> responseObserver) {
        var user = userService.findByEmail(request.getEmail());
        UserResponse userResponse = UserResponse.newBuilder()
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setContactNumber(user.getContactNumber())
                .setCpf(user.getCpf())
                .setPassword(user.getPassword())
                .setId(user.getId())
                .setRoles(convertRoles(user)).build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();

    }

    private RoleResponseList convertRoles(Users user){
        List<RoleResponse> roles = user.getRoles().stream()
                .map(role -> RoleResponse.newBuilder()
                        .setId(role.getId())
                        .setRoleName(role.getRoleName())
                        .build()).collect(Collectors.toList());

        return RoleResponseList
                .newBuilder()
                .addAllRoles(roles)
                .build();
    }

}
