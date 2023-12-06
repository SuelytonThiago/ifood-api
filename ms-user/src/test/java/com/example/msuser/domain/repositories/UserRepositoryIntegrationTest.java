package com.example.msuser.domain.repositories;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.configs.ContainerBase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private UserRepository userRepository;
    private static Users user;

    @Test
    @Order(1)
    @DisplayName("test Given User Object WhenSave Then Return User Object")
    void integrationTestSave(){
        user = new Users(null, "zeze", "zeze@example.com",
                "99940028922", "81426049064", "senha123");
        Users savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(savedUser.getCpf()).isEqualTo(user.getCpf());
        assertThat(savedUser.getContactNumber()).isEqualTo(user.getContactNumber());

        user = savedUser;
    }


    @Test
    @Order(2)
    @DisplayName("test Given User Id When Find By Id then Return User Object")
    void integrationTestFindById(){
        Users outputUser = userRepository.findById(user.getId()).get();

        assertThat(outputUser).isNotNull();
        assertThat(outputUser.getName()).isEqualTo(user.getName());
        assertThat(outputUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(outputUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(outputUser.getCpf()).isEqualTo(user.getCpf());
        assertThat(outputUser.getContactNumber()).isEqualTo(user.getContactNumber());
    }

    @Test
    @Order(3)
    @DisplayName("test Given User Email When Find By Email then Return User Object")
    void integrationTestFindByEmail(){
        Users outputUser = userRepository.findByEmail(user.getEmail()).get();

        assertThat(outputUser).isNotNull();
        assertThat(outputUser.getName()).isEqualTo(user.getName());
        assertThat(outputUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(outputUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(outputUser.getCpf()).isEqualTo(user.getCpf());
        assertThat(outputUser.getContactNumber()).isEqualTo(user.getContactNumber());
    }


    @Test
    @Order(4)
    @DisplayName("test Given User Email And Cpf When Find By Email Or Cpf then Return User Object")
    void integrationTestFindFirstByEmailOrCpf(){
        Users outputUser = userRepository.findFirstByEmailOrCpf(user.getEmail(),user.getCpf()).get();

        assertThat(outputUser).isNotNull();
        assertThat(outputUser.getName()).isEqualTo(user.getName());
        assertThat(outputUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(outputUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(outputUser.getCpf()).isEqualTo(user.getCpf());
        assertThat(outputUser.getContactNumber()).isEqualTo(user.getContactNumber());
    }


    @Test
    @Order(5)
    @DisplayName("test Given Only User Email When FindByEmailOrCpf then Return User Object")
    void integrationTestFindFirstByEmailOrCpfWithOnlyEmail(){
        Users outputUser = userRepository.findFirstByEmailOrCpf(user.getEmail(), "asdsadasdasd").get();

        assertThat(outputUser).isNotNull();
        assertThat(outputUser.getName()).isEqualTo(user.getName());
        assertThat(outputUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(outputUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(outputUser.getCpf()).isEqualTo(user.getCpf());
        assertThat(outputUser.getContactNumber()).isEqualTo(user.getContactNumber());
    }



    @Test
    @Order(6)
    @DisplayName("test Given Only User Cpf When Find By Email Or Cpf then Return User Object")
    void integrationTestFindFirstByEmailOrCpfWithOnlyCpf(){
        Users outputUser = userRepository.findFirstByEmailOrCpf("ASdfasdadfdfa", user.getCpf()).get();

        assertThat(outputUser).isNotNull();
        assertThat(outputUser.getName()).isEqualTo(user.getName());
        assertThat(outputUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(outputUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(outputUser.getCpf()).isEqualTo(user.getCpf());
        assertThat(outputUser.getContactNumber()).isEqualTo(user.getContactNumber());
    }


    @Test
    @Order(7)
    @DisplayName("test Given User Object WhenDelete")
    void integrationTestDelete(){
        userRepository.delete(user);
        assertThat(userRepository.findById(user.getId()).isEmpty()).isTrue();
    }
}
