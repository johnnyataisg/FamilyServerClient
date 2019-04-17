package com.example.familyserverclient.Results;

public class LoginResult
{
    private String authToken;
    private String userName;
    private String personID;
    private String message;

    public LoginResult(String token, String user, String perID)
    {
        this.authToken = token;
        this.userName = user;
        this.personID = perID;
    }

    public LoginResult(String msg)
    {
        this.message = msg;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getAuthToken()
    {
        return this.authToken;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public String getPersonID()
    {
        return this.personID;
    }
}
