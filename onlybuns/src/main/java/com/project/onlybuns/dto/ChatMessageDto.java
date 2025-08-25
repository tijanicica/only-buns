package com.project.onlybuns.dto;

public class ChatMessageDto {
    private String content;
    private Integer chatRoomId;

   public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Integer chatRoomId) { this.chatRoomId = chatRoomId; }
}