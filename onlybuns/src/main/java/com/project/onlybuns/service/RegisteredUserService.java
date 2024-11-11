package com.project.onlybuns.service;

import com.project.onlybuns.dto.LoginDto;
import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.dto.RegistrationDto;
import com.project.onlybuns.mapper.RegisteredUserMapper;
import com.project.onlybuns.model.Location;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.PostRepository;
import com.project.onlybuns.repository.RegisteredUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RegisteredUserService {


    private final RegisteredUserRepository registeredUserRepository;
    //private final PostService postService;
    private final PostRepository postRepository;
    private final RegisteredUserMapper registeredUserMapper;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public RegisteredUserDto getUserProfile(Integer userId) {
        return registeredUserRepository.findById(userId)
                .map(this.registeredUserMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public RegisteredUser findById(Integer userId) {
        return registeredUserRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public void register(RegistrationDto registrationDto) {

        //validacije
        if (registeredUserRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException ("User with the given email already exists!");
        }
        if (registeredUserRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException ("User with the given username already exists!");
        }


        RegisteredUser user = new RegisteredUser();
        Location location = new Location();
        location.setCity(registrationDto.getCity());
        location.setCountry(registrationDto.getCountry());
        location.setStreetName(registrationDto.getStreetName());
        location.setStreetNumber(registrationDto.getStreetNumber());
        user.setUsername(registrationDto.getUsername());

        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setAddress(location);
        user.setActive(false);
        user.setAdmin(false);
        user.setActivationDate(null);
        user.setRegistrationDate(LocalDateTime.now());
        user.setFollowersNumber(0);
        user.setLastLoginDate(null);
        String hashedPassword = bCryptPasswordEncoder.encode(registrationDto.getPassword());
        user.setPassword(hashedPassword);

        String token = generateActivationToken(user);
        user.setActivationToken(token);

        registeredUserRepository.save(user);

        emailService.sendActivationEmail(user.getEmail(), token);
    }

    private String generateActivationToken(RegisteredUser user) {
        return UUID.randomUUID().toString();
    }

    public void activateUser(String token) {
        RegisteredUser user = findUserByToken(token);
        if (user != null) {
            user.setActive(true);
            user.setActivationDate(LocalDateTime.now());
            registeredUserRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid activation token!");
        }
    }

    private RegisteredUser findUserByToken(String token) {
        return registeredUserRepository.findByActivationToken(token)
                .orElseThrow(() -> new EntityNotFoundException("User not found with the given token"));
    }

    public String login(LoginDto loginDto) {
        RegisteredUser user = registeredUserRepository.findByEmail(loginDto.getEmail()).orElse(null);
        if (user != null && user.isActive()) {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            user.setLastLoginDate(LocalDateTime.now());
            registeredUserRepository.save(user);
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.isAdmin() ? "ADMIN" : "USER");
           return jwtService.generateToken(claims,user);
        } else {
            return "";
        }



    }

    public RegisteredUser findByEmail(String email) {
        return registeredUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<RegisteredUserDto> getAllUsers() {
        List<RegisteredUser> users = registeredUserRepository.findAll();  
        return users.stream()
                .map(registeredUserMapper::toUserDto)
                .collect(Collectors.toList());
    }




    public int getNumberOfFollowers(Integer userId) {
        RegisteredUser user = registeredUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return user.getFollowersNumber();
    }

    public void makeUserAdmin(Integer userId) {
        RegisteredUser user = registeredUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setAdmin(true);
        registeredUserRepository.save(user);
    }


    public Page<RegisteredUserDto> searchUsers(String firstName, String lastName, String email,
                                               Integer minPosts, Integer maxPosts, int page, int size, String sortBy, String order) {
        Pageable pageable = PageRequest.of(page, size, order.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<RegisteredUser> usersPage = registeredUserRepository.searchUsers(
                firstName, lastName, email, minPosts, maxPosts, pageable);

        return usersPage.map(registeredUserMapper::toUserDto);
    }










}

