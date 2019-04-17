package com.example.familyserverclient.Adapters;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.familyserverclient.Models.PersonExpandable;
import com.example.familyserverclient.R;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import java.util.HashMap;
import java.util.List;

public class FamilyListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<PersonExpandable>> expandableListDetail;

    public FamilyListAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<PersonExpandable>> expandableListDetail)
    {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public PersonExpandable getChild(int listPosition, int expandedListPosition)
    {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition)
    {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        PersonExpandable person = getChild(listPosition, expandedListPosition);
        final String expandedListText = person.getPerson().getFirstName() + " " + person.getPerson().getLastName() + "\n" + person.getRelationship();
        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.child_text);
        expandedListTextView.setText(expandedListText);

        ImageView icon = convertView.findViewById(R.id.listIcon);
        Iconify.with(new FontAwesomeModule());
        Drawable genderIcon = null;
        if (person.getPerson().getGender().equals("m"))
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

    @Override
    public int getChildrenCount(int listPosition)
    {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition)
    {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition)
    {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.text);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition)
    {
        return true;
    }
}