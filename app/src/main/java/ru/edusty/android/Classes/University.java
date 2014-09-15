package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 25.07.2014.
 */
public class University {
    UUID ID;
    String Title;
    String Country;
    String City;

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

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
