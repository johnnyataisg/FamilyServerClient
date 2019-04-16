package com.example.familyserverclient;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.familyserverclient.Models.HttpClient;
import com.example.familyserverclient.Models.LoginResult;
import com.example.familyserverclient.Results.EventRelatedResult;
import com.example.familyserverclient.Results.PersonResult;
import com.google.gson.Gson;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity
{
    private LoginResult loginResultObject;
    private String serverHost = "192.168.56.1";
    private PersonResult selectedPersonObject;
    private TextView firstName;
    private TextView lastName;
    private TextView gender;
    private ExpandableListView eventList;

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

        this.firstName = (TextView)findViewById(R.id.firstName);
        this.lastName = (TextView)findViewById(R.id.lastName);
        this.gender = (TextView)findViewById(R.id.gender);
        this.eventList = (ExpandableListView)findViewById(R.id.eventList);

        updateFields();
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
