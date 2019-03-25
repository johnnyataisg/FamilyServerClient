package com.example.familyserverclient.Models;

public class Person
{
    private String personID;
    private String descendant;
    private String firstName;
    private String lastName;
    private String gender;
    private String father;
    private String mother;
    private String spouse;

    public Person(String personID, String descendant, String firstName, String lastName, String gender, String father, String mother, String spouse)
    {
        this.personID = personID;
        this.descendant = descendant;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
    }

    public boolean equals(Object obj_2)
    {
        if (obj_2.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            Person person_2 = (Person)obj_2;

            if (!this.personID.equals(person_2.getPersonID()))
            {
                return false;
            }
            if (!this.descendant.equals(person_2.getDescendant()))
            {
                return false;
            }
            if (!this.firstName.equals(person_2.getFirstName()))
            {
                return false;
            }
            if (!this.lastName.equals(person_2.getLastName()))
            {
                return false;
            }
            if (!this.gender.equals(person_2.getGender()))
            {
                return false;
            }
            if (!this.father.equals(person_2.getFather()))
            {
                return false;
            }
            if (!this.mother.equals(person_2.getMother()))
            {
                return false;
            }
            if (!this.spouse.equals(person_2.getSpouse()))
            {
                return false;
            }
            return true;
        }
    }

    public String getPersonID()
    {
        return this.personID;
    }

    public String getDescendant()
    {
        return this.descendant;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getLastName()
    {
        return this.lastName;
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

    public String getGender()
    {
        return this.gender;
    }

    public void setPersonID()
    {
        //To be implemented
    }

    public void setDescendant()
    {
        //To be implemented
    }

    public void setFirstName()
    {
        //To be implemented
    }

    public void setLastName()
    {
        //To be implemented
    }
    public void setGender()
    {
        //To be implemented
    }

    public void setFather()
    {
        //To be implemented
    }

    public void setMother()
    {
        //To be implemented
    }

    public void setSpouse()
    {
        //To be implemented
    }
}
