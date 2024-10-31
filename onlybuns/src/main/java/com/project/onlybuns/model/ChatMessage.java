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
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private RegisteredUser sender;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

}
