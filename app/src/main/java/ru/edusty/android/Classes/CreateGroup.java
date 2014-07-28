package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 28.07.2014.
 */
public class CreateGroup {
    String Title;
    UUID TokenID;
    UUID UniversityID;

    public CreateGroup(String title, UUID tokenID, UUID universityID) {
        Title = title;
        TokenID = tokenID;
        UniversityID = universityID;
    }
}
