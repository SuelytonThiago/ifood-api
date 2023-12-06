package com.example.msuser.domain.repositories;
import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.configs.ContainerBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class AddressRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    private static Address address;
    private static Users user;

    @Test
    @Order(1)
    @DisplayName("test Given Address Object WhenSave Then Return Address Object")
    void integrationTestSave(){
        user = userRepository.findByEmail("ana@example.com").get();
        address = new Address(null,"rua a","bairro a","cidade a",
                "estado a","00000000",user);
        Address savedAddress = addressRepository.save(address);

        assertThat(savedAddress).isNotNull();
        Assertions.assertThat(savedAddress.getCity()).isEqualTo(address.getCity());
        Assertions.assertThat(savedAddress.getStreet()).isEqualTo(address.getStreet());
        Assertions.assertThat(savedAddress.getCep()).isEqualTo(address.getCep());
        Assertions.assertThat(savedAddress.getState()).isEqualTo(address.getState());
        Assertions.assertThat(savedAddress.getNeighborhood()).isEqualTo(address.getNeighborhood());

        address = savedAddress;

    }

    @Test
    @Order(2)
    @DisplayName("test Given AddressId when FindById Then Return Address Object")
    public void integrationTestFindById(){

        Address outputAddress = addressRepository.findById(address.getId()).get();

        Assertions.assertThat(outputAddress).isNotNull();
        Assertions.assertThat(outputAddress.getCity()).isEqualTo(address.getCity());
        Assertions.assertThat(outputAddress.getStreet()).isEqualTo(address.getStreet());
        Assertions.assertThat(outputAddress.getCep()).isEqualTo(address.getCep());
        Assertions.assertThat(outputAddress.getState()).isEqualTo(address.getState());
        Assertions.assertThat(outputAddress.getNeighborhood()).isEqualTo(address.getNeighborhood());
    }

    @Test
    @Order(3)
    @DisplayName("test Given User Object When FindByCardUser Then Return UserAddressList")
    public void integrationTestFindByCardUser(){
        List<Address> outputAddress = addressRepository.findByAddressUser(user);

        assertThat(outputAddress.size()).isEqualTo(1);
        Assertions.assertThat(outputAddress)
                .extracting("street","state","cep","city","neighborhood")
                .contains(
                        tuple(address.getStreet(),
                                address.getState(),
                                address.getCep(),
                                address.getCity(),
                                address.getNeighborhood()));
    }

    @Test
    @Order(4)
    @DisplayName("test Given Address Object When Update Then Return Address Object Updated")
    public void integrationTestUpdate(){

        address.setCity("cidade z");
        Address updatedAddress = addressRepository.save(address);

        Assertions.assertThat(updatedAddress.getId()).isEqualTo(address.getId());
        Assertions.assertThat(updatedAddress.getCity()).isEqualTo(address.getCity());
        Assertions.assertThat(updatedAddress.getStreet()).isEqualTo(address.getStreet());
        Assertions.assertThat(updatedAddress.getCep()).isEqualTo(address.getCep());
        Assertions.assertThat(updatedAddress.getState()).isEqualTo(address.getState());
        Assertions.assertThat(updatedAddress.getNeighborhood()).isEqualTo(address.getNeighborhood());
    }

    @Test
    @Order(5)
    @DisplayName("test Given Address Object When Delete")
    void integrationTestDelete(){
        addressRepository.delete(address);
        assertThat(addressRepository.findById(address.getId()).isEmpty()).isTrue();
    }
}
