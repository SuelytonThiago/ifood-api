package com.example.msuser.rest.services;
import com.example.msuser.rest.dto.*;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.repositories.UserRepository;
import com.example.msuser.rest.services.exceptions.AlreadyExistsException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final TokenService tokenService;

    private final RoleService roleService;

    @Transactional
    public void createNewUser(UserRequestDto requestDto){
        userRepository.findFirstByEmailOrCpf(requestDto.getEmail(), requestDto.getCpf()).ifPresent(e->{
            throw new AlreadyExistsException("This email or cpf already in use");
        });
        var user = Users.of(requestDto);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        user.getRoles().add(roleService.findByRoleName("ROLE_USER"));
        userRepository.save(user);
    }

    @Transactional
    public Users createNewOwner(UserRequestDto requestDto){
        userRepository.findFirstByEmailOrCpf(requestDto.getEmail(), requestDto.getCpf()).ifPresent(e->{
            throw new AlreadyExistsException("This email or cpf already in use");
        });
        Users user = new Users(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getContactNumber(),
                requestDto.getCpf(),
                requestDto.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        user.getRoles().add(roleService.findByRoleName("ROLE_OWNER"));

        return userRepository.save(user);
    }

    public void save(Users user){
        userRepository.save(user);
    }

    public Users findById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("The user is not found"));
    }

    public Users findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ObjectNotFoundException("The user is not found"));
    }

    public Users getUserAuthenticate(HttpServletRequest request){
        var email = tokenService.getUserEmailAuthenticated(request);
        return findByEmail(email);
    }

    //***
    @Transactional
    public void update(UserRequestUpdate requestDto,HttpServletRequest request){
        var id = tokenService.getClaimId(request);
        verifyEmailAlreadyInUse(id, requestDto.getEmail());
        var user = findById(id);
        updateData(user,requestDto);
        userRepository.save(user);
    }

    private void updateData(Users user, UserRequestUpdate requestDto) {
       if(requestDto.getContactNumber() != null){
           user.setContactNumber(requestDto.getContactNumber());
       }
       if(requestDto.getPassword() != null){
            user.setPassword(encoder.encode(requestDto.getPassword()));
       }
       if(requestDto.getName() != null){
           user.setName(requestDto.getName());
       }
       if(requestDto.getEmail() != null){
           user.setEmail(requestDto.getEmail());
       }
    }

    //***
    public void deleteOwnerById(Long id){
        userRepository.deleteById(id);
    }

    private void verifyEmailAlreadyInUse(Long id,String email){
        var existUser = userRepository.findByEmail(email);
        if(existUser.isPresent() && !existUser.get().getId().equals(id)){
            throw new AlreadyExistsException("This Email Already In Use");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("The user is not found"));
    }
}
