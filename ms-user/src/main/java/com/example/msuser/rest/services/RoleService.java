package com.example.msuser.rest.services;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.repositories.RoleRepository;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {


    private final RoleRepository roleRepository;

    public Roles findByRoleName(String name){
        return roleRepository.findByRoleName(name)
                .orElseThrow(() -> new ObjectNotFoundException("The role is not found"));
    }
}
