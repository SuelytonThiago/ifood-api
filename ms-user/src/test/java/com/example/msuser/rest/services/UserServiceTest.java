package com.example.msuser.rest.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.repositories.UserRepository;
import com.example.msuser.rest.dto.UserRequestDto;
import com.example.msuser.rest.dto.UserRequestUpdate;
import com.example.msuser.rest.services.exceptions.AlreadyExistsException;
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
import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private Users user,user1;
    private Roles role;
    private UserRequestDto userRequestDto;
    private UserRequestUpdate userRequestUpdate;

    private String token,jwtSecret;

    @BeforeEach
    public void setUp(){

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

        user1 = new Users();
        user1.setId(1L);
        user1.setName("bob");
        user1.setEmail("bob@example.com");
        user1.setContactNumber("99940028922");
        user1.setCpf("95485168027");
        user1.setPassword("senha123");
        user1.setAddresses(new ArrayList<>());
        user1.setRoles(new ArrayList<>());

        role = new Roles("ROLE_USER");

        userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("ana");
        userRequestUpdate.setEmail("ana@example.com");
        userRequestUpdate.setContactNumber("99940028922");
        userRequestUpdate.setPassword("senha123");

        jwtSecret = "secret";
        int jwtExpires = 600000;

        token = JWT.create().withSubject(user.getEmail())
                .withClaim("id",user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpires))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @DisplayName("test Given User Object When CreateNewUser")
    @Test
    void testCreateNewUser(){
        given(userRepository.findFirstByEmailOrCpf(anyString(),anyString())).willReturn(Optional.empty());
        given(userRepository.save(any(Users.class))).willReturn(user);
        given(roleService.findByRoleName(anyString())).willReturn(role);

        userService.createNewUser(userRequestDto);

        verify(userRepository).findFirstByEmailOrCpf(anyString(),anyString());
        verify(roleService).findByRoleName(anyString());
        verify(userRepository, times(2)).save(any(Users.class));
        verifyNoMoreInteractions(roleService);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("test Given Existing Email Or Cpf When Save then Throws Already Exists Exception")
    @Test
    void testCreateNewUserWithEmailOrCpfAlreadyInUse(){
        given(userRepository.findFirstByEmailOrCpf(anyString(),anyString())).willReturn(Optional.of(user));

        assertThatExceptionOfType(AlreadyExistsException.class)
                .isThrownBy(() -> userService.createNewUser(userRequestDto))
                        .withMessage("This email or cpf already in use");


        verify(userRepository).findFirstByEmailOrCpf(anyString(),anyString());
        verifyNoMoreInteractions(userRepository);
        verify(userRepository,never()).save(user);
    }

    @DisplayName("test Given Id User When Find By Id then Return User Object")
    @Test
    void testFindById(){
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        var outputUser = userService.findById(user.getId());

        assertThat(outputUser).isNotNull();
        assertThat(outputUser).isEqualTo(user);
        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("test Given Id User When Find By Id then Throw Object Not Found Exception")
    @Test
    void testFindByIdWithInvalidUserId() {
        var id = 5L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> userService.findById(id))
                        .withMessage("The user is not found");

        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }


    @DisplayName("test Given Email User When Find Email then Throw Object Not Found Exception")
    @Test
    void testFindByEmail() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        var userOutput = userService.findByEmail(user.getEmail());

        assertThat(userOutput).isNotNull();
        assertThat(user).isEqualTo(userOutput);
        verify(userRepository).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("test Given Email User When Find By Email then Throw Object Not Found Exception")
    @Test
    void testFindByEmailWithInvalidEmail() {
        var email = "abcd";
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> userService.findByEmail(email))
                .withMessage("The user is not found");

        verify(userRepository).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("test Given Email User When Find By Email then Throw Object Not Found Exception")
    @Test
    void testGetUserAuthenticate() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getUserEmailAuthenticated(any(HttpServletRequest.class))).willReturn(user.getEmail());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        var userOutput = userService.getUserAuthenticate(request);

        assertThat(userOutput).isNotNull();
        assertThat(user).isEqualTo(userOutput);
        verify(userRepository).findByEmail(anyString());
        verify(tokenService).getUserEmailAuthenticated(any(HttpServletRequest.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(tokenService);
    }

    @DisplayName("test Given User Object When CreateNewOwner")
    @Test
    void testCreateNewOwner(){
        given(userRepository.findFirstByEmailOrCpf(anyString(),anyString())).willReturn(Optional.empty());
        given(userRepository.save(any(Users.class))).willReturn(user);
        given(roleService.findByRoleName(anyString())).willReturn(role);

        var outputUser = userService.createNewOwner(userRequestDto);

        assertThat(outputUser).usingRecursiveComparison()
                .comparingOnlyFields("name","email","contactNumber","cpf","password")
                        .isEqualTo(user);

        verify(userRepository).findFirstByEmailOrCpf(anyString(),anyString());
        verify(roleService).findByRoleName(anyString());
        verify(userRepository, times(2)).save(any(Users.class));
        verifyNoMoreInteractions(roleService);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("test Given Existing Email Or Cpf When Save then Throws Already Exists Exception")
    @Test
    void testGivenExistingEmailOrCpf_WhenSave_thenThrowsAlreadyExistsException(){
        given(userRepository.findFirstByEmailOrCpf(anyString(),anyString())).willReturn(Optional.of(user));

        assertThatExceptionOfType(AlreadyExistsException.class)
                .isThrownBy(() -> userService.createNewOwner(userRequestDto))
                .withMessage("This email or cpf already in use");


        verify(userRepository).findFirstByEmailOrCpf(anyString(),anyString());
        verifyNoMoreInteractions(userRepository);
        verify(userRepository,never()).save(user);
    }


    @Test
    @DisplayName("test Given UserRequest Object And HttpServletRequest When Update")
    public void testUpdate(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(user.getId());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        userService.update(userRequestUpdate,request);

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(userRepository).save(any(Users.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(tokenService);
    }

    @Test
    @DisplayName("test Given UserId When DeleteOwnerById")
    public void testDeleteOwnerById(){
        userService.deleteOwnerById(user1.getId());
        verify(userRepository).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
