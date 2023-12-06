package com.example.msuser.domain.repositories;

import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardsRepository extends JpaRepository<Cards,Long> {

    List<Cards> findByCardUser(Users users);
}
