package ru.edusty.android.Classes;

import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by Руслан on 20.10.2014.
 */
public class PutUser {
    UUID TokenID;
    String FirstName;
    String LastName;
    String PictureUrl;
    String EMail;

    public PutUser(UUID tokenID, String firstName, String lastName, String pictureUrl, String email) {
        TokenID = tokenID;
        FirstName = firstName;
        LastName = lastName;
        PictureUrl = pictureUrl;
        EMail = email;
    }
}
