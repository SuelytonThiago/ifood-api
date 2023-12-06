package com.example.msuser.domain.repositories;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.configs.ContainerBase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class CardRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private UserRepository userRepository;
    private static Cards card;
    private static Users user;

    @Test
    @Order(1)
    @DisplayName("test Given Cards Object WhenSave Then Return Cards Object")
    void integrationTestSave(){
        user = userRepository.findByEmail("ana@example.com").get();
        card =  new Cards(null,"myCard","1234567891234567",
                "12/2028","266",user, TypeCard.CREDIT_CARD);
        Cards savedCard = cardsRepository.save(card);

        assertThat(savedCard.getId()).isNotNull();
        assertThat(savedCard.getCvv()).isEqualTo(card.getCvv());
        assertThat(savedCard.getName()).isEqualTo(card.getName());
        assertThat(savedCard.getExpiration()).isEqualTo(card.getExpiration());
        assertThat(savedCard.getNumber()).isEqualTo(card.getNumber());
        assertThat(savedCard.getType()).isEqualTo(card.getType());

        card = savedCard;
    }

    @Test
    @Order(2)
    @DisplayName("test Given CardsId when FindById Then Return Cards Object")
    public void integrationTestFindById(){
        Cards outputCard = cardsRepository.findById(card.getId()).get();

        assertThat(outputCard).isNotNull();
        assertThat(outputCard.getCvv()).isEqualTo(card.getCvv());
        assertThat(outputCard.getName()).isEqualTo(card.getName());
        assertThat(outputCard.getExpiration()).isEqualTo(card.getExpiration());
        assertThat(outputCard.getNumber()).isEqualTo(card.getNumber());
        assertThat(outputCard.getType()).isEqualTo(card.getType());
    }


    @Test
    @Order(3)
    @DisplayName("test Given User Object When FindByCardUser Then Return UserCardList")
    public void integrationTestFindByCardUser(){
        List<Cards> outputCard = cardsRepository.findByCardUser(user);

        assertThat(outputCard.size()).isEqualTo(1);
        assertThat(outputCard)
                .extracting("name","number","cvv","expiration","type")
                .contains(
                        tuple(card.getName(),
                                card.getNumber(),
                                card.getCvv(),
                                card.getExpiration(),
                                card.getType()));
    }


    @Test
    @Order(4)
    @DisplayName("test Given Card Object When Update Then Return Card Object Updated")
    public void integrationTestUpdate(){
        card.setName("MyNewCard");
        Cards updatedCard = cardsRepository.save(card);

        assertThat(updatedCard.getId()).isEqualTo(card.getId());
        assertThat(updatedCard.getCvv()).isEqualTo(card.getCvv());
        assertThat(updatedCard.getName()).isEqualTo(card.getName());
        assertThat(updatedCard.getExpiration()).isEqualTo(card.getExpiration());
        assertThat(updatedCard.getNumber()).isEqualTo(card.getNumber());
        assertThat(updatedCard.getType()).isEqualTo(card.getType());

    }


    @Test
    @Order(5)
    @DisplayName("test Given Cards Object When Delete")
    void integrationTestDelete(){
        Cards savedCard = cardsRepository.save(card);
        cardsRepository.delete(savedCard);
        assertThat(cardsRepository.findById(savedCard.getId()).isEmpty()).isTrue();
    }
}
