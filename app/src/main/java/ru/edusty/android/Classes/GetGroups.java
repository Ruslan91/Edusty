package ru.edusty.android.Classes;

import java.util.List;
import java.util.UUID;

/**
 * Created by Руслан on 26.07.2014.
 */
public class GetGroups {
    UUID ID;
    String Title;
    List<String>  Members;
    Integer MembersCount;

    public List<String> getMembers() {
        return Members;
    }

    public void setMembers(List<String> members) {
        Members = members;
    }

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
