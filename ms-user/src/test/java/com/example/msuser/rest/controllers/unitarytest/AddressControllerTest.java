package com.example.msuser.rest.controllers.unitarytest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.rest.controllers.AddressController;
import com.example.msuser.rest.dto.AddressRequestDto;
import com.example.msuser.rest.dto.AddressRequestUpdate;
import com.example.msuser.rest.dto.AddressResponseDto;
import com.example.msuser.rest.services.AddressService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Roles roles;
    private Users user;
    private Address address;
    private AddressRequestDto addressRequestDto;
    private AddressRequestUpdate addressRequestUpdate;
    private String access_token;

    @BeforeEach
    public void beforeEach(){
        objectMapper  = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
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

        address = new Address(
                1L,
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000",
                user);

        addressRequestDto = new AddressRequestDto();
        addressRequestDto.setCep(address.getCep());
        addressRequestDto.setStreet(address.getStreet());
        addressRequestDto.setCity(address.getCity());
        addressRequestDto.setState(address.getState());
        addressRequestDto.setNeighborhood(address.getNeighborhood());

        addressRequestUpdate =new AddressRequestUpdate();
        addressRequestUpdate.setCep(address.getCep());
        addressRequestUpdate.setStreet(address.getStreet());
        addressRequestUpdate.setCity(address.getCity());
        addressRequestUpdate.setNeighborhood(address.getNeighborhood());
        addressRequestUpdate.setState(address.getState());

        roles = new Roles("ROLE_USER");
        user.getRoles().add(roles);

        access_token = JWT.create().withSubject(user.getEmail())
                .withClaim("id",user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));

    }


    @Test
    @DisplayName("test Given HttpServletRequest And AddressRequest Object when CreateNewAddress Then Return Status CREATED")
    public void testCreateNewAddress() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        assert addressRequestDto.getCep() != null && addressRequestDto.getCep() != "";
        mockMvc.perform(post("/api/address/create")
                .header("Authorization","Bearer "+access_token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Null Street And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithNullStreet() throws Exception {
        addressRequestDto.setStreet(null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Null State And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithNullState() throws Exception {
        addressRequestDto.setState(null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Null City And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithNullCity() throws Exception {
        addressRequestDto.setCity(null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Null Cep And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithNullCep() throws Exception {
        addressRequestDto.setCep(null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Null Neighborhood And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithNullNeighborhood() throws Exception {
        addressRequestDto.setNeighborhood(null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //--------------------------


    @Test
    @DisplayName("test Given HttpServletRequest With Empty Street And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithEmptyStreet() throws Exception {
        addressRequestDto.setStreet("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Empty State And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithEmptyState() throws Exception {
        addressRequestDto.setState("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Empty City And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithEmptyCity() throws Exception {
        addressRequestDto.setCity("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Empty Cep And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithEmptyCep() throws Exception {
        addressRequestDto.setCep("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Null Neighborhood And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithEmptyNeighborhood() throws Exception {
        addressRequestDto.setNeighborhood("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest With Invalid Cep And AddressRequest Object when CreateNewAddress Then Return Status BADREQUEST")
    public void testCreateNewAddressWithInvalidCep() throws Exception {
        addressRequestDto.setCep("0000");
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(post("/api/address/create")
                        .header("Authorization","Bearer "+access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given HttpServletRequest When findAllAddressMethods then Return UserCardsList")
    public void testFindAllAddressMethods() throws Exception {
        user.getAddresses().add(address);
        HttpServletRequest request = mock(HttpServletRequest.class);
        var list = Collections.singletonList(AddressResponseDto.of(address));
        given(addressService.findAllAddressMethod(any(HttpServletRequest.class))).willReturn(list);

        mockMvc.perform(get("/api/address/myAddresses")
                        .header("Authorization", "Bearer " + access_token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test Given HttpRequest and AddressId When delete Then Return Status NoContent")
    public void testDelete() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        mockMvc.perform(delete("/api/address/delete/{id}",address.getId())
                .header("Authorization","Bearer " + access_token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test Given AddressId and AddressRequest Object Then Update Then Return Status NoContent")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/address/update/{id}",address.getId())
                .header("Authorization", "Bearer " +access_token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test Given AddressId and AddressRequest Cep With Only Spaces Then Update Then Return Status BadRequest")
    public void testUpdateCepWithOnlySpaces() throws Exception {
        addressRequestUpdate.setCep("  ");
        mockMvc.perform(patch("/api/address/update/{id}",address.getId())
                        .header("Authorization", "Bearer " +access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given AddressId and AddressRequest City With Only Spaces Then Update Then Return Status BadRequest")
    public void testUpdateCityWithOnlySpaces() throws Exception {
        addressRequestUpdate.setCity("  ");
        mockMvc.perform(patch("/api/address/update/{id}",address.getId())
                        .header("Authorization", "Bearer " +access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given AddressId and AddressRequest State With Only Spaces Then Update Then Return Status BadRequest")
    public void testUpdateStateWithOnlySpaces() throws Exception {
        addressRequestUpdate.setState("  ");
        mockMvc.perform(patch("/api/address/update/{id}",address.getId())
                        .header("Authorization", "Bearer " +access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given AddressId and AddressRequest Street With Only Spaces Then Update Then Return Status BadRequest")
    public void testUpdateStreetWithOnlySpaces() throws Exception {
        addressRequestUpdate.setStreet("  ");
        mockMvc.perform(patch("/api/address/update/{id}",address.getId())
                        .header("Authorization", "Bearer " +access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test Given AddressId and AddressRequest Neighborhood With Only Spaces Then Update Then Return Status BadRequest")
    public void testUpdateNeighborhoodWithOnlySpaces() throws Exception {
        addressRequestUpdate.setStreet("  ");
        mockMvc.perform(patch("/api/address/update/{id}",address.getId())
                        .header("Authorization", "Bearer " +access_token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
