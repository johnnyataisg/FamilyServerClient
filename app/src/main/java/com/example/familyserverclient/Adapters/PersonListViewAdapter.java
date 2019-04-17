package com.example.familyserverclient.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familyserverclient.R;
import com.example.familyserverclient.Results.PersonResult;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;

public class PersonListViewAdapter extends BaseAdapter
{
    private Context context; //context
    private ArrayList<PersonResult> items; //data source of the list adapter

    //public constructor
    public PersonListViewAdapter(Context context, ArrayList<PersonResult> items)
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
            convertView = LayoutInflater.from(context).inflate(R.layout.child, parent, false);
        }

        PersonResult currentItem = (PersonResult) getItem(position);

        TextView textViewItemName = (TextView) convertView.findViewById(R.id.child_text);
        textViewItemName.setText(currentItem.getFirstName() + " " + currentItem.getLastName());

        ImageView icon = convertView.findViewById(R.id.listIcon);
        Iconify.with(new FontAwesomeModule());
        Drawable genderIcon = null;
        if (currentItem.getGender().equals("m"))
        {
            genderIcon = new IconDrawable(this.context, FontAwesomeIcons.fa_male).sizeDp(25);
        }
        else
        {
            genderIcon = new IconDrawable(this.context, FontAwesomeIcons.fa_female).sizeDp(25);
        }
        icon.setImageDrawable(genderIcon);

        return convertView;
    }
}
