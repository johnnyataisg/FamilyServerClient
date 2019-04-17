package com.example.familyserverclient.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familyserverclient.Models.Event;
import com.example.familyserverclient.Models.EventExpandable;
import com.example.familyserverclient.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;

public class EventListViewAdapter extends BaseAdapter
{
    private Context context; //context
    private ArrayList<EventExpandable> items; //data source of the list adapter

    //public constructor
    public EventListViewAdapter(Context context, ArrayList<EventExpandable> items)
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.child, parent, false);
        }

        EventExpandable currentItem = (EventExpandable) getItem(position);

        TextView textViewItemName = (TextView) convertView.findViewById(R.id.child_text);
        Event event = currentItem.getEvent();
        textViewItemName.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")" + "\n" + currentItem.getPersonName());

        ImageView icon = convertView.findViewById(R.id.listIcon);
        Iconify.with(new FontAwesomeModule());
        Drawable eventIcon = null;
        eventIcon = new IconDrawable(this.context, FontAwesomeIcons.fa_map_marker).sizeDp(25);
        icon.setImageDrawable(eventIcon);

        return convertView;
    }
}