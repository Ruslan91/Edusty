package ru.edusty.android.Classes;

import java.util.List;
import java.util.UUID;

/**
 * Created by Руслан on 15.08.2014.
 */
public class PutMessage {
    UUID TokenID;
    String Message;
    UUID MessageID;
    List<UUID> Files;

    public PutMessage(UUID tokenID, String message, UUID messageID, List<UUID> files) {
        TokenID = tokenID;
        Message = message;
        MessageID = messageID;
        Files = files;
    }
}
