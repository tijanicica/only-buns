package com.project.onlybuns.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class RegisteredUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location address;

    private boolean isActive;

    private LocalDateTime registrationDate;

    private LocalDateTime activationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastLoginDate;

    private boolean isAdmin;

    private int followersNumber;

    private String activationToken;

    



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        String role = "USER";
        if (isAdmin) {
            role = "ADMIN";
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_"+ role));
        return authorities;
    }

    //zbog autorizacije
    public String getUsername() {
        return email;
    }

}
