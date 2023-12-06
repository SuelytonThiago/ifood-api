package com.example.msstore.domain.repositories;

import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {

    List<Store> findByType(TypeCode typeCode);
    Optional<Store> findByName(String name);
}
