package com.project.onlybuns.model;

import com.project.onlybuns.model.RegisteredUser;
import com.rabbitmq.client.AMQP;
import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    private RegisteredUser admin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chatroom_participants",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<RegisteredUser> participants = new HashSet<>();

    private boolean isGroupChat;


}