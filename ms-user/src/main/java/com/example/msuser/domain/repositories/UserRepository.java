package com.example.msuser.domain.repositories;

import com.example.msuser.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findFirstByEmailOrCpf(String email, String cpf);

}
