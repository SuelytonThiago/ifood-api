package com.example.msstore.domain.repositories;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food,Long> {

    List<Food> findByCategory(Categories categories);
    Optional<Food> findByName(String name);
}
