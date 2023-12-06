package com.example.msuser.rest.services;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.repositories.RoleRepository;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Roles role;

    @BeforeEach
    public void setUp(){
        role = new Roles("ROLE_USER");
    }

    @DisplayName("test Given Role Name when Find By Role Name then Return Roles Object")
    @Test
    void testGivenRoleName_whenFindByRoleName_thenReturnRolesObject(){
        given(roleRepository.findByRoleName(anyString())).willReturn(Optional.of(role));

        var roleOutput = roleService.findByRoleName(role.getRoleName());

        assertThat(roleOutput).isNotNull();
        assertThat(roleOutput).isEqualTo(role);
        verify(roleRepository).findByRoleName(anyString());
        verifyNoMoreInteractions(roleRepository);
    }

    @DisplayName("test Given Role Name when Find By Role Name then Throw Object Not Found Exception")
    @Test
    void testGivenRoleName_whenFindByRoleName_thenThrowObjectNotFoundException(){
        given(roleRepository.findByRoleName(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> roleService.findByRoleName(role.getRoleName()))
                .withMessage("The role is not found");

        verify(roleRepository).findByRoleName(anyString());
        verifyNoMoreInteractions(roleRepository);
    }



}
