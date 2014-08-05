package ru.edusty.android.Classes;

/**
 * Created by Руслан on 05.08.2014.
 */
public class Group {

    String Title;
    User[] Members;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public User[] getUsers() {
        return Members;
    }

    public void setUsers(User[] users) {
        this.Members = users;
    }
}
