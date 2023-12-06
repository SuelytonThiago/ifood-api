package com.example.msuser.rest.controllers.unitarytest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.rest.controllers.CardController;
import com.example.msuser.rest.dto.CardRequestDto;
import com.example.msuser.rest.dto.CardRequestUpdate;
import com.example.msuser.rest.dto.CardResponseDto;
import com.example.msuser.rest.services.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Cards card;
    private Users user;
    private CardRequestDto cardRequestDto;
    private CardRequestUpdate cardRequestUpdate;
    private String access_token;


    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .alwaysDo(print()).build();

        user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("40028922");
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

        access_token = JWT.create().withSubject(user.getEmail())
                .withClaim("id",user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));
    }

    @Test
    @DisplayName("test given CardRequest Object when CreateNewCard Then Return Status Created")
    public void testCreateNewCard() throws Exception {
        mockMvc.perform(post("/api/cards/create")
                .header("Authorization","Bearer " + access_token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("test given CardRequest Object With Null Name when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithNullName() throws Exception {
        cardRequestDto.setName(null);
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Null Number when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithNullNumber() throws Exception {
        cardRequestDto.setNumber(null);
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Null Expiration when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithNullExpiration() throws Exception {
        cardRequestDto.setExpiration(null);
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Null Cvv when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithNullCvv() throws Exception {
        cardRequestDto.setCvv(null);
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //-----------------

    @Test
    @DisplayName("test given CardRequest Object With Empty Name when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithEmptyName() throws Exception {
        cardRequestDto.setName("");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Empty Number when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithEmptyNumber() throws Exception {
        cardRequestDto.setNumber("");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Empty Expiration when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithEmptyExpiration() throws Exception {
        cardRequestDto.setExpiration("");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Empty Cvv when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithEmptyCvv() throws Exception {
        cardRequestDto.setCvv("");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Invalid Cvv when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithInvalidCvv() throws Exception {
        cardRequestDto.setCvv("123321123");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Invalid Number when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithInvalidNumber() throws Exception {
        cardRequestDto.setNumber("1233asda21123");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given CardRequest Object With Invalid Expiration Data when CreateNewCard Then Return Status BadRequest")
    public void testCreateNewCardWithInvalidExpirationData() throws Exception {
        cardRequestDto.setExpiration("30/12345");
        mockMvc.perform(post("/api/cards/create")
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest When findAllPaymentMethods then Return UserCardsList")
    public void testFindAllPaymentMethods() throws Exception {
        user.getPaymentMethods().add(card);
        HttpServletRequest request = mock(HttpServletRequest.class);
        var list = Collections.singletonList(new CardResponseDto(card));
        given(cardService.findAllPaymentMethod(any(HttpServletRequest.class))).willReturn(list);

        mockMvc.perform(get("/api/cards/paymentMethods")
                .header("Authorization", "Bearer " + access_token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test Given CardId When Delete Then Return Status NoContent")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/cards/delete/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test Given CardRequest Object And CardId When Update Then Return Status NoContent")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test Given CardRequest Object Number With Only SpaceWhen Update Then Return Status BadRequest")
    public void testUpdateCvvWithOnlySpaces() throws Exception {
        cardRequestUpdate.setCvv("  ");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given CardRequest Object Number With Only SpaceWhen Update Then Return Status BadRequest")
    public void testUpdateNumberWithOnlySpaces() throws Exception {
        cardRequestUpdate.setNumber("  ");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given CardRequest Object Name With Only SpaceWhen Update Then Return Status BadRequest")
    public void testUpdateNameWithOnlySpaces() throws Exception {
        cardRequestUpdate.setName("  ");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given CardRequest Object Expiration With Only SpaceWhen Update Then Return Status BadRequest")
    public void testUpdateExpirationWithOnlySpaces() throws Exception {
        cardRequestUpdate.setExpiration("  ");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given CardRequest Object Invalid Cvv Update Then Return Status BadRequest")
    public void testUpdateWithInvalidCvv() throws Exception {
        cardRequestUpdate.setCvv("asdas");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given CardRequest Object Invalid Number Update Then Return Status BadRequest")
    public void testUpdateWithInvalidNumber() throws Exception {
        cardRequestUpdate.setNumber("asdassad47785458");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given CardRequest Object Invalid Expiration Update Then Return Status BadRequest")
    public void testUpdateWithInvalidExpiration() throws Exception {
        cardRequestUpdate.setExpiration("12/2025689");
        mockMvc.perform(patch("/api/cards/update/{id}",card.getId())
                        .header("Authorization","Bearer " + access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }




}
