package com.example.msorder.domain.repositories;

import com.example.msorder.domain.entities.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository<OrderItems,Long> {

}
