package com.example.msstore.domain.repositories;

import com.example.msstore.domain.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Categories,Long> {

    Optional<Categories>findByName(String name);
}
