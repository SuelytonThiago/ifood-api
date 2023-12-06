package com.example.msuser.rest.controllers.unitarytest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.rest.controllers.UserController;
import com.example.msuser.rest.dto.UserRequestDto;
import com.example.msuser.rest.dto.UserRequestUpdate;
import com.example.msuser.rest.services.UserService;
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
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    private Users user;
    private UserRequestDto userRequestDto;
    private UserRequestUpdate userRequestUpdate;
    private Roles role;
    private Cards card;
    private Address address;

    private String access_token;

    @BeforeEach
    public void setUp(){
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .alwaysDo(print()).build();

        userRequestDto = new UserRequestDto("ana",
                "ana@xample.com",
                "99940028922",
                "05661795041",
                "senha123");

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

        address = new Address(
                1L,
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000",
                user);

        userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("ana");
        userRequestUpdate.setEmail("ana@example.com");
        userRequestUpdate.setContactNumber("99940028922");
        userRequestUpdate.setPassword("senha123");

        role = new Roles("ROLE_ADMIN");

        access_token = JWT.create().withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));
    }

    @DisplayName("test Given User Request Object when Create New User then Return Http Status Created")
    @Test
    void testGivenUserRequestObject_whenCreateNewUser_thenReturnHttpStatusCreated() throws Exception {

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("test Given User Request Object With Empty Name when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithEmptyName_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setName("");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Empty Email when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithEmptyEmail_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setEmail("");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @DisplayName("test Given User Request Object With Invalid Email when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithInvalidEmail_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setEmail("asdasdasd");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Empty Password when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithEmptyPassword_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setPassword("");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Invalid Password when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenCreateNewUserWithInvalidPassword() throws Exception {
        userRequestDto.setPassword("asda");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Empty Contact Number when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithEmptyContactNumber_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setContactNumber("");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Invalid Contact Number when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testCreateNewUserWithInvalidContactNumber() throws Exception {
        userRequestDto.setContactNumber("sdadsasdsda");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Empty Cpf when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithEmptyCpf_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setCpf("");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Invalid Cpf when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithInvalidCpf_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setCpf("231351321asd");
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Null Cpf when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithNullCpf_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setCpf(null);
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Null Email when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithNullEmail_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setEmail(null);
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Null Name when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithNullName_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setName(null);
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Null Contact Number when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithNullContactNumber_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setContactNumber(null);
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test Given User Request Object With Null Password when Create New User then Throw Method Argument Not Valid Exception")
    @Test
    void testGivenUserRequestObjectWithNullPassword_whenCreateNewUser_thenThrowMethodArgumentNotValidException() throws Exception {
        userRequestDto.setPassword(null);
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("test Given HttpServletRequest And UserRequest Object When Update Then ReturnStatus NoContent")
    public void testUpdate() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(patch("/api/users/update")
                .header("Authorization","Bearer " + access_token )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test Given With Invalid Contact Number When Update Then ReturnStatus NoContent")
    public void testUpdateWithInvalidContactNumber() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        userRequestUpdate.setContactNumber("asdasdadasd");
        mockMvc.perform(patch("/api/users/update")
                        .header("Authorization","Bearer " + access_token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given With Invalid Password When Update Then ReturnStatus NoContent")
    public void testUpdateWithInvalidPassword() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        userRequestUpdate.setPassword("asd");
        mockMvc.perform(patch("/api/users/update")
                        .header("Authorization","Bearer " + access_token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given With Invalid Email When Update Then ReturnStatus NoContent")
    public void testUpdateWithInvalidEmail() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        userRequestUpdate.setEmail("asdadsd");
        mockMvc.perform(patch("/api/users/update")
                        .header("Authorization","Bearer " + access_token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given With Name Contains Only Spaces When Update Then ReturnStatus NoContent")
    public void testUpdateWithNameContainsOnlySpaces() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        userRequestUpdate.setName("   ");
        mockMvc.perform(patch("/api/users/update")
                        .header("Authorization","Bearer " + access_token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }



}
