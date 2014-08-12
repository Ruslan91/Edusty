package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 22.07.2014.
 */
public class User {

    int VkontakteID;
    String FirstName;
    String LastName;
    String PictureUrl;
    String UniversityTitle;
    String GroupTitle;

    public String getGroupTitle() {
        return GroupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        GroupTitle = groupTitle;
    }

    public String getUniversityTitle() {
        return UniversityTitle;
    }

    public void setUniversityTitle(String universityTitle) {
        UniversityTitle = universityTitle;
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

    public String getPictureUrl() {
        return PictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        PictureUrl = pictureUrl;
    }
}
