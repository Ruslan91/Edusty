package ru.edusty.android.Classes;

import java.util.UUID;

/**
 * Created by Руслан on 13.10.2014.
 */
public class EdustyAuthUser {
    String Login;
    String Password;

    public EdustyAuthUser(String login, String password) {
        Login = login;
        Password = password;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

