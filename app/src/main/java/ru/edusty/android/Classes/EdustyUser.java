package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 13.10.2014.
 */
public class EdustyUser {
    UUID TokenID;
    int NewUser;
    UUID UserID;

    public EdustyUser(UUID tokenID, int newUser, UUID userID) {
        TokenID = tokenID;
        NewUser = newUser;
        UserID = userID;
    }

    public UUID getTokenID() {
        return TokenID;
    }

    public void setTokenID(UUID tokenID) {
        TokenID = tokenID;
    }

    public int getNewUser() {
        return NewUser;
    }

    public void setNewUser(int newUser) {
        NewUser = newUser;
    }

    public UUID getUserID() {
        return UserID;
    }

    public void setUserID(UUID userID) {
        UserID = userID;
    }
}
