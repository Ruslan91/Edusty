package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 25.07.2014.
 */
public class University {
    UUID ID;
    String Title;

    public UUID getID() {
        return ID;
    }

    public void setID(UUID id) {
        ID = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
