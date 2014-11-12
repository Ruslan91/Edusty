package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 12.11.2014.
 */
public class PostUniversity {
    UUID TokenID;

    String Title;
    String Country;
    String City;

    public PostUniversity(UUID tokenID, String title, String country, String city) {
        TokenID = tokenID;
        Title = title;
        Country = country;
        City = city;
    }
}
