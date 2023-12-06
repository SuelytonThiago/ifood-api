package com.example.msuser.domain.repositories;

import com.example.msuser.domain.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles,Long> {

    Optional<Roles> findByRoleName(String role);
}
