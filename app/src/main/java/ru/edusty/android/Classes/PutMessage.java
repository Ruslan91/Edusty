package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 15.08.2014.
 */
public class PutMessage {
    UUID TokenID;
    String Message;
    UUID MessageID;

    public PutMessage(UUID tokenID, String message, UUID messageID) {
        TokenID = tokenID;
        Message = message;
        MessageID = messageID;
    }
}
