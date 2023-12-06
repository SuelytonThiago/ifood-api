package com.example.msuser.grpc;
import com.example.address.AddressResponse;
import com.example.address.AddressResponseList;
import com.example.address.AddressServiceGrpc;
import com.example.address.RequestId;
import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import com.example.msuser.domain.repositories.UserRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class MyGrpcAddressServiceIntegrationTest extends ContainerBase {

    private ManagedChannel channel;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void setUp(){
        channel = ManagedChannelBuilder.forAddress("localHost", ConstantsTest.SERVER_GRPC)
                .usePlaintext()
                .build();
    }

    @AfterEach
    public void tearDown() {
        channel.shutdownNow();
    }

    @Test
    @Order(1)
    @DisplayName("integration Test given AddressId When findById thenReturn AddressResponse")
    public void integrationTestFindById(){
        AddressServiceGrpc.AddressServiceBlockingStub client = AddressServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(1L).build();
                AddressResponse response = client.findById(request);

        assertThat(response.getStreet()).isEqualTo("rua a");
        assertThat(response.getState()).isEqualTo("estado a");
        assertThat(response.getNeighborhood()).isEqualTo("bairro a");
        assertThat(response.getCep()).isEqualTo("00000000");
        assertThat(response.getCity()).isEqualTo("cidade a");
    }

    @Test
    @Order(2)
    @DisplayName("integration Test given Invalid AddressId When findById then Throw NotFound Exception")
    public void integrationTestFindByIdWithInvalidAddressId() {
        AddressServiceGrpc.AddressServiceBlockingStub client = AddressServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(5L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findById(request))
                .withMessage("NOT_FOUND: The address is not found");
    }

    @Test
    @Order(3)
    @DisplayName("integration Test given UserId When FindAllByUserId then return AddressResponseList")
    public void integrationTestFindAllByUserId() {

        System.out.println(userRepository.findById(1L).isEmpty());
        AddressServiceGrpc.AddressServiceBlockingStub client = AddressServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(1L).build();

        AddressResponseList response = client.findAllByIdUser(request);

        assertThat(response.getAddressesList().size()).isEqualTo(1);
        assertThat(response.getAddressesList())
                .extracting("street","state","city","cep","neighborhood")
                .contains(
                        tuple("rua a","estado a","cidade a","00000000","bairro a")
                );
    }

    @Test
    @Order(4)
    @DisplayName("integration Test given With Invalid UserId When FindAllByUserId then throw NotFound Exception")
    public void integrationTestFindAllByUserIdWithInvalidUserId() {
        AddressServiceGrpc.AddressServiceBlockingStub client = AddressServiceGrpc.newBlockingStub(channel);
        RequestId request = RequestId.newBuilder().setId(5L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findAllByIdUser(request))
                .withMessage("NOT_FOUND: The user is not found");

    }

    @Test
    @Order(5)
    @DisplayName("integration Test given With Empty AddressUserList When FindAllByUserId then throw Custom Exception")
    public void integrationTestFindAllByUserIdWithEmptyAddressUserList() {
        AddressServiceGrpc.AddressServiceBlockingStub client = AddressServiceGrpc.newBlockingStub(channel);

        RequestId request = RequestId.newBuilder().setId(2L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findAllByIdUser(request))
                .withMessage("INVALID_ARGUMENT: The user does not have any registered address");

    }




}
