package com.example.msuser.domain.entities;
import com.example.msuser.rest.dto.AddressRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String cep;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users addressUser;

    public static Address of(AddressRequestDto requestDto, Users  user){
        Address address = new Address();
        address.setStreet(requestDto.getStreet());
        address.setNeighborhood(requestDto.getNeighborhood());
        address.setCep(requestDto.getCep());
        address.setCity(requestDto.getCity());
        address.setState(requestDto.getState());
        address.setAddressUser(user);
        return address;
    }

    public Address(String street, String neighborhood, String city, String state, String cep, Users addressUser) {
        this.street = street;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.cep = cep;
        this.addressUser = addressUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return getId().equals(address.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
