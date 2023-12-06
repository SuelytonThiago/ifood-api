package com.example.msorder.rest.services;

import com.example.msorder.grpc.client.MyUserGrpcClient;
import com.example.user.RoleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private MyUserGrpcClient userGrpcClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userGrpcClient.findUserByEmail(email);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for(RoleResponse x : user.getRoles().getRolesList()){
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(x.getRoleName());
            authorities.add(grantedAuthority);
        }
        return new User(user.getEmail(), user.getPassword(), true,true,true,true, authorities);
    }
}
