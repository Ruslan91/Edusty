package ru.edusty.android.Classes;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Руслан on 04.08.2014.
 */
public class Feed implements Serializable{
    UUID MessageID;
    String Message;
    String MessageDate;
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

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }
}
