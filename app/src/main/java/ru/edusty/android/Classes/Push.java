package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 08.08.2014.
 */
public class Push {
    UUID TokenID;
    String PushCode;
    int ClientOS;
    String DeviceID;

    public Push(UUID tokenID, String pushCode, int clientOS, String deviceID) {
        TokenID = tokenID;
        PushCode = pushCode;
        ClientOS = clientOS;
        DeviceID = deviceID;
    }
}
