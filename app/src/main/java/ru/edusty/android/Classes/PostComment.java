package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 13.09.2014.
 */
public class PostComment {
    UUID MessageID;
    String Text;
    UUID TokenID;

    public PostComment(UUID messageID, String text, UUID tokenID) {
        MessageID = messageID;
        Text = text;
        TokenID = tokenID;
    }
}
