package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 13.09.2014.
 */
public class Comment {
    UUID ID;
    UUID MessageID;
    String Text;
    User User;
    String CommentDate;

    public String getCommentDate() {
        return CommentDate;
    }

    public void setCommentDate(String commentDate) {
        CommentDate = commentDate;
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public UUID getMessageID() {
        return MessageID;
    }

    public void setMessageID(UUID messageID) {
        MessageID = messageID;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }
}
