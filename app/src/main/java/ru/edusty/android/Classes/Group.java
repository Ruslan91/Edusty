package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 26.07.2014.
 */
public class Group {
    UUID Id;
    String Title;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
