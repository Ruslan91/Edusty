package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 26.07.2014.
 */
public class GetGroups {
    UUID ID;
    String Title;
    Integer MembersCount;

    public Integer getMembersCount() {
        return MembersCount;
    }

    public void setMembersCount(Integer membersCount) {
        MembersCount = membersCount;
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
