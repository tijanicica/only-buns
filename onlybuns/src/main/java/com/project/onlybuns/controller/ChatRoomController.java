// u controller/ChatRoomController.java
package com.project.onlybuns.controller;

import com.project.onlybuns.model.ChatMessage;
import com.project.onlybuns.model.ChatRoom;
import com.project.onlybuns.service.ChatService;
import com.project.onlybuns.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter
@Setter
class CreateRoomRequest {
    private String name;
}

@Getter
@Setter
class AddUserRequest {
    private Integer userIdToAdd;
}

@Getter
@Setter
class StartPrivateChatRequest {
    private String username;
}

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtService jwtService;


    @PostMapping
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest request, @RequestHeader("Authorization") String token) {
        String adminEmail = jwtService.extractUsername(token.substring(7));
        ChatRoom room = chatService.createRoom(request.getName(), adminEmail);
        return ResponseEntity.ok(room);
    }



    @PostMapping("/private-chat")
    public ResponseEntity<ChatRoom> getOrCreatePrivateRoom(@RequestBody StartPrivateChatRequest request, @RequestHeader("Authorization") String token) {
        String initiatorEmail = jwtService.extractUsername(token.substring(7));
        String targetUsername = request.getUsername();
        if (targetUsername == null || targetUsername.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            ChatRoom room = chatService.getOrCreatePrivateChat(initiatorEmail, targetUsername);
            return ResponseEntity.ok(room);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/{roomId}/participants")
    public ResponseEntity<String> addUserToRoom(@PathVariable Integer roomId, @RequestBody AddUserRequest request, @RequestHeader("Authorization") String token) {
        String requesterEmail = jwtService.extractUsername(token.substring(7));
        try {
            chatService.addUserToRoomById(roomId, request.getUserIdToAdd(), requesterEmail);
            return ResponseEntity.ok("User added successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Chat Room not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{roomId}/participants/{userIdToRemove}")
    public ResponseEntity<Void> removeUserFromRoom(@PathVariable Integer roomId, @PathVariable Integer userIdToRemove, @RequestHeader("Authorization") String token) {
        String requesterEmail = jwtService.extractUsername(token.substring(7));
        try {
            chatService.removeUserFromRoom(roomId, userIdToRemove, requesterEmail);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(@RequestHeader("Authorization") String token) {
        String userEmail = jwtService.extractUsername(token.substring(7));
        List<ChatRoom> rooms = chatService.getUserChatRooms(userEmail);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getInitialMessages(@PathVariable Integer roomId) {
        List<ChatMessage> messages = chatService.getLatestMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoom> getRoomDetails(@PathVariable Integer roomId) {
        ChatRoom room = chatService.findRoomById(roomId);
        return ResponseEntity.ok(room);
    }
}