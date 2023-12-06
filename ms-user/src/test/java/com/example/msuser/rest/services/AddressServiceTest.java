package com.example.msuser.rest.services;

import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.repositories.AddressRepository;
import com.example.msuser.rest.dto.AddressRequestDto;
import com.example.msuser.rest.dto.AddressRequestUpdate;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private Users user;
    private AddressRequestDto addressRequestDto;
    private AddressRequestUpdate addressRequestUpdate;

    @BeforeEach
    public void setUp(){
        user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("99940028922");
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

    }

    @DisplayName("test GivenAddress Request Dto Object And Http Servlet Request when Create New Address")
    @Test
    void testCreateNewAddress(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);
        given(addressRepository.save(any(Address.class))).willReturn(address);

        addressService.createNewAddress(request,addressRequestDto);

        verify(userService).getUserAuthenticate(any(HttpServletRequest.class));
        verify(userService).save(any(Users.class));
        verify(addressRepository).save(any(Address.class));
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(addressRepository);
    }

    @DisplayName("test Given Address Id when Find By Id then Return Address Object")
    @Test
    void testFindById(){
        given(addressRepository.findById(address.getId())).willReturn(Optional.of(address));

        var addressOutput = addressService.findById(address.getId());
        assertThat(addressOutput).isNotNull();
        assertThat(addressOutput).usingRecursiveComparison()
                .comparingOnlyFields("street","state","city","cep","neighborhood")
                .isEqualTo(address);
    }

    @DisplayName("test Given Address Id when Find By Id then Throw Object Not Found Exception")
    @Test
    void testFindByIdWithInvalidId(){
        given(addressRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> addressService.findById(5L))
                .withMessage("The address is not found");
    }


    @Test
    @DisplayName("test Given HttpServletRequest When findAllAddressMethod Then Return UserAddressList")
    public void testFindAllAddressMethod(){
        Address address = new Address(1L,
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000",
                user);
        user.getAddresses().add(address);

        HttpServletRequest request = mock(HttpServletRequest.class);
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);


        var list = addressService.findAllAddressMethod(request);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list)
                .extracting("street","state","city","cep","neighborhood")
                .contains(
                        tuple("rua a","estado a","cidade a","00000000","bairro a")
                );
    }

    @Test
    @DisplayName("test Given HttpServletRequest When FindAllAddressMethod Then Throw CustomException")
    public void FindAllAddressesMethodWithEmptyUserCardsList(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> addressService.findAllAddressMethod(request))
                .withMessage("The user does not have any registered address");
    }

    @DisplayName("test Given Address Id when Delete By Id")
    @Test
    void testDeleteById(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(addressRepository.findById(anyLong())).willReturn(Optional.of(address));
        given(userService.getUserAuthenticate(any(HttpServletRequest.class))).willReturn(user);

        addressService.deleteById(request, address.getId());

        verify(addressRepository).findById(anyLong());
        verify(userService).getUserAuthenticate(any(HttpServletRequest.class));
        verify(userService).save(any(Users.class));
        verify(addressRepository).delete(any(Address.class));
        verifyNoMoreInteractions(addressRepository);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("test Given UserId then findAllByUserId Then Return UserAddressList")
    public void testFindAllByUserId(){
        given(userService.findById(anyLong())).willReturn(user);
        given(addressRepository.findByAddressUser(user)).willReturn(Collections.singletonList(address));

        var list = addressService.findAllByUserId(user.getId());

        assertThat(list.size()).isEqualTo(1);
        verify(userService).findById(anyLong());
        verify(addressRepository).findByAddressUser(any(Users.class));
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    @DisplayName("test Given UserId then findAllByUserId Then Throw CustomException")
    public void testFindAllByUserIdWithEmptyList(){
        Users user1 = new Users(
                2L,
                "jonas",
                "jonas@example.com",
                "99940028922",
                "30956930018",
                "senha123");

        given(userService.findById(anyLong())).willReturn(user);
        given(addressRepository.findByAddressUser(any(Users.class))).willReturn(Collections.emptyList());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> addressService.findAllByUserId(user1.getId()))
                        .withMessage("The user does not have any registered address");

        verify(userService).findById(anyLong());
        verify(addressRepository).findByAddressUser(any(Users.class));
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    @DisplayName("test given AddressRequest Object And AddressId When UpdateAddresData")
    public void testUpdateAddressData(){
        given(addressRepository.findById(anyLong())).willReturn(Optional.of(address));
        addressService.updateAddressData(addressRequestUpdate, address.getId());

        verify(addressRepository).save(any(Address.class));
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    @DisplayName("test given AddressRequest With Invalid Id Object And AddressId When UpdateAddressData Then Throw ObjectNotFound")
    public void testUpdateAddressDataWithInvalidId(){
        given(addressRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> addressService.updateAddressData(addressRequestUpdate,5L))
                        .withMessage("The address is not found");

    }



}
