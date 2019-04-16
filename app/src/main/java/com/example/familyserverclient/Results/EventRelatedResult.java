package com.example.familyserverclient.Results;

import com.example.familyserverclient.Models.Event;
import java.util.List;

public class EventRelatedResult
{
    private List<Event> data;
    private String message;

    public EventRelatedResult(List<Event> data)
    {
        this.data = data;
    }

    public EventRelatedResult(String msg)
    {
        this.message = msg;
    }

    public List<Event> getData()
    {
        return this.data;
    }

    public String getMessage()
    {
        return this.message;
    }
}
