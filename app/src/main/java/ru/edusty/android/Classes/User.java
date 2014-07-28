package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 22.07.2014.
 */
public class User {
    UUID TokenID;
    UUID GroupID;

    public User(UUID tokenID, UUID groupID) {
        TokenID = tokenID;
        GroupID = groupID;
    }
}
