package com.example.msuser.grpc;

import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import com.example.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class MyGrpcUserServiceIntegrationTest extends ContainerBase {

    private ManagedChannel channel;
    private static OwnerIdResponse idResponse;


    @BeforeEach
    public void setUp() {
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
    @DisplayName("test Given OwnerObject when CreateOwner then Return OwnerId ")
    public void integrationTestCreateOwner(){
        OwnerRequest request = OwnerRequest.newBuilder()
                .setCpf("15853557041")
                .setContactNumber("99940028922")
                .setName("luiza")
                .setEmail("luiza@example.com")
                .setPassword("senha123")
                .build();
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        OwnerIdResponse response = client.createOwner(request);

        assertThat(response.getId()).isNotNull();
        idResponse = response;
    }

    @Test
    @Order(2)
    @DisplayName("test Given with email already exists when CreateOwner then Throw Exception ")
    public void integrationTestCreateOwnerEmailAlreadyExists(){
        OwnerRequest request = OwnerRequest.newBuilder()
                .setCpf("15853557041")
                .setContactNumber("61254591010")
                .setName("luiza")
                .setEmail("luiza@example.com")
                .setPassword("senha123")
                .build();
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.createOwner(request))
                .withMessage("ALREADY_EXISTS: This email or cpf already in use");
    }

    @Test
    @Order(3)
    @DisplayName("test Given with cpf already exists when CreateOwner then Throw Exception ")
    public void integrationTestCreateOwnerCpfAlreadyExists(){
        OwnerRequest request = OwnerRequest.newBuilder()
                .setCpf("61254591010")
                .setContactNumber("61254591010")
                .setName("luiza")
                .setEmail("luiza@example.com")
                .setPassword("senha123")
                .build();
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.createOwner(request))
                .withMessage("ALREADY_EXISTS: This email or cpf already in use");
    }


    @Test
    @Order(4)
    @DisplayName("test Given RequestId when FindById then Return User Response")
    public void integrationTestFindById(){
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        RequestId id = RequestId.newBuilder().setId(idResponse.getId()).build();

        UserResponse response = client.findById(id);

        assertThat( response.getId()).isEqualTo(idResponse.getId());
        assertThat( response.getName()).isEqualTo("luiza");
        assertThat( response.getEmail()).isEqualTo("luiza@example.com");
        assertThat( response.getContactNumber()).isEqualTo("99940028922");
        assertThat( response.getCpf()).isEqualTo("15853557041");
    }

    @Test
    @Order(5)
    @DisplayName("test Given Invalid RequestId when FindById then Throw ObjectNotFoundException")
    void integrationTestFindByIdNotFound() {
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        RequestId id = RequestId.newBuilder().setId(5L).build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findById(id))
                .withMessage("NOT_FOUND: The user is not found");
    }

    @Test
    @Order(6)
    @DisplayName("test Given UserEmail when FindByEmail then Return User Response")
    public void integrationTestFindByEmail(){
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        RequestEmail email = RequestEmail.newBuilder().setEmail("luiza@example.com").build();

        UserResponse response = client.findByEmail(email);

        assertThat( response.getId()).isEqualTo(idResponse.getId());
        assertThat( response.getName()).isEqualTo("luiza");
        assertThat( response.getEmail()).isEqualTo("luiza@example.com");
        assertThat( response.getContactNumber()).isEqualTo("99940028922");
        assertThat( response.getCpf()).isEqualTo("15853557041");
    }

    @Test
    @Order(7)
    @DisplayName("test Given Invalid UserEmail when FindByEmail then Throw ObjectNotFoundException")
    void integrationTestFindByEmailNotFound() {
        UserServiceGrpc.UserServiceBlockingStub client = UserServiceGrpc.newBlockingStub(channel);

        RequestEmail email = RequestEmail.newBuilder().setEmail("xyz@example.com").build();

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findByEmail(email))
                .withMessage("NOT_FOUND: The user is not found");
    }
}
