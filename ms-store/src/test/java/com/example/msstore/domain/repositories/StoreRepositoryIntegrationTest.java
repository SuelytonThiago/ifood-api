package com.example.msstore.domain.repositories;

import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class StoreRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private StoreRepository storeRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    private static Store store;

    @Test
    @Order(1)
    @DisplayName("integration test given store object when save then return store object")
    public void testSave(){
        var savedStore = storeRepository.save(new Store(null,"mac","vendemos hamburguers",2L, TypeCode.RESTAURANT));

        assertThat(savedStore.getId()).isNotNull();
        assertThat(savedStore.getName()).isEqualTo("mac");
        assertThat(savedStore.getBio()).isEqualTo("vendemos hamburguers");
        assertThat(savedStore.getOwnerId()).isEqualTo(2L);
        assertThat(savedStore.getType()).isEqualTo(TypeCode.RESTAURANT);
        store = savedStore;
    }

    @Test
    @Order(2)
    @DisplayName("integration test given store Type when findByType then return store object")
    public void testFindByType(){
        var outputStore = storeRepository.findByType(TypeCode.RESTAURANT);

        assertThat(outputStore).usingRecursiveComparison()
                .comparingOnlyFields("name","bio","ownerId","type")
                .isEqualTo(store);
    }

    @Test
    @Order(3)
    @DisplayName("integration test given store name when findByName then return store object")
    public void testFindByName(){
        var outputStore = storeRepository.findByName(store.getName());

        assertThat(outputStore).usingRecursiveComparison()
                .comparingOnlyFields("name","bio","ownerId","type")
                .isEqualTo(store);
    }

    @Test
    @Order(4)
    @DisplayName("integration test when findAll then return store list")
    public void testFindAll(){
        var list = storeRepository.findAll();

        assertThat(list).extracting("name","bio","ownerId","type")
                .contains(
                        tuple(store.getName(),store.getBio(),store.getOwnerId(),store.getType()));
    }

    @Test
    @Order(5)
    @DisplayName("integration test given store object when delete will delete store from database")
    public void testDelete(){
        storeRepository.delete(store);
        assertThat(storeRepository.findById(store.getId()).isEmpty()).isTrue();
    }
}
