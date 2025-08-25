package com.project.onlybuns.repository;

import com.project.onlybuns.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.id = :userId")
    List<ChatRoom> findChatRoomsByParticipantId(@Param("userId") Integer userId);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p1 JOIN cr.participants p2 " +
            "WHERE cr.isGroupChat = false " +
            "AND p1.id = :userId1 " +
            "AND p2.id = :userId2")
    Optional<ChatRoom> findPrivateChatRoomBetweenUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);




}