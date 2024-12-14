package com.project.onlybuns.service;

import com.project.onlybuns.dto.*;
import com.project.onlybuns.mapper.RegisteredUserMapper;
import com.project.onlybuns.model.Follow;
import com.project.onlybuns.model.Location;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.LocationRepository;
import com.project.onlybuns.repository.PostRepository;
import com.project.onlybuns.repository.RegisteredUserRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RegisteredUserService {


    private final RegisteredUserRepository registeredUserRepository;
    private final RegisteredUserMapper registeredUserMapper;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final FollowService followService;
    private final LocationService locationService;
    private final LocationRepository locationRepository;




    public RegisteredUserDto getUserProfile(Integer userId) {
        return registeredUserRepository.findById(userId)
                .map(this.registeredUserMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public RegisteredUser findById(Integer userId) {
        return registeredUserRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
    @Transactional
    public void register(RegistrationDto registrationDto) {

        //validacije
        if (registeredUserRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException ("User with the given email already exists!");
        }
        if (registeredUserRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException ("User with the given username already exists!");
        }

        // samo za testiranje transakcije
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            if (user.getRegistrationDate().plusMinutes(5).isAfter(LocalDateTime.now())){
                user.setActive(true);
                user.setActivationDate(LocalDateTime.now());
                registeredUserRepository.save(user);
            } else {
                throw new IllegalArgumentException("Activation token has expired!");
            }
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

    public boolean emailExists(String email) {
        return registeredUserRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return registeredUserRepository.existsByUsername(username);
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

    public RegisteredUserDto findDtoByEmail(String email) {
        RegisteredUser registeredUser = registeredUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return registeredUserMapper.toUserDto(registeredUser); // koristimo mapper
    }

        public List<RegisteredUserDto> getFollowers(String email) {
            // Prvo, nađemo sve Follow entitete gde je 'followedUser' onaj koji nas zanima
            List<Follow> follows = followService.findByFollowedUserEmail(email);

            // Pretvorimo listu Follow entiteta u listu RegisteredUserDto objekata
            return follows.stream()
                    .map(follow -> registeredUserMapper.toUserDto(follow.getFollower()))
                    .collect(Collectors.toList());
        }

    public List<RegisteredUserDto> getFollowing(String email) {
        // Prvo, nađemo sve Follow entitete gde je 'followedUser' onaj koji nas zanima
        List<Follow> follows = followService.findByFollowerEmail(email);

        // Pretvorimo listu Follow entiteta u listu RegisteredUserDto objekata
        return follows.stream()
                .map(follow -> registeredUserMapper.toUserDto(follow.getFollower()))
                .collect(Collectors.toList());
    }


    public void changePassword(String email, String oldPassword, String newPassword) {
        RegisteredUser user = registeredUserRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        // Proveravamo da li je stara lozinka tačna
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        // Ažuriramo lozinku korisnika
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        registeredUserRepository.save(user);
    }

    public void updateUserProfile(String email, EditUserDto userEditDTO) throws Exception {
        RegisteredUser user = registeredUserRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));

        Location location = new Location();


        user.setFirstName(userEditDTO.getFirstName());
        user.setLastName(userEditDTO.getLastName());
        location.setCity(userEditDTO.getCity());
        location.setCountry(userEditDTO.getCountry());
        location.setStreetName(userEditDTO.getStreetName());
        location.setStreetNumber(userEditDTO.getStreetNumber());
        user.setAddress(location);

        locationRepository.save(location);
        registeredUserRepository.save(user);
    }


    public void deleteInactiveUsers() {
        LocalDateTime cutoffDate = LocalDateTime.now()
                .minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        List<RegisteredUser> inactiveUsers = registeredUserRepository.findInactiveUsersBefore(cutoffDate);

        if (!inactiveUsers.isEmpty()) {
            registeredUserRepository.deleteAll(inactiveUsers);
            System.out.println("Deleted " + inactiveUsers.size() + " inactive user(s).");
        } else {
            System.out.println("No inactive users found to delete.");
        }
    }

    // ovo otkomentarisati pred odbranu - to je funkcionalnost
    /* 0 - nula sekundi
       0 - nula minuta
       0 - sati
       L - poslednji dan u mesecu
       * - sve
       ? - nije nikakav konkretan dan u nedelji
       znaci svaki posl dan u mesecu u ponoc
     */

  /*  @Scheduled(cron = "0 0 0 L * ?")
    public void scheduleInactiveUserCleanup() {
        deleteInactiveUsers();
    }*/

    // ovo je samo da se pokaze da radi, svaki minut
    @Scheduled(cron = "0 * * * * ?")
    public void scheduleInactiveUserCleanup() {
        System.out.println("Starting cleanup task at: " + LocalDateTime.now());
        deleteInactiveUsers();
        System.out.println("Cleanup task completed at: " + LocalDateTime.now());
    }












}

