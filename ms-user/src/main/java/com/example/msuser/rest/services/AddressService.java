package com.example.msuser.rest.services;

import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.repositories.AddressRepository;
import com.example.msuser.rest.dto.AddressRequestDto;
import com.example.msuser.rest.dto.AddressRequestUpdate;
import com.example.msuser.rest.dto.AddressResponseDto;
import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    private final UserService userService;

    @Transactional
    public void createNewAddress(HttpServletRequest request, AddressRequestDto objectDto){
        var user = userService.getUserAuthenticate(request);
        var address = addressRepository.save(Address.of(objectDto,user));
        user.getAddresses().add(address);
        userService.save(user);
    }


    @Transactional
    public List<AddressResponseDto> findAllAddressMethod(HttpServletRequest request){
        var user = userService.getUserAuthenticate(request);
        var addresses = user.getAddresses().stream().map(AddressResponseDto::of)
                .collect(Collectors.toList());
        if(addresses.isEmpty()){
            throw new CustomException("The user does not have any registered address");
        }
        return addresses;
    }

    public Address findById(Long id){
        return addressRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("The address is not found"));
    }

    @Transactional
    public void deleteById(HttpServletRequest request, Long id){
        var address = findById(id);
        var user = userService.getUserAuthenticate(request);
        user.getAddresses().remove(address);
        userService.save(user);
        addressRepository.delete(address);
    }

    @Transactional
    public List<Address> findAllByUserId(Long id){
        var user = userService.findById(id);
        var list = addressRepository.findByAddressUser(user);
        if(list.isEmpty()){
            throw new CustomException("The user does not have any registered address");
        }
        return list;
    }

    @Transactional
    public void updateAddressData(AddressRequestUpdate requestUpdate,Long id){
        var address = findById(id);
        updateData(address,requestUpdate);
        addressRepository.save(address);
    }

    private void updateData(Address address, AddressRequestUpdate requestUpdate) {
        if(requestUpdate.getCep() != null){
            address.setCep(requestUpdate.getCep());
        }
        if(requestUpdate.getCity() != null){
            address.setCity(requestUpdate.getCity());
        }
        if(requestUpdate.getStreet() != null){
            address.setStreet(requestUpdate.getStreet());
        }
        if(requestUpdate.getState() != null){
            address.setState(requestUpdate.getState());
        }
        if(requestUpdate.getNeighborhood() != null){
            address.setNeighborhood(requestUpdate.getNeighborhood());
        }
    }
}
