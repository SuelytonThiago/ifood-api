package com.example.msorder.rest.services;

import com.example.msorder.rest.dto.BagRequestDto;
import com.example.msorder.rest.dto.BagRequestUpdate;
import com.example.msorder.rest.dto.BagResponseDto;
import com.example.msorder.rest.services.exceptions.CustomException;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.domain.repositories.BagRepository;
import com.example.msorder.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BagService {

    private final BagRepository bagRepository;

    private final MyFoodGrpcClient foodGrpcClient;

    private final TokenService tokenService;

    @Transactional
    public void addFoodToBag(HttpServletRequest request, BagRequestDto dto){
        var userId = tokenService.getClaimId(request);
        var food = foodGrpcClient.findById(dto.getFoodId());
        var bag = bagRepository.findTop1ByUserId(userId);
        if(bag.isPresent() && !food.getStore().getName().equals(bag.get().getStoreName())){
            throw new CustomException("There cannot be items from different restaurants in the bag");
        }
        bagRepository.save(new Bag(food.getId(), dto.getQuantity(), food.getPrice(), userId, food.getStore().getName()));
    }
    @Transactional
    public List<BagResponseDto> getBagItem(HttpServletRequest request){
        var userId = tokenService.getClaimId(request);
        var items =  bagRepository.findByUserId(userId).stream()
                .map(BagResponseDto::new)
                .collect(Collectors.toList());
        if(items.isEmpty()){
            throw new CustomException("the bag is empty");
        }
        return items;
    }

    public Bag findById(Long id){
        return bagRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("the bag item is not found"));
    }
    @Transactional
    public void deleteById(Long id){
        var bag = findById(id);
        bagRepository.delete(bag);
    }

    @Transactional
    public void updateBagItemData(Long id, BagRequestUpdate bagRequestUpdate){
        var bag = findById(id);
        updateData(bag,bagRequestUpdate);
        bagRepository.save(bag);
    }

    public void cleanBagAfter10Hours(Instant date){
        bagRepository.deleteByDateBefore(date);
    }

    public void emptyBag(List<Bag> item){
        bagRepository.deleteAll(item);
    }

    private void updateData(Bag bag, BagRequestUpdate bagRequestUpdate) {
        bag.setQuantity(bagRequestUpdate.getQuantity());
    }

    public List<Bag> getBagItems(Long userId){
        var items =  bagRepository.findByUserId(userId);
        if(items.isEmpty()){
            throw new CustomException("the bag is empty");
        }
        return items;
    }
}
