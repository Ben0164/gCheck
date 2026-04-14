package com.example.myapplication.feature.collaboration;

public class ChatMessageModel {
    private String messageText;
    private boolean isSentByUser;
    private String timestamp;

    public ChatMessageModel(String messageText, boolean isSentByUser, String timestamp) {
        this.messageText = messageText;
        this.isSentByUser = isSentByUser;
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
