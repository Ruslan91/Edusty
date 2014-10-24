package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 24.10.2014.
 */
public class SuggestedGroups {
    UUID ID;
    String GroupTitle;
    String UniversityTitle;
    String City;
    String Country;
    Integer MembersCount;

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public String getGroupTitle() {
        return GroupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        GroupTitle = groupTitle;
    }

    public String getUniversityTitle() {
        return UniversityTitle;
    }

    public void setUniversityTitle(String universityTitle) {
        UniversityTitle = universityTitle;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public Integer getMembersCount() {
        return MembersCount;
    }

    public void setMembersCount(Integer membersCount) {
        MembersCount = membersCount;
    }
}
