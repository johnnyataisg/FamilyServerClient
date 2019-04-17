package com.example.familyserverclient.Models;

public class EventExpandable
{
    private Event event;
    private String personName;

    public EventExpandable(Event event, String personName)
    {
        this.event = event;
        this.personName = personName;
    }

    public Event getEvent()
    {
        return this.event;
    }

    public String getPersonName()
    {
        return this.personName;
    }
}
