package com.example.msorder.domain.repositories;

import com.example.msorder.config.ContainerBase;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.grpc.client.MyAddressGrpcClient;
import com.example.msorder.grpc.client.MyCardGrpcClient;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.grpc.client.MyUserGrpcClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class BagRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private BagRepository bagRepository;

    @MockBean
    private MyAddressGrpcClient myAddressGrpcClient;
    @MockBean
    private MyCardGrpcClient myCardGrpcClient;
    @MockBean
    private MyFoodGrpcClient myFoodGrpcClient;
    @MockBean
    private MyUserGrpcClient myUserGrpcClient;

    private static Bag bag;

    @Test
    @Order(1)
    @DisplayName("test given bag object when sav then return bag object")
    public void integrationTestSave(){
        var imputBag = new Bag(
                null,
                2L,
                "macDonald",
                1,
                15.00,
                10L,
                Instant.now());
        var savedBag = bagRepository.save(imputBag);

        assertThat(savedBag.getId()).isNotNull();
        assertThat(savedBag)
                .usingRecursiveComparison()
                .comparingOnlyFields("foodId","storeName","quantity","price","userId","date")
                .isEqualTo(imputBag);

        bag = savedBag;
    }

    @Test
    @Order(2)
    @DisplayName("test given bagId when FindById thenreturn bag object")
    public void integrationTestFindById(){
        var outputBag = bagRepository.findById(bag.getId()).get();

        assertThat(outputBag).isNotNull();
        assertThat(outputBag)
                .usingRecursiveComparison()
                .comparingOnlyFields("foodId","storeName","quantity","price","userId")
                .isEqualTo(bag);
    }

    @Test
    @Order(3)
    @DisplayName("test given userId when findByUserId then return BagList")
    public void integrationTestFindByUserId(){
        var bagList = bagRepository.findByUserId(bag.getUserId());

        assertThat(bagList).extracting("foodId","storeName","quantity","price","userId")
                .contains(
                        tuple(bag.getFoodId(),bag.getStoreName(),bag.getQuantity(),bag.getPrice(),bag.getUserId())
                );
    }

    @Test
    @Order(4)
    @DisplayName("test given userId when findTop1ByUserId then return bag object")
    public void integrationTestFindByTop1ByUserId(){
        var bagOutput = bagRepository.findTop1ByUserId(bag.getUserId());

        assertThat(bagOutput)
                .usingRecursiveComparison()
                .comparingOnlyFields("foodId","storeName","quantity","price","userId","date")
                .isEqualTo(bag);
    }

    @Test
    @Order(5)
    @Transactional
    @DisplayName("test given date when deleteByDateBefore will delete all bags that were saved more than 10 hours ago")
    public void integrationTestDeleteByDateBefore(){
        var date  = Instant.now().minus(11, ChronoUnit.HOURS);
        var imputBag = new Bag(
                null, 3L, "macDonald",
                1, 100.00, 20L, date);
        var newBag = bagRepository.save(imputBag);

        bagRepository.deleteByDateBefore(Instant.now().minus(10,ChronoUnit.HOURS));

        assertThat(bagRepository.findById(newBag.getId()).isEmpty()).isTrue();
    }

    @Test
    @Order(6)
    @DisplayName("test given bag object when delete will delete thisbag from databse")
    public void integrationTestDelete(){
        bagRepository.delete(bag);

        assertThat(bagRepository.findById(bag.getId()).isEmpty()).isTrue();
    }


}
