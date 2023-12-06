package com.example.msuser.rest.services;

import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.domain.repositories.CardsRepository;
import com.example.msuser.rest.dto.CardRequestDto;
import com.example.msuser.rest.dto.CardRequestUpdate;
import com.example.msuser.rest.dto.CardResponseDto;
import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CardService {

    private final CardsRepository cardsRepository;

    private final PasswordEncoder encoder;

    private final UserService userService;


    @Transactional
    public void createNewCard(HttpServletRequest request, CardRequestDto dto){
            var user = userService.getUserAuthenticate(request);
            var type = TypeCard.codOf(dto.getTypeCard());
            var card =cardsRepository.save(encryptCardData(new Cards(dto, user, type)));
            user.getPaymentMethods().add(card);
            userService.save(user);
    }

    @Transactional
    public List<CardResponseDto> findAllPaymentMethod(HttpServletRequest request){
        var user = userService.getUserAuthenticate(request);
        var cards = user.getPaymentMethods().stream().map(CardResponseDto::new)
                .collect(Collectors.toList());
        if(cards.isEmpty()){
            throw new CustomException("The user does not have any registered payment methods");
        }
        return cards;
    }

    public Cards findById(Long id){
        return cardsRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("The card is not found"));
    }

    @Transactional
    public List<Cards> findAllCardsByUser(Long id){
        var user = userService.findById(id);
        var cards = cardsRepository.findByCardUser(user);
        if(cards.isEmpty()){
            throw new CustomException("The user does not have any registered payment methods");
        }
        return cards;
    }

    @Transactional
    public void deleteById(Long cardId, HttpServletRequest request){
        var card = findById(cardId);
        var user = userService.getUserAuthenticate(request);
        user.getPaymentMethods().remove(card);
        cardsRepository.delete(card);
        userService.save(user);

    }

    @Transactional
    public void updateUserCard(Long cardId, CardRequestUpdate update){
        var card = findById(cardId);
        updateData(card,update);
        cardsRepository.save(card);
    }

    private void updateData(Cards card, CardRequestUpdate update) {
        if(update.getCvv() != null){
            card.setCvv(update.getCvv());
        }
        if(update.getExpiration() != null ){
            card.setExpiration(update.getExpiration());
        }
        if(update.getNumber() != null){
            card.setNumber(update.getNumber());
        }
        if(update.getName() != null){
            card.setName(update.getName());
        }
        if(card.getType().getCode() != update.getTypeCod()){
            var type = TypeCard.codOf(update.getTypeCod());
            card.setType(type);
        }
    }

    private Cards encryptCardData(Cards card){
        card.setNumber(encoder.encode(card.getNumber()));
        card.setCvv(encoder.encode(card.getCvv()));
        card.setExpiration(encoder.encode(card.getExpiration()));
        return card;
    }




}
