package com.example.msorder.domain.repositories;

import com.example.msorder.domain.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByUserId(Long id);
}
