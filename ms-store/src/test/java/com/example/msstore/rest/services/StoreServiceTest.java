package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.domain.repositories.StoreRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import com.example.msstore.rest.dto.OwnerRequestDto;
import com.example.msstore.rest.dto.StoreRequestDto;
import com.example.msstore.rest.dto.StoreRequestUpdate;
import com.example.msstore.rest.services.exceptions.CustomException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserServiceGrpcClient grpcClient;

    @Mock
    private RabbitMQService rabbitMQService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private StoreService storeService;

    private Food food;
    private Categories category;
    private Review reviewP,reviewS,reviewR;
    private Store restaurant,pharmacy,supermarket;
    private StoreRequestDto storeRequestDto;
    private StoreRequestUpdate storeRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        restaurant = new Store(1L,"pizzaria","vendemos pizza",2L, TypeCode.RESTAURANT);
        pharmacy = new Store(2L,"farmacia","bio far",3L, TypeCode.PHARMACY);
        supermarket = new Store(3L,"mercado","bio mer",4L, TypeCode.SUPERMARKET);

        category = new Categories(1L,"pizza");
        food = new Food(1L,"pizza",30.00,10,category,restaurant);

        reviewR = new Review(1L,1L,5,"muito bom", LocalDate.now(), restaurant);
        reviewS = new Review(1L,1L,5,"muito bom", LocalDate.now(), supermarket);
        reviewP = new Review(1L,1L,5,"muito bom", LocalDate.now(), pharmacy);

        restaurant.getReviews().add(reviewR);
        pharmacy.getReviews().add(reviewP);
        supermarket.getReviews().add(reviewS);

        restaurant.getMenu().add(food);

        storeRequestDto = new StoreRequestDto(
                "habbibs",
                "bio",
                3,
                "jose",
                "jose@example.com",
                "99940028922",
                "97980163001",
                "senha123");
        storeRequestUpdate = new StoreRequestUpdate("habbibs","new bio");
    }

    @Test
    @DisplayName("Test Given StoreRequest Object when CreateNewOwner Will Save Owner")
    public void testCreateNewOwner(){
        given(grpcClient.createOwner(any(OwnerRequestDto.class))).willReturn(1L);

        storeService.createNewStore(storeRequestDto);
        verify(grpcClient).createOwner(any(OwnerRequestDto.class));
        verify(storeRepository).save(any(Store.class));
        verifyNoMoreInteractions(grpcClient);
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("Test Given StoreRequest With Store Name Already In Use Object when CreateNewOwner Then Throw CustomException")
    public void testCreateNewOwnerWithStoreNameAlreadyInUse(){
        given(grpcClient.createOwner(any(OwnerRequestDto.class))).willReturn(1L);
        given(storeRepository.save(any(Store.class))).willThrow(DataIntegrityViolationException.class);

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() ->  storeService.createNewStore(storeRequestDto))
                        .withMessage("the store name already in use");

        verify(grpcClient).createOwner(any(OwnerRequestDto.class));
        verify(storeRepository).save(any(Store.class));
        verify(rabbitMQService).sendMessage(anyString(),anyLong());
        verifyNoMoreInteractions(grpcClient);
        verifyNoMoreInteractions(storeRepository);
        verifyNoMoreInteractions(rabbitMQService);
    }

    @Test
    @DisplayName("test when findAllPharmacy then return PharmacyList")
    public void testFindAllPharmacy(){
        given(storeRepository.findByType(any(TypeCode.class))).willReturn(Collections.singletonList(pharmacy));

        var list = storeService.findAllPharmacy();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).extracting("name","bio","totalRating")
                .contains(
                        tuple(pharmacy.getName(),
                                pharmacy.getBio(),
                                String.format("%.1f",pharmacy.getAverageRating())));
        verify(storeRepository).findByType(any(TypeCode.class));
        verifyNoMoreInteractions(storeRepository);

    }

    @Test
    @DisplayName("test when findAllSupermarket then return SupermarketList")
    public void testFindAllSupermarket(){
        given(storeRepository.findByType(any(TypeCode.class))).willReturn(Collections.singletonList(supermarket));

        var list = storeService.findAllSupermarket();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).extracting("name","bio","totalRating")
                .contains(
                        tuple(supermarket.getName(),
                                supermarket.getBio(),
                                String.format("%.1f",supermarket.getAverageRating())));
        verify(storeRepository).findByType(any(TypeCode.class));
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test when findAllRestaurant then return RestaurantList")
    public void testFindAllRestaurant(){
        given(storeRepository.findByType(any(TypeCode.class))).willReturn(Collections.singletonList(restaurant));

        var list = storeService.findAllSupermarket();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).extracting("name","bio","totalRating")
                .contains(
                        tuple(restaurant.getName(),
                                restaurant.getBio(),
                                String.format("%.1f",restaurant.getAverageRating())));
        verify(storeRepository).findByType(any(TypeCode.class));
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test Given StoreId When FindById Then Return Store Object")
    public void testFindById(){
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        var outputStore = storeService.findById(restaurant.getId());
        assertThat(outputStore).usingRecursiveComparison().isEqualTo(restaurant);
        verify(storeRepository).findById(anyLong());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test Given With Invalid StoreId When FindById Then Throw ObjectNotFoundException")
    public void testFindByIdWithInvalidId(){
        given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> storeService.findById(10L))
                .withMessage("The store not found");
        verify(storeRepository).findById(anyLong());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test Given StoreId When ShowStoreMenu Then Return FoodStoreList")
    public void testShowStoreMenu(){
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        var list = storeService.showStoreMenu(restaurant.getId());

        assertThat(list).extracting("name","price","quantityStock","categoryName")
                .contains(
                        tuple(food.getName(),food.getPrice(),food.getQuantityStock(),food.getCategory().getName()));
        verify(storeRepository).findById(anyLong());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test given StoreId When GetReviews Then Return ReviewsStoreList")
    public void testGetReviews(){
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        var list = storeService.getReviews(restaurant.getId());
        assertThat(list.size()).isEqualTo(1);
        assertThat(list).extracting("rating","comment","date")
                .contains(
                        tuple(reviewR.getRating(),reviewR.getComment(),reviewR.getDate().toString()));
        verify(storeRepository).findById(anyLong());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test given StoreName When FindByName Then Return StoreObject")
    public void testFindByName(){
        given(storeRepository.findByName(anyString())).willReturn(Optional.of(restaurant));

        var storeOutput = storeService.findByName(restaurant.getName());

        assertThat(storeOutput).usingRecursiveComparison().isEqualTo(restaurant);
        verify(storeRepository).findByName(anyString());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test given With Invalid Store Name When FindByName Then Return StoreObject")
    public void testFindByNameWithInvalidStoreName(){
        given(storeRepository.findByName(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> storeService.findByName(restaurant.getName()))
                        .withMessage("The store not found");
        verify(storeRepository).findByName(anyString());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test Given StoreId When DeleteById Will Delete Store")
    public void testDeleteById(){
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(restaurant));
        storeService.deleteById(restaurant.getId());

        verify(storeRepository).findById(anyLong());
        verify(storeRepository).delete(any(Store.class));
        verify(rabbitMQService).sendMessage(anyString(),anyLong());
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    @DisplayName("test given StoreId, HttpServletRequest and StoreRequest Object When UpdateStoreData Will update store")
    public void testUpdateStoreData(){
        HttpServletRequest request =mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(restaurant.getOwnerId());
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        storeService.updateStoreData(restaurant.getId(),storeRequestUpdate,request);

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(storeRepository,times(2)).findById(anyLong());
        verify(storeRepository).save(any(Store.class));
        verifyNoMoreInteractions(storeRepository);
        verifyNoMoreInteractions(tokenService);
    }

    @Test
    @DisplayName("test given StoreId, HttpServletRequest and StoreRequest Object With Invalid Store OwnerId When UpdateStoreData Then Throw CustomException")
    public void testUpdateStoreDataWithInvalidStoreOwnerId(){
        HttpServletRequest request =mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(pharmacy.getOwnerId());
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> storeService.updateStoreData(restaurant.getId(),storeRequestUpdate,request))
                        .withMessage("You don`t have permission to edit store data");

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(storeRepository,times(2)).findById(anyLong());
        verifyNoMoreInteractions(storeRepository);
        verifyNoMoreInteractions(tokenService);
    }
}
