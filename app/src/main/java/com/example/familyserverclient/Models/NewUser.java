package com.example.familyserverclient.Models;

public class NewUser
{
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    public NewUser(String userName, String password, String firstName, String lastName, String email, String gender)
    {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }
}
