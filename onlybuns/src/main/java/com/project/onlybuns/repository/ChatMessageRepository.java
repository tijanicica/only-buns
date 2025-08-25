package com.project.onlybuns.repository;

import com.project.onlybuns.model.ChatMessage;
import com.project.onlybuns.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findTop10ByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);//poslednjih 10 poruka
}