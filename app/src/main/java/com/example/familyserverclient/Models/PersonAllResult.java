package com.example.familyserverclient.Models;

import java.util.List;

public class PersonAllResult
{
    private List<Person> data;
    private String message;

    public PersonAllResult(List<Person> data)
    {
        this.data = data;
    }

    public PersonAllResult(String msg)
    {
        this.message = msg;
    }

    public List<Person> getData()
    {
        return this.data;
    }

    public String getMessage()
    {
        return this.message;
    }
}
