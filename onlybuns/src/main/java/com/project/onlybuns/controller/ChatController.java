package com.project.onlybuns.controller;

import com.project.onlybuns.dto.ChatMessageDto;
import com.project.onlybuns.model.ChatMessage;
import com.project.onlybuns.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto messageDto, Principal principal) {
        System.out.println("Primljen DTO sa sadržajem: " + messageDto.getContent());

        String senderEmail = principal.getName();

        ChatMessage savedMessage = chatService.processAndSaveMessage(messageDto, senderEmail);

        messagingTemplate.convertAndSend("/topic/room/" + savedMessage.getChatRoom().getId(), savedMessage);
    }
}