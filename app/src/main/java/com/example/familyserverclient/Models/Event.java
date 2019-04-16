package com.example.familyserverclient.Models;

public class Event
{
    private String eventID;
    private String descendant;
    private String personID;
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    public Event() {}

    public Event(String eventID, String descendant, String person, double latitude, double longitude, String country, String city, String eventType, int year)
    {
        this.eventID = eventID;
        this.descendant = descendant;
        this.personID = person;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public String getEventID()
    {
        return this.eventID;
    }

    public String getDescendant()
    {
        return this.descendant;
    }

    public String getPerson()
    {
        return this.personID;
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    public String getCountry()
    {
        return this.country;
    }

    public String getCity()
    {
        return this.city;
    }

    public String getEventType()
    {
        return this.eventType;
    }

    public int getYear()
    {
        return this.year;
    }
}
