package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.domain.repositories.StoreRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import com.example.msstore.rabbitmq.Constants;
import com.example.msstore.rest.dto.*;
import com.example.msstore.rest.services.exceptions.CustomException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StoreService {


    private final StoreRepository storeRepository;

    private final UserServiceGrpcClient userGrpcClient;

    private final RabbitMQService rabbitMQService;

    private final TokenService tokenService;

    public void createNewStore(StoreRequestDto request){
        var ownerRequestDto = new OwnerRequestDto(
                request.getOwnerName(),
                request.getEmail(),
                request.getContactNumber(),
                request.getCpf(),
                request.getPassword());
        var ownerId = userGrpcClient.createOwner(ownerRequestDto);
        try {
            var type = TypeCode.codOf(request.getTypeCode());
            var store = new Store(request, ownerId, type);
            storeRepository.save(store);
        }
        catch (DataIntegrityViolationException e){
            this.rabbitMQService.sendMessage(Constants.QUEUE_DELETE_USER,ownerId);
            throw new CustomException("the store name already in use");
        }
    }

    public List<StoreResponseDto> findAllPharmacy(){
        return storeRepository.findByType(TypeCode.PHARMACY)
                .stream().map(StoreResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<StoreResponseDto> findAllSupermarket(){
        return storeRepository.findByType(TypeCode.SUPERMARKET)
                .stream().map(StoreResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<StoreResponseDto> findAllRestaurant(){
        return storeRepository.findByType(TypeCode.RESTAURANT)
                .stream().map(StoreResponseDto::new)
                .collect(Collectors.toList());
    }

    public Store findById(Long id){
        return storeRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("The store not found"));
    }

    public List<FoodDto> showStoreMenu(Long id){
        return findById(id).getMenu().stream()
                .map(FoodDto::new)
                .collect(Collectors.toList());
    }

    public List<ReviewResponseDto> getReviews(Long id){
        return findById(id).getReviews().stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    public Store findByName(String name){
        return storeRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException("The store not found"));
    }

    public void deleteById(Long id){
        var store = findById(id);
        var ownerId = store.getOwnerId();
        storeRepository.delete(store);
        this.rabbitMQService.sendMessage(Constants.QUEUE_DELETE_USER,ownerId);
    }

    public void updateStoreData(Long idStore,
                                StoreRequestUpdate storeRequest,
                                HttpServletRequest request){
        var store = findById(idStore);
        if(verifyOwnerStore(idStore,request)){
            updateData(store,storeRequest);
            storeRepository.save(store);
            return;
        }
        throw new CustomException("You don`t have permission to edit store data");
    }

    public void save(Store store){
        storeRepository.save(store);
    }

    public boolean verifyOwnerStore(Long idStore, HttpServletRequest request){
        var idOwner = tokenService.getClaimId(request);
        var store = findById(idStore);
        var ownerStoreId = store.getOwnerId();
        return ownerStoreId == idOwner;
    }

    private void updateData(Store store, StoreRequestUpdate storeRequest) {
        if(storeRequest.getName() != null){
            store.setName(storeRequest.getName());
        }
        if(storeRequest.getBio() != null){
            store.setBio(storeRequest.getBio());
        }
    }


}
