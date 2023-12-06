package com.example.msstore.grpc.service;

import com.example.food.*;
import com.example.msstore.config.ConstantsTest;
import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.repositories.FoodRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class MyFoodServiceGrpcIntegrationTest extends ContainerBase {

    private ManagedChannel channel;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    @Autowired
    private FoodRepository foodRepository;

    @BeforeEach
    public void beforeEach(){
        channel = ManagedChannelBuilder.forAddress("localhost", ConstantsTest.SERVER_GRPC)
                .usePlaintext()
                .build();
    }

    @AfterEach
    public void afterEach(){
        channel.shutdown();
    }

    @Test
    @DisplayName("integration Test Given food RequestId When FindById Then Return FoodResponse")
    public void integrationTetFindById(){
        var food = foodRepository.findByName("white horse").get();
        assertThat(food).isNotNull();
        RequestId id = RequestId.newBuilder().setId(food.getId()).build();

        FoodServiceGrpc.FoodServiceBlockingStub client = FoodServiceGrpc.newBlockingStub(channel);
        var response = client.findById(id);

        var foodResponse = convertFoodToFoodResponse(food);

        assertThat(response.getName()).isEqualTo(foodResponse.getName());
        assertThat(response.getPrice()).isEqualTo(foodResponse.getPrice());
        assertThat(response.getQuantityStock()).isEqualTo(foodResponse.getQuantityStock());
        assertThat(response.getCategory()).isEqualTo(foodResponse.getCategory());
        assertThat(response.getStore()).isEqualTo(foodResponse.getStore());
    }

    @Test
    @DisplayName("integration Test Given With Invalid food RequestId When FindById Then Throw StatusRuntimeException")
    public void integrationTetFindByIdWithInvalidFoodId(){
        RequestId id = RequestId.newBuilder().setId(100L).build();

        FoodServiceGrpc.FoodServiceBlockingStub client = FoodServiceGrpc.newBlockingStub(channel);

        assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> client.findById(id))
                .withMessage("NOT_FOUND: The food cannot found");
    }

    private FoodResponse convertFoodToFoodResponse(Food food){
        StoreResponse store = StoreResponse.newBuilder().setId(food.getFoodStore().getId()).setName(food.getFoodStore().getName()).build();
        CategoryResponse category = CategoryResponse.newBuilder().setName(food.getCategory().getName()).build();
        return FoodResponse.newBuilder()
                .setName(food.getName())
                .setId(food.getId())
                .setPrice(food.getPrice())
                .setQuantityStock(food.getQuantityStock())
                .setCategory(category)
                .setStore(store).build();
    }

}
