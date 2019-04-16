package com.example.familyserverclient.Results;

public class PersonResult
{
    private String descendant;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String father;
    private String mother;
    private String spouse;
    private String message;

    public PersonResult(String desc, String perID, String fName, String lName, String gen, String dad, String mom, String spo)
    {
        this.descendant = desc;
        this.personID = perID;
        this.firstName = fName;
        this.lastName = lName;
        this.gender = gen;
        this.father = dad;
        this.mother = mom;
        this.spouse = spo;
    }

    public PersonResult(String msg)
    {
        this.message = msg;
    }

    public String getDescendant()
    {
        return this.descendant;
    }

    public String getPersonID()
    {
        return this.personID;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public String getGender()
    {
        return this.gender;
    }

    public String getFather()
    {
        return this.father;
    }

    public String getMother()
    {
        return this.mother;
    }

    public String getSpouse()
    {
        return this.spouse;
    }

    public String getMessage()
    {
        return this.message;
    }
}
