package com.example.msorder.grpc.client;
import com.example.card.CardResponse;
import com.example.card.CardServiceGrpc;
import com.example.card.RequestId;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyCardGrpcClient {

    public CardResponse findCardById(Long id){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("ms-user",9090).usePlaintext().build();
        CardServiceGrpc.CardServiceBlockingStub cardClient = CardServiceGrpc.newBlockingStub(channel);

        RequestId requestId = RequestId.newBuilder().setId(id).build();
        var response = cardClient.findById(requestId);
        channel.shutdown();
        return response;
    }
}
