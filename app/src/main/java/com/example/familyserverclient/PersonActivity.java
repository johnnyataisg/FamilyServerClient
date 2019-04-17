package com.example.familyserverclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.familyserverclient.Adapters.FamilyListAdapter;
import com.example.familyserverclient.Fragments.MapFragment;
import com.example.familyserverclient.Models.Event;
import com.example.familyserverclient.Adapters.EventListAdapter;
import com.example.familyserverclient.Adapters.HttpClient;
import com.example.familyserverclient.Models.PersonExpandable;
import com.example.familyserverclient.Results.EventAllResult;
import com.example.familyserverclient.Results.LoginResult;
import com.example.familyserverclient.Models.Person;
import com.example.familyserverclient.Results.PersonAllResult;
import com.example.familyserverclient.Results.EventRelatedResult;
import com.example.familyserverclient.Results.PersonResult;
import com.google.gson.Gson;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PersonActivity extends AppCompatActivity
{
    private LoginResult loginResultObject;
    private String serverHost = "192.168.56.1";
    private PersonResult selectedPersonObject;
    private EventAllResult eventAllTaskResult;
    private TextView firstName;
    private TextView lastName;
    private TextView gender;
    private ExpandableListView eventList;
    private ExpandableListView familyList;

    public void updateFields()
    {
        this.firstName.setText(this.selectedPersonObject.getFirstName());
        this.lastName.setText(this.selectedPersonObject.getLastName());

        if (this.selectedPersonObject.getGender().equals("m"))
        {
            this.gender.setText("Male");
        }
        else
        {
            this.gender.setText("Female");
        }

        EventRelatedResult relatedEvents = sendRelatedEventRequest(this.selectedPersonObject.getPersonID(), this.loginResultObject.getAuthToken());
        PersonAllResult relatedPeople = sendPersonAllRequest(this.loginResultObject.getAuthToken());
        List<Event> eList = relatedEvents.getData();
        List<PersonExpandable> fList = getRelatedPeople(relatedPeople);

        HashMap<String, List<Event>> eventListData = new HashMap<>();
        eventListData.put("Life Events", eList);

        List<String> eventListTitle = new ArrayList<>(eventListData.keySet());
        EventListAdapter eventListAdapter = new EventListAdapter(this, eventListTitle, eventListData);
        eventListAdapter.setPersonName(this.selectedPersonObject.getFirstName() + " " + this.selectedPersonObject.getLastName());
        this.eventList.setAdapter(eventListAdapter);

        HashMap<String, List<PersonExpandable>> familyListData = new HashMap<>();
        familyListData.put("Family", fList);

        List<String> familyListTitle = new ArrayList<>(familyListData.keySet());
        FamilyListAdapter familyListAdapter = new FamilyListAdapter(this, familyListTitle, familyListData);
        this.familyList.setAdapter(familyListAdapter);
    }

    public List<PersonExpandable> getRelatedPeople(PersonAllResult result)
    {
        List<PersonExpandable> output = new ArrayList<>();
        for (Person person : result.getData())
        {
            String personDetails = null;
            if (this.selectedPersonObject.getPersonID().equals(person.getFather()) || this.selectedPersonObject.getPersonID().equals(person.getMother()))
            {
                output.add(new PersonExpandable(person, "Child"));
            }
            else if (this.selectedPersonObject.getPersonID().equals(person.getSpouse()))
            {
                output.add(new PersonExpandable(person, "Spouse"));
            }
            else if (person.getPersonID().equals(this.selectedPersonObject.getFather()) || person.getPersonID().equals(this.selectedPersonObject.getMother()))
            {
                output.add(new PersonExpandable(person, "Parent"));
            }
        }
        return output;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String loginExtra = getIntent().getStringExtra("LOGIN_OBJECT");
        this.loginResultObject = new Gson().fromJson(loginExtra, LoginResult.class);
        String selectedPersonExtra = getIntent().getStringExtra("SELECTED_PERSON");
        this.selectedPersonObject = new Gson().fromJson(selectedPersonExtra, PersonResult.class);
        String eventAllResultExtra = getIntent().getStringExtra("EVENT_ALL_RESULT");
        this.eventAllTaskResult = new Gson().fromJson(eventAllResultExtra, EventAllResult.class);

        this.firstName = (TextView)findViewById(R.id.firstName);
        this.lastName = (TextView)findViewById(R.id.lastName);
        this.gender = (TextView)findViewById(R.id.gender);
        this.eventList = (ExpandableListView)findViewById(R.id.eventList);
        this.familyList = (ExpandableListView)findViewById(R.id.familyList);

        updateFields();

        this.eventList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                Event event = (Event)eventList.getExpandableListAdapter().getChild(groupPosition, childPosition);
                String selectedEventJSON = new Gson().toJson(event);
                String loginObjectJSON = new Gson().toJson(loginResultObject);
                String eventAllResultJSON = new Gson().toJson(eventAllTaskResult);

                Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
                intent.putExtra("SELECTED_EVENT", selectedEventJSON);
                intent.putExtra("EVENT_ALL_RESULT", eventAllResultJSON);
                startActivityForResult(intent, 0);

                return true;
            }
        });
    }

    public PersonAllResult sendPersonAllRequest(String auth_token)
    {
        try
        {
            PersonAllTask task = new PersonAllTask();
            String url = "http://" + this.serverHost + ":8080/person";
            ArrayList params = new ArrayList(2);
            params.add(new URL(url));
            params.add(auth_token);
            try
            {
                PersonAllResult result = task.execute(params).get();
                return result;
            }
            catch(Exception e)
            {
                Toast.makeText(PersonActivity.this, "Person all request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(PersonActivity.this, "Person all request failed, please check your connection", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public EventRelatedResult sendRelatedEventRequest(String personID, String auth_token)
    {
        try
        {
            EventRelatedTask task = new EventRelatedTask();
            String url = "http://" + this.serverHost + ":8080/event/person/" + personID;
            ArrayList params = new ArrayList(2);
            params.add(new URL(url));
            params.add(auth_token);
            try
            {
                EventRelatedResult result = task.execute(params).get();
                return result;
            }
            catch(Exception e)
            {
                Toast.makeText(PersonActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(PersonActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public class PersonAllTask extends AsyncTask<ArrayList, Integer, PersonAllResult>
    {
        protected PersonAllResult doInBackground(ArrayList... params)
        {
            HttpClient httpClient = new HttpClient();

            String responseMsg = null;
            for (int i = 0; i < params.length; i++)
            {
                responseMsg = httpClient.getRequest((URL)params[i].get(0), (String)params[i].get(1));
            }
            return new Gson().fromJson(responseMsg, PersonAllResult.class);
        }
        protected void onPostExecute(PersonAllResult result)
        {
            if (result == null)
            {
                Toast.makeText(PersonActivity.this, "Person all request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (result.getData() == null)
                {
                    Toast.makeText(PersonActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class EventRelatedTask extends AsyncTask<ArrayList, Integer, EventRelatedResult>
    {
        protected EventRelatedResult doInBackground(ArrayList... params)
        {
            HttpClient httpClient = new HttpClient();

            String responseMsg = null;
            for (int i = 0; i < params.length; i++)
            {
                responseMsg = httpClient.getRequest((URL)params[i].get(0), (String)params[i].get(1));
            }
            return new Gson().fromJson(responseMsg, EventRelatedResult.class);
        }
        protected void onPostExecute(EventRelatedResult result)
        {
            if (result == null)
            {
                Toast.makeText(PersonActivity.this, "Event all request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (result.getData() == null)
                {
                    Toast.makeText(PersonActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
