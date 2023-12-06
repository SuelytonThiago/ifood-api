package com.example.msuser.config;

import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.domain.repositories.AddressRepository;
import com.example.msuser.domain.repositories.CardsRepository;
import com.example.msuser.domain.repositories.RoleRepository;
import com.example.msuser.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class EnterDataTest implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        Users users = new Users(
                null,
                "adm",
                "adm@example.com",
                "99940028922",
                "61254591010",
                encoder.encode("senha123"));
        userRepository.save(users);

        Users users1 = new Users(
                null,
                "ana",
                "ana@example.com",
                "99940028922",
                "87466407030",
                encoder.encode("senha123"));
        userRepository.save(users1);


        Roles roles = new Roles("ROLE_ADMIN");
        Roles roles1 = new Roles("ROLE_USER");
        Roles roles2 = new Roles("ROLE_OWNER");
        roleRepository.saveAll(Arrays.asList(roles,roles1,roles2));

        users.getRoles().add(roles);
        users1.getRoles().add(roles1);
        userRepository.saveAll(Arrays.asList(users,users1));

        Cards cards= new Cards(
                null,"myCard","1234567891234567",
                "12/2028","266",users, TypeCard.CREDIT_CARD);
        cardsRepository.save(cards);

        users.getPaymentMethods().add(cards);
        userRepository.save(users);

        Address address = new Address(null,"rua a","bairro a","cidade a","estado a","00000000",users);
        addressRepository.save(address);

        users.getAddresses().add(address);
        userRepository.save(users);
    }
}
