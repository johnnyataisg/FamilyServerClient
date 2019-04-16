package com.example.familyserverclient.Results;

import com.example.familyserverclient.Models.Event;
import java.util.List;

public class EventAllResult
{
    private List<Event> data;
    private String message;

    public EventAllResult(List<Event> data)
    {
        this.data = data;
    }

    public EventAllResult(String msg)
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
