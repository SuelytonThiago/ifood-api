package com.example.msuser.domain.repositories;

import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByAddressUser(Users user);
}
