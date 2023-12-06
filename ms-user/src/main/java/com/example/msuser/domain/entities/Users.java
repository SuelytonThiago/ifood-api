package com.example.msuser.domain.entities;

import com.example.msuser.rest.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Users implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String cpf;
    private String password;

    @OneToMany(mappedBy = "addressUser")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "cardUser")
    private List<Cards> paymentMethods = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Roles> roles = new ArrayList<>();

    public Users(Long id, String name, String email, String contactNumber, String cpf, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.cpf = cpf;
        this.password = password;
    }

    public Users(String name, String email, String contactNumber, String cpf, String password) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.cpf = cpf;
        this.password = password;
    }

    public static Users of(UserRequestDto requestDto){
        Users user = new Users();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setContactNumber(requestDto.getContactNumber());
        user.setCpf(requestDto.getCpf());
        user.setPassword(requestDto.getPassword());
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Users users)) return false;
        return Objects.equals(getId(), users.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
