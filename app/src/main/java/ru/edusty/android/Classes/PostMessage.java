package ru.edusty.android.Classes;

import java.util.List;
import java.util.UUID;

/**
 * Created by Руслан on 04.08.2014.
 */
public class PostMessage {
    UUID TokenID;
    String Message;
    List<UUID> Files;

    public PostMessage(UUID tokenID, String message, List<UUID> files) {
        TokenID = tokenID;
        Message = message;
        Files = files;
    }
}
