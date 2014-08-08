package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 08.08.2014.
 */
public class Push {
    UUID TokenID;
    String PushCode;
    int ClientOS;

    public Push(UUID tokenID, String pushCode, int clientOS) {
        TokenID = tokenID;
        PushCode = pushCode;
        ClientOS = clientOS;
    }
}
