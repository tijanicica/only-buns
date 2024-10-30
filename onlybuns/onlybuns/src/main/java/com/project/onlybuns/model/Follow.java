package com.project.onlybuns.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // onaj koji je zapracen
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "followed_id")
    private RegisteredUser followedUser;

    //onaj koji je zapratio
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "follower_id")
    private RegisteredUser follower;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}
