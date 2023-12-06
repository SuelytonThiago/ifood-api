package com.example.msuser.rest.services;

import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.domain.repositories.CardsRepository;
import com.example.msuser.rest.dto.CardRequestDto;
import com.example.msuser.rest.dto.CardRequestUpdate;
import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardsRepository cardsRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private CardRequestDto cardRequestDto;
    private CardRequestUpdate cardRequestUpdate;
    private Users user;
    private Cards card;

    @BeforeEach
    public void beforeEach(){
        user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("99940028922");
        user.setCpf("05661795041");
        user.setPassword("senha123");
        user.setAddresses(new ArrayList<>());
        user.setRoles(new ArrayList<>());

        card = new Cards(
                1L,
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266",
                user,
                TypeCard.CREDIT_CARD);


        cardRequestDto = new CardRequestDto(
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266",
                1);

        cardRequestUpdate = new CardRequestUpdate(
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266",
                1);
    }

    @Test
    @DisplayName("test Given CardRequestDto Object When CreateNewCard Then")
    public void testCreteNewCard(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);
        given(cardsRepository.save(any(Cards.class))).willReturn(card);

        cardService.createNewCard(request,cardRequestDto);

        verify(userService).getUserAuthenticate(any(HttpServletRequest.class));
        verify(cardsRepository).save(any(Cards.class));
        verify(userService).save(any(Users.class));
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(cardsRepository);
    }

    @Test
    @DisplayName("test Given CardId When FindById Then Return Card Object")
    public void testFindById(){
        given(cardsRepository.findById(anyLong())).willReturn(Optional.of(card));

        var cardOutput = cardService.findById(card.getId());

        assertThat(cardOutput).usingRecursiveComparison()
                .comparingOnlyFields("name","number","expiration","cvv","type")
                .isEqualTo(card);

        verify(cardsRepository).findById(anyLong());
        verifyNoMoreInteractions(cardsRepository);
    }

    @Test
    @DisplayName("test Given With Invalid CardId When FindById Then Throw ObjectNotFound")
    public void testFindByIdWithInvalidCardId(){
        given(cardsRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> cardService.findById(5L))
                .withMessage("The card is not found");
    }

    @Test
    @DisplayName("test Given UserId Then FindAllCardsByUser Then Return UserCardsList")
    public void testFindAllCardsByUser(){
        user.getPaymentMethods().add(card);
        given(userService.findById(anyLong())).willReturn(user);
        given(cardsRepository.findByCardUser(any(Users.class))).willReturn(Collections.singletonList(card));

        var list = cardService.findAllCardsByUser(user.getId());

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).extracting("name","number","expiration","cvv","type")
                .contains(
                        tuple(card.getName(),card.getNumber(),card.getExpiration(),card.getCvv(),card.getType())
                );
        verify(userService).findById(anyLong());
        verify(cardsRepository).findByCardUser(any(Users.class));
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(cardsRepository);
    }

    @Test
    @DisplayName("test Given HttpServletRequest When FindAllPaymentMethods Then Return UserCardList")
    public void testFindAllPaymentMethods(){
        Cards cards = new Cards(
                1L,
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266",
                user,
                TypeCard.CREDIT_CARD);
        user.getPaymentMethods().add(cards);

        HttpServletRequest request = mock(HttpServletRequest.class);
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);

        var list = cardService.findAllPaymentMethod(request);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list)
                .extracting("name","number","expiration","cvv","type")
                .contains(
                        tuple("MyCARD","1234567891234567","12/2028","266","CREDIT_CARD")
                );
        verify(userService).getUserAuthenticate(any(HttpServletRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("test Given HttpServletRequest When FindAllPaymentMethods Then Throw CustomException")
    public void testFindAllPaymentMethodsWithEmptyUserCardsList(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> cardService.findAllPaymentMethod(request))
                .withMessage("The user does not have any registered payment methods");
    }

    @Test
    @DisplayName("test Given UserId Then FindAllCardsByUser Then Throw CustomException")
    public void testFindAllCardsByUserWithEmptyUserCardsList(){
        user.getPaymentMethods().add(card);
        given(userService.findById(anyLong())).willReturn(user);
        given(cardsRepository.findByCardUser(any(Users.class))).willReturn(Collections.emptyList());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> cardService.findAllCardsByUser(user.getId()))
                .withMessage("The user does not have any registered payment methods");

        verify(userService).findById(anyLong());
        verify(cardsRepository).findByCardUser(any(Users.class));
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(cardsRepository);
    }

    @Test
    @DisplayName("test Given CardId When DeleteById")
    public void testDeleteById(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(cardsRepository.findById(anyLong())).willReturn(Optional.of(card));
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);

        cardService.deleteById(card.getId(),request);

        verify(cardsRepository).findById(anyLong());
        verify(cardsRepository).delete(any(Cards.class));
        verify(userService).getUserAuthenticate(any(HttpServletRequest.class));
        verify(userService).save(any(Users.class));
        verifyNoMoreInteractions(cardsRepository);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("test Given Invalid CardId When DeleteById Then Return ObjectNotFound")
    public void testDeleteByIdWithInvalidCardId(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(cardsRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> cardService.deleteById(5L,request))
                        .withMessage("The card is not found");

        verify(cardsRepository).findById(anyLong());
        verifyNoMoreInteractions(cardsRepository);
    }

    @Test
    @DisplayName("test Given CardId and CardRequest Object When UpdateUserCard")
    public void testUpdateUserCard(){
        given(cardsRepository.findById(anyLong())).willReturn(Optional.of(card));

        cardService.updateUserCard(card.getId(),cardRequestUpdate);

        verify(cardsRepository).save(any(Cards.class));
        verifyNoMoreInteractions(cardsRepository);
    }

    @Test
    @DisplayName("test Given invalid CardId and CardRequest Object When UpdateUserCard Then Throw ObjectNotFound")
    public void testUpdateUserCardWithInvalidCardId(){
        given(cardsRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> cardService.updateUserCard(5L, cardRequestUpdate))
                .withMessage("The card is not found");
    }
}

