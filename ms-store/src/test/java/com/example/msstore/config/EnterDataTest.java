package com.example.msstore.config;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.domain.repositories.CategoryRepository;
import com.example.msstore.domain.repositories.FoodRepository;
import com.example.msstore.domain.repositories.ReviewRepository;
import com.example.msstore.domain.repositories.StoreRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@Configuration
@ActiveProfiles("test")
public class EnterDataTest implements CommandLineRunner {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void run(String... args) throws Exception {
        Categories category = new Categories(null,"bebidas");
        categoryRepository.save(category);

        Store store = new Store(null,"habbibs","vendemos pizza",1L, TypeCode.RESTAURANT);
        storeRepository.save(store);

        Food food = new Food(null,"white horse",30.00,10,category,store);
        foodRepository.save(food);

        Review review = new Review(null,1L,5,"muito bom", LocalDate.now(), store);
        reviewRepository.save(review);

        store.getReviews().add(review);
        storeRepository.save(store);

        store.getMenu().add(food);
        storeRepository.save(store);
    }
}
