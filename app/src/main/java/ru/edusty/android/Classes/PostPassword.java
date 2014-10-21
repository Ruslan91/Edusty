package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 21.10.2014.
 */
public class PostPassword {
    UUID TokenID;
    String OldPassword;
    String NewPassword;

    public PostPassword(UUID tokenID, String oldPassword, String newPassword) {
        TokenID = tokenID;
        OldPassword = oldPassword;
        NewPassword = newPassword;
    }
}
