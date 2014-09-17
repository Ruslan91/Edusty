package ru.edusty.android.Classes;

import java.util.List;
import java.util.UUID;

/**
 * Created by Руслан on 13.09.2014.
 */
public class Message {
    UUID MessageID;
    String Message;
    String MessageDate;
    UUID SenderID;
    User User;
    List<UUID> Files;

    public List<UUID> getFiles() {
        return Files;
    }

    public void setFiles(List<UUID> files) {
        Files = files;
    }

    public UUID getMessageID() {
        return MessageID;
    }

    public void setMessageID(UUID messageID) {
        MessageID = messageID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessageDate() {
        return MessageDate;
    }

    public void setMessageDate(String messageDate) {
        MessageDate = messageDate;
    }

    public UUID getSenderID() {
        return SenderID;
    }

    public void setSenderID(UUID senderID) {
        SenderID = senderID;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }
}
