package com.example.msuser.grpc;

import com.example.card.CardResponse;
import com.example.card.CardResponseList;
import com.example.card.CardServiceGrpc;
import com.example.card.RequestId;
import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class MyGrpcCardServiceIntegrationTest extends ContainerBase {

    private ManagedChannel channel;

    @BeforeEach
    public void setUp(){
        channel = ManagedChannelBuilder.forAddress("localhost", ConstantsTest.SERVER_GRPC)
                .usePlaintext()
                .build();
    }

    @AfterEach
    public void tearDown() {
        channel.shutdownNow();
    }

    @Test
    @Order(1)
    @DisplayName("integration Test Given CardId When FindById Then Return Card Response")
    public void integrationTestFindById() {

        CardServiceGrpc.CardServiceBlockingStub client = CardServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(1L).build();
        CardResponse response = client.findById(request);

        assertThat(response.getName()).isEqualTo("myCard");
        assertThat(response.getCvv()).isEqualTo("266");
        assertThat(response.getTypeCard()).isEqualTo("CREDIT_CARD");
        assertThat(response.getExpiration()).isEqualTo("12/2028");
        assertThat(response.getNumber()).isEqualTo("1234567891234567");
    }

    @Test
    @Order(2)
    @DisplayName("integration Test Given CardId When FindById Then Return CardResponse")
    public void integrationTestFindByIdWithInvalidCardId(){
        CardServiceGrpc.CardServiceBlockingStub client = CardServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(5L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findById(request))
                .withMessage("NOT_FOUND: The card is not found");
    }

    @Test
    @Order(3)
    @DisplayName("integration Test Given UserId When FindAllByIdUser Then Return CardResponseList")
    public void integrationTestFindAllByIdUser(){
        CardServiceGrpc.CardServiceBlockingStub client = CardServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(1L).build();

        CardResponseList response = client.findAllByIdUser(request);

        assertThat(response.getCardsList().size()).isEqualTo(1);
        assertThat(response.getCardsList())
                .extracting("id","name","number","expiration","cvv","typeCard")
                .contains(
                        tuple(1L,"myCard","1234567891234567","12/2028","266","CREDIT_CARD")
                );
    }

    @Test
    @Order(4)
    @DisplayName("integration Test Given With Invalid UserId When FindAllByIdUser Then throw NotFoundException")
    public void integrationTestFindAllByIdUserWithInvalidUserId(){
        CardServiceGrpc.CardServiceBlockingStub client = CardServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(50L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findAllByIdUser(request))
                .withMessage("NOT_FOUND: The user is not found");
    }

    @Test
    @Order(5)
    @DisplayName("integration Test Given With User Empty PaymentMethods List When FindAllByIdUser Then throw CustomException")
    public void integrationTestFindAllByIdUserWithEmptyCardList(){
        CardServiceGrpc.CardServiceBlockingStub client = CardServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(2L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findAllByIdUser(request))
                .withMessage("INVALID_ARGUMENT: The user does not have any registered payment methods");
    }


}
