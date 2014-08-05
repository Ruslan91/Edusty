package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 22.07.2014.
 */
public class User {

    int VkontakteID;
    String FirstName;
    String LastName;
    UUID PictureID;

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
}
