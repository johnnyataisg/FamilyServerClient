package com.example.familyserverclient.Models;

public class PersonExpandable
{
    private Person person;
    private String relationship;

    public PersonExpandable(Person person, String relationship)
    {
        this.person = person;
        this.relationship = relationship;
    }

    public Person getPerson()
    {
        return this.person;
    }

    public String getRelationship()
    {
        return this.relationship;
    }
}
