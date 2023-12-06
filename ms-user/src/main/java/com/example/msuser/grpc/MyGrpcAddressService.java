package com.example.msuser.grpc;

import com.example.address.AddressResponse;
import com.example.address.AddressResponseList;
import com.example.address.AddressServiceGrpc;
import com.example.address.RequestId;
import com.example.msuser.rest.services.AddressService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
@AllArgsConstructor
public class MyGrpcAddressService extends AddressServiceGrpc.AddressServiceImplBase {


    private final AddressService addressService;

    @Override
    public void findById(RequestId request, StreamObserver<AddressResponse> responseObserver) {
        var address = addressService.findById(request.getId());
        AddressResponse response = AddressResponse.newBuilder()
                .setCep(address.getCep())
                .setCity(address.getCity())
                .setNeighborhood(address.getNeighborhood())
                .setState(address.getState())
                .setStreet(address.getStreet())
                .setId(address.getId()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findAllByIdUser(RequestId request, StreamObserver<AddressResponseList> responseObserver) {
        var addresses = addressService.findAllByUserId(request.getId());
                var outputList = addresses.stream().map(address-> AddressResponse.newBuilder()
                        .setCep(address.getCep())
                        .setCity(address.getCity())
                        .setId(address.getId())
                        .setNeighborhood(address.getNeighborhood())
                        .setState(address.getState())
                        .setStreet(address.getStreet())
                        .build()).collect(Collectors.toList());

        AddressResponseList addressResponseList = AddressResponseList
                .newBuilder()
                .addAllAddresses(outputList)
                .build();

        responseObserver.onNext(addressResponseList);
        responseObserver.onCompleted();
    }



}
