package ru.edusty.android.Classes;

/**
 * Created by Руслан on 13.10.2014.
 */
public class EdustyRegisterUser {
    String Login;
    String Password;
    String FirstName;
    String LastName;

    public EdustyRegisterUser(String login, String password, String firstName, String lastName) {
        Login = login;
        Password = password;
        FirstName = firstName;
        LastName = lastName;
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

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
