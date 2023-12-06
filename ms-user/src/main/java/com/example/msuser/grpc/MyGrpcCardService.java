package com.example.msuser.grpc;

import com.example.card.CardResponse;
import com.example.card.CardResponseList;
import com.example.card.CardServiceGrpc;
import com.example.card.RequestId;
import com.example.msuser.domain.repositories.UserRepository;
import com.example.msuser.rest.services.CardService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@GrpcService
@AllArgsConstructor
public class MyGrpcCardService extends CardServiceGrpc.CardServiceImplBase {


    private final CardService cardService;


    @Override
    public void findById(RequestId request, StreamObserver<CardResponse> responseObserver) {
        var card = cardService.findById(request.getId());
        CardResponse response = CardResponse.newBuilder()
                .setCvv(card.getCvv())
                .setExpiration(card.getExpiration())
                .setId(card.getId())
                .setName(card.getName())
                .setNumber(card.getNumber())
                .setTypeCard(card.getType().toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findAllByIdUser(RequestId request, StreamObserver<CardResponseList> responseObserver) {
        var list = cardService.findAllCardsByUser(request.getId()).stream()
                .map(card -> CardResponse.newBuilder()
                        .setCvv(card.getCvv())
                        .setExpiration(card.getExpiration())
                        .setId(card.getId())
                        .setName(card.getName())
                        .setNumber(card.getNumber())
                        .setTypeCard(card.getType().toString())
                        .build()).collect(Collectors.toList());
        CardResponseList cardResponseList = CardResponseList
                .newBuilder()
                .addAllCards(list)
                .build();

        responseObserver.onNext(cardResponseList);
        responseObserver.onCompleted();
    }
}
