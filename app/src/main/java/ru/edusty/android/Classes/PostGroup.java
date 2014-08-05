package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 05.08.2014.
 */
public class PostGroup {
    UUID Token;
    String Title;

    public PostGroup(UUID token, String title) {
        Token = token;
        Title = title;
    }
}
