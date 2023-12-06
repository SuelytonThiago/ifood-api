package com.example.msuser.domain.repositories;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.configs.ContainerBase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class RoleRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private RoleRepository roleRepository;
    private static Roles role;


    @Test
    @Order(1)
    @DisplayName("test Given Role Object when Save then Return Role Object")
    void integrationTestSave(){
        role = new Roles(null,"ROLE_TEST");
        Roles savedRole = roleRepository.save(role);

        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getRoleName()).isEqualTo(role.getRoleName());
        role = savedRole;
    }

    @Test
    @Order(2)
    @DisplayName("test Given Role Name when Find By Role Name then Return Role Object")
    void integrationTestFindByRoleName(){
        Roles roleOutput = roleRepository.findByRoleName(role.getRoleName()).get();

        assertThat(roleOutput.getId()).isNotNull();
        assertThat(roleOutput.getRoleName()).isEqualTo(role.getRoleName());
    }

    @Test
    @DisplayName("test Given Role Object when Delete")
    void integrationTestDelete(){
        roleRepository.delete(role);
        assertThat(roleRepository.findById(role.getId()).isEmpty()).isTrue();
    }
}
