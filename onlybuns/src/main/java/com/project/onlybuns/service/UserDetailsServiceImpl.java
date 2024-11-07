package com.project.onlybuns.service;

import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<RegisteredUser> account = registeredUserRepository.findByEmail(username);
        if(account.isPresent()){
            String role = account.get().isAdmin() ? "ADMIN" : "USER";
            return org.springframework.security.core.userdetails.User.withUsername(username).password(account.get().getPassword()).authorities(account.get().getAuthorities()).roles(role).build();
        }
        throw new UsernameNotFoundException("User not found with this username: " + username);
    }
}