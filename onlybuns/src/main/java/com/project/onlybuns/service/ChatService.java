// u service/ChatService.java
package com.project.onlybuns.service;

import com.project.onlybuns.dto.ChatMessageDto;
import com.project.onlybuns.model.ChatMessage;
import com.project.onlybuns.model.ChatRoom;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.ChatMessageRepository;
import com.project.onlybuns.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RegisteredUserService registeredUserService;

    @Autowired
    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, RegisteredUserService registeredUserService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.registeredUserService = registeredUserService;
    }

    /**
     * Kreira novu grupnu sobu.
     * @param roomName Ime sobe.
     * @param adminEmail Email korisnika koji kreira sobu (on postaje admin).
     * @return Kreirana ChatRoom.
     */
    @Transactional
    public ChatRoom createRoom(String roomName, String adminEmail) {
        RegisteredUser admin = registeredUserService.findByEmail(adminEmail);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom.setAdmin(admin);
        chatRoom.setGroupChat(true);


        Set<RegisteredUser> participants = new HashSet<>();
        participants.add(admin);
        chatRoom.setParticipants(participants);

        return chatRoomRepository.save(chatRoom);
    }

    /**
     * Dodaje korisnika u postojeću sobu. Samo admin sobe može dodati novog korisnika.
     * @param roomId ID sobe.
     * @param requesterEmail Email korisnika koji pokušava da doda novog člana (mora biti admin).
     */

    @Transactional
    public void addUserToRoom(Integer roomId, String usernameToAdd, String requesterEmail) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Soba za čet nije pronađena."));

        if (!room.getAdmin().getEmail().equals(requesterEmail)) {
            throw new SecurityException("Samo admin može dodavati korisnike.");
        }

        RegisteredUser userToAdd = registeredUserService.findByUsername(usernameToAdd);

        if (userToAdd.isAdmin()) {
            throw new IllegalArgumentException("Administratori ne mogu biti dodati u grupe.");
        }

        // NOVI DEO: Provera da li je korisnik već član
        if (room.getParticipants().contains(userToAdd)) {
            throw new IllegalArgumentException("Korisnik je već član ove grupe.");
        }

        room.getParticipants().add(userToAdd);

        chatRoomRepository.save(room);
    }

    /**
     * Uklanja korisnika iz sobe. Samo admin sobe može ukloniti korisnika.
     * @param roomId ID sobe.
     * @param userIdToRemove ID korisnika kojeg treba ukloniti.
     * @param requesterEmail Email korisnika koji pokušava da ukloni člana (mora biti admin).
     */
    @Transactional
    public void removeUserFromRoom(Integer roomId, Integer userIdToRemove, String requesterEmail) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Provera da li je onaj ko šalje zahtev zaista admin te sobe
        if (!room.getAdmin().getEmail().equals(requesterEmail)) {
            throw new SecurityException("Only the admin can remove users from this room.");
        }

        // Admin ne može ukloniti samog sebe
        if (room.getAdmin().getId().equals(userIdToRemove)) {
            throw new IllegalArgumentException("Admin cannot be removed from the chat room.");
        }

        room.getParticipants().removeIf(participant -> participant.getId().equals(userIdToRemove));

        chatRoomRepository.save(room);
    }

    /**
     * Čuva novu poruku u bazi.
     * @return Sačuvana poruka sa generisanim ID-jem i vremenom.
     */
    @Transactional
    public ChatMessage processAndSaveMessage(ChatMessageDto dto, String senderEmail) {
        RegisteredUser sender = registeredUserService.findByEmail(senderEmail);
        ChatRoom room = chatRoomRepository.findById(dto.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        ChatMessage message = new ChatMessage();
        message.setContent(dto.getContent());
        message.setChatRoom(room);
        message.setSender(sender);
        message.setSentAt(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getLatestMessages(Integer roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        List<ChatMessage> latestMessages = chatMessageRepository.findTop10ByChatRoomOrderBySentAtDesc(room);

        Collections.reverse(latestMessages);

        return latestMessages;
    }

    public ChatRoom findRoomById(Integer roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }

    public List<ChatRoom> getUserChatRooms(String userEmail) {
        RegisteredUser user = registeredUserService.findByEmail(userEmail);
        return chatRoomRepository.findChatRoomsByParticipantId(user.getId());
    }

    @Transactional
    public void addUserToRoomById(Integer roomId, Integer userIdToAdd, String requesterEmail) { // <-- Prima ID
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Soba za čet nije pronađena."));

        if (!room.getAdmin().getEmail().equals(requesterEmail)) {
            throw new SecurityException("Samo admin može dodavati korisnike.");
        }

        RegisteredUser userToAdd = registeredUserService.findById(userIdToAdd); // <-- Promena

        if (userToAdd.isAdmin()) {
            throw new IllegalArgumentException("Administratori ne mogu biti dodati u grupe.");
        }

        if (room.getParticipants().contains(userToAdd)) {
            throw new IllegalArgumentException("Korisnik je već član ove grupe.");
        }

        room.getParticipants().add(userToAdd);

        chatRoomRepository.save(room);
    }

    @Transactional
    public ChatRoom getOrCreatePrivateChat(String user1Email, String user2Username) {
        RegisteredUser user1 = registeredUserService.findByEmail(user1Email);
        RegisteredUser user2 = registeredUserService.findByEmail(user2Username);

        if (user1.getId().equals(user2.getId())) {
            throw new IllegalArgumentException("Cannot create a chat with yourself.");
        }

        return chatRoomRepository.findPrivateChatRoomBetweenUsers(user1.getId(), user2.getId())
                .orElseGet(() -> {
                    ChatRoom privateRoom = new ChatRoom();
                    privateRoom.setName(user1.getUsername() + " - " + user2.getUsername());
                    privateRoom.setGroupChat(false);

                    Set<RegisteredUser> participants = new HashSet<>();
                    participants.add(user1);
                    participants.add(user2);
                    privateRoom.setParticipants(participants);


                    return chatRoomRepository.save(privateRoom);
                });
    }
}