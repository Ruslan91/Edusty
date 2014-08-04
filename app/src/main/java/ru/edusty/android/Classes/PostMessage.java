package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 04.08.2014.
 */
public class PostMessage {
    UUID TokenID;
    String Message;

    public PostMessage(UUID tokenID, String message) {
        this.TokenID = tokenID;
        this.Message = message;
    }
}
