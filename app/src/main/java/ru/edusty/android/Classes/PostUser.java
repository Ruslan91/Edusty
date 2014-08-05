package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 05.08.2014.
 */
public class PostUser {
    UUID TokenID;
    UUID GroupID;

    public PostUser(UUID tokenID, UUID groupID) {
        TokenID = tokenID;
        GroupID = groupID;
    }

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
}
