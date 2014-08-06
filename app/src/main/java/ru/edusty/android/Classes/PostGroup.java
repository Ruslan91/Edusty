package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 05.08.2014.
 */
public class PostGroup {
    UUID TokenID;
    String Title;

    public PostGroup(UUID tokenID, String title) {
        TokenID = tokenID;
        Title = title;
    }
}
