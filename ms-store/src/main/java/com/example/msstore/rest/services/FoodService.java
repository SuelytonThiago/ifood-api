package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.repositories.FoodRepository;
import com.example.msstore.rest.services.exceptions.CustomException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import com.example.msstore.rest.dto.FoodDto;
import com.example.msstore.rest.dto.FoodRequestUpdate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    private final CategoryService categoryService;

    private final StoreService storeService;

    @Transactional
    public void createNewFood(FoodDto request, Long idRestaurant, HttpServletRequest servletRequest){
        if(!storeService.verifyOwnerStore(idRestaurant,servletRequest)){
            throw new CustomException("You don`t have permission to edit store data");
        }
        var cat = categoryService.findByName(request.getCategoryName());
        var store = storeService.findById(idRestaurant);
        var food = foodRepository.save(new Food(request,cat,store));
        store.getMenu().add(food);
    }

    public Food findById(Long id){
        return foodRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("The food cannot found"));
    }

    public List<FoodDto> findByCategory(String cat){
        var category = categoryService.findByName(cat);
        return foodRepository.findByCategory(category).stream()
                .map(FoodDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id){
        var food = findById(id);
        var store = storeService.findById(food.getFoodStore().getId());
        store.getMenu().remove(food);
        storeService.save(store);
        foodRepository.delete(food);
     }

    @Transactional
    public void updateFoodData(Long id,FoodRequestUpdate request){
        var food = findById(id);
        updateData(food,request);
        foodRepository.save(food);
    }

    private void updateData(Food food, FoodRequestUpdate request) {
        if(request.getName() != null){
            food.setName(request.getName());
        }
        if(request.getPrice() != null){
            food.setPrice(request.getPrice());
        }
        if(request.getQuantityStock() != null){
            food.setQuantityStock(request.getQuantityStock());
        }
    }
}
