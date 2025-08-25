package com.project.onlybuns.model;

import com.project.onlybuns.model.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    private LocalDateTime sentAt;

    @ManyToOne
    private RegisteredUser sender;

    @ManyToOne
    private ChatRoom chatRoom;

}