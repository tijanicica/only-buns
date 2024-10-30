package com.project.onlybuns.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ???????
    @OneToMany
    private List<RegisteredUser> participans = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private RegisteredUser chatAdmin;

    private String name;




}
