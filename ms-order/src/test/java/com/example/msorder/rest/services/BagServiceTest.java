package com.example.msorder.rest.services;

import com.example.food.CategoryResponse;
import com.example.food.FoodResponse;
import com.example.food.StoreResponse;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.repositories.BagRepository;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.rest.dto.BagRequestDto;
import com.example.msorder.rest.dto.BagRequestUpdate;
import com.example.msorder.rest.dto.BagResponseDto;
import com.example.msorder.rest.services.BagService;
import com.example.msorder.rest.services.TokenService;
import com.example.msorder.rest.services.exceptions.CustomException;
import com.example.msorder.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BagServiceTest {

    @Mock
    private BagRepository bagRepository;

    @Mock
    private MyFoodGrpcClient foodGrpcClient;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private BagService bagService;
    private Bag bag;
    private BagRequestDto bagRequestDto;
    private BagRequestUpdate bagRequestUpdate;
    private FoodResponse foodResponse;
    private CategoryResponse categoryResponse;
    private StoreResponse storeResponse;


    @BeforeEach
    public void beforeEach(){
        bag = new Bag(
                1L,
                1L,
                "habbibs",
                2,
                30.00,
                1L,
                Instant.now());

        bagRequestDto = new BagRequestDto(
                1L,
                3
        );

        bagRequestUpdate = new BagRequestUpdate(2);

        categoryResponse = categoryResponse.newBuilder()
                .setName("hamburguer")
                .build();
        storeResponse = StoreResponse.newBuilder()
                .setId(1L)
                .setName("habbibs")
                .build();
        foodResponse = FoodResponse.newBuilder()
                .setCategory(categoryResponse)
                .setStore(storeResponse)
                .setName("x-tudo")
                .setPrice(18.00)
                .setQuantityStock(15)
                .setId(1L)
                .build();
    }

    @Test
    @DisplayName("test given HttpServletRequet and BagRequestDto object when addFoodToBag will save food to bag")
    public void testAddFoodToBag(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(bag.getUserId());
        given(foodGrpcClient.findById(anyLong())).willReturn(foodResponse);
        given(bagRepository.findTop1ByUserId(anyLong())).willReturn(Optional.of(bag));

        bagService.addFoodToBag(request, bagRequestDto);
        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(foodGrpcClient).findById(anyLong());
        verify(bagRepository).findTop1ByUserId(anyLong());
        verify(bagRepository).save(any(Bag.class));
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(foodGrpcClient);
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given HttpServletRequet and BagRequestDto object With Food From Different Store when addFoodToBag Then throw CustomException")
    public void testAddFoodToBagWithFoodFromDifferentStore(){
        bag.setStoreName("bobs");

        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(bag.getUserId());
        given(foodGrpcClient.findById(anyLong())).willReturn(foodResponse);
        given(bagRepository.findTop1ByUserId(anyLong())).willReturn(Optional.of(bag));

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> bagService.addFoodToBag(request,bagRequestDto))
                .withMessage("There cannot be items from different restaurants in the bag");

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(foodGrpcClient).findById(anyLong());
        verify(bagRepository).findTop1ByUserId(anyLong());
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(foodGrpcClient);
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given HttpServletRequest when getBagItems then Return BagResponseDto list")
    public void testGetBagItem(){
        var bagDto = new BagResponseDto(bag);
        HttpServletRequest request  = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(bag.getUserId());
        given(bagRepository.findByUserId(anyLong())).willReturn(Collections.singletonList(bag));

        var bagDtoList = bagService.getBagItem(request);
        assertThat(bagDtoList).extracting("foodId","quantity","price")
                .contains(
                        tuple(bagDto.getFoodId(),bagDto.getQuantity(),bagDto.getPrice())
                );

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(bagRepository).findByUserId(anyLong());
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given HttpServletRequest when getBagItems with return empty list then throw CustomException")
    public void testGetBagItemWhenReturnEmptyList(){
        var bagDto = new BagResponseDto(bag);
        HttpServletRequest request  = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(bag.getUserId());
        given(bagRepository.findByUserId(anyLong())).willReturn(Collections.emptyList());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> bagService.getBagItem(request))
                        .withMessage("the bag is empty");

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(bagRepository).findByUserId(anyLong());
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given bagId when findById then return Bag object")
    public void testFindById(){
        given(bagRepository.findById(anyLong())).willReturn(Optional.of(bag));

        var bagOutput = bagService.findById(bag.getId());

        assertThat(bagOutput).usingRecursiveComparison()
                .isEqualTo(bag);
        verify(bagRepository).findById(anyLong());
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given invalid bagId when findById then return Bag object")
    public void testFindByIdWithInvalidBagId(){
        given(bagRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bagService.findById(100L))
                        .withMessage("the bag item is not found");
    }

    @Test
    @DisplayName("test given bagId when DeleteById will delete bag from database")
    public void testDeleteById(){
        given(bagRepository.findById(anyLong())).willReturn(Optional.of(bag));

        bagService.deleteById(bag.getId());

        verify(bagRepository).findById(anyLong());
        verify(bagRepository).delete(any(Bag.class));
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given bagId and BagRequestUpdate object when updateBagItemData will update bag item quantity")
    public void testUpdateBagItemData(){
        given(bagRepository.findById(anyLong())).willReturn(Optional.of(bag));

        bagService.updateBagItemData(bag.getId(),bagRequestUpdate);

        verify(bagRepository).findById(anyLong());
        verify(bagRepository).save(any(Bag.class));
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given instant when clean bag after 10Hours will delete All bags that have been in the database for more than 10 hours")
    public void testCleanBagAfter10Hours(){
        bagService.cleanBagAfter10Hours(Instant.now().minus(10,ChronoUnit.HOURS));

        verify(bagRepository).deleteByDateBefore(any(Instant.class));
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given List items when emptyBag will delete delete all items from the provided list ")
    public void testEmptyBag(){
        bagService.emptyBag(Collections.singletonList(bag));

        verify(bagRepository).deleteAll(any());
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given userId when getBagItems when return bag list")
    public void testGetBagItems(){
        given(bagRepository.findByUserId(anyLong())).willReturn(Collections.singletonList(bag));

        var list = bagService.getBagItems(bag.getUserId());

        assertThat(list)
                .extracting("id","foodId","storeName","quantity","price","userId","date")
                .contains(
                        tuple(bag.getId(), bag.getFoodId(), bag.getStoreName(), bag.getQuantity(), bag.getPrice(), bag.getUserId(), bag.getDate()));

        verify(bagRepository).findByUserId(anyLong());
        verifyNoMoreInteractions(bagRepository);
    }

    @Test
    @DisplayName("test given invalid userId when getBagItems then throw CustomException")
    public void testGetBagItemsWithInvalidUserId(){
        given(bagRepository.findByUserId(anyLong())).willReturn(Collections.emptyList());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> bagService.getBagItems(100L))
                        .withMessage("the bag is empty");

        verify(bagRepository).findByUserId(anyLong());
        verifyNoMoreInteractions(bagRepository);
    }
}
