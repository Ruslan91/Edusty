package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 22.07.2014.
 */
public class User {
    UUID TokenID;
    UUID GroupID;
    int VkontakteID;
    String FirstName;
    String LastName;
    UUID PictureID;

    public UUID getTokenID() {
        return TokenID;
    }

    public void setTokenID(UUID tokenID) {
        TokenID = tokenID;
    }

    public UUID getGroupID() {
        return GroupID;
    }

    public void setGroupID(UUID groupID) {
        GroupID = groupID;
    }

    public int getVkontakteID() {
        return VkontakteID;
    }

    public void setVkontakteID(int vkontakteID) {
        VkontakteID = vkontakteID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public UUID getPictureID() {
        return PictureID;
    }

    public void setPictureID(UUID pictureID) {
        PictureID = pictureID;
    }

    public User(UUID tokenID, UUID groupID) {
        TokenID = tokenID;
        GroupID = groupID;
    }
}
