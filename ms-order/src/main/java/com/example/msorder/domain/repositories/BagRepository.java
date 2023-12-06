package com.example.msorder.domain.repositories;

import com.example.msorder.domain.entities.Bag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BagRepository extends JpaRepository<Bag,Long> {

    List<Bag> findByUserId(Long id);

    Optional<Bag> findTop1ByUserId(Long id);

    @Transactional
    @Modifying
    void deleteByDateBefore(Instant date);

}
