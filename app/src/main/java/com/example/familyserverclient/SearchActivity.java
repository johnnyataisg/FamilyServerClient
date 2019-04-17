package com.example.familyserverclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.familyserverclient.Adapters.EventListViewAdapter;
import com.example.familyserverclient.Adapters.HttpClient;
import com.example.familyserverclient.Adapters.PersonListViewAdapter;
import com.example.familyserverclient.Models.Event;
import com.example.familyserverclient.Models.EventExpandable;
import com.example.familyserverclient.Results.EventAllResult;
import com.example.familyserverclient.Results.LoginResult;
import com.example.familyserverclient.Results.PersonResult;
import com.google.gson.Gson;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class SearchActivity extends AppCompatActivity
{
    private String serverHost = "192.168.56.1";
    private LoginResult loginObject;
    private EventAllResult eventAllResult;
    private ArrayList<PersonResult> personResultList = new ArrayList<>();
    private SearchView searchBar;
    private ListView personList;
    private ListView eventList;

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
        setContentView(R.layout.activity_search);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.searchBar = (SearchView)findViewById(R.id.searchBar);
        this.personList = (ListView)findViewById(R.id.personList);
        this.eventList = (ListView)findViewById(R.id.eventList);

        String eventAllResultJSON = getIntent().getStringExtra("EVENT_LIST");
        this.eventAllResult = new Gson().fromJson(eventAllResultJSON, EventAllResult.class);
        final String loginObjectJSON = getIntent().getStringExtra("LOGIN_OBJECT");
        this.loginObject = new Gson().fromJson(loginObjectJSON, LoginResult.class);

        HashSet<String> personIDSet = new HashSet<>();
        for (Event event : this.eventAllResult.getData())
        {
            personIDSet.add(event.getPerson());
        }
        for (String id : personIDSet)
        {
            PersonResult result = sendPersonRequest(id, this.loginObject.getAuthToken());
            this.personResultList.add(result);
        }


        this.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text)
            {
                String query = text.toLowerCase();
                ArrayList<EventExpandable> eventQueryResult = new ArrayList<>();
                ArrayList<PersonResult> personQueryResult = new ArrayList<>();

                for (Event event : eventAllResult.getData())
                {
                    if (event.getEventType().toLowerCase().contains(query) ||
                            event.getCity().toLowerCase().contains(query) ||
                            event.getCountry().toLowerCase().contains(query) ||
                            String.valueOf(event.getYear()).toLowerCase().contains(query))
                    {
                        PersonResult person = sendPersonRequest(event.getPerson(), loginObject.getAuthToken());
                        EventExpandable eventExpandable = new EventExpandable(event, person.getFirstName() + " " + person.getLastName());
                        eventQueryResult.add(eventExpandable);
                    }
                }
                for (PersonResult person : personResultList)
                {
                    if (person.getFirstName().toLowerCase().contains(query) || person.getLastName().toLowerCase().contains(query))
                    {
                        personQueryResult.add(person);
                    }
                }
                EventListViewAdapter adapter = new EventListViewAdapter(SearchActivity.this, eventQueryResult);
                eventList.setAdapter(adapter);

                PersonListViewAdapter adapter2 = new PersonListViewAdapter(SearchActivity.this, personQueryResult);
                personList.setAdapter(adapter2);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        this.eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Event event = ((EventExpandable)eventList.getAdapter().getItem(position)).getEvent();
                String selectedEventJSON = new Gson().toJson(event);
                String loginObjectJSON = new Gson().toJson(loginObject);
                String eventAllResultJSON = new Gson().toJson(eventAllResult);

                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
                intent.putExtra("SELECTED_EVENT", selectedEventJSON);
                intent.putExtra("EVENT_ALL_RESULT", eventAllResultJSON);
                startActivityForResult(intent, 0);
            }
        });

        this.personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                PersonResult selectedPerson = (PersonResult)personList.getAdapter().getItem(position);
                String loginObjectJSON = new Gson().toJson(loginObject);
                String selectedPersonJSON = new Gson().toJson(selectedPerson);
                String eventAllResultJSON = new Gson().toJson(eventAllResult);

                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
                intent.putExtra("SELECTED_PERSON", selectedPersonJSON);
                intent.putExtra("EVENT_ALL_RESULT", eventAllResultJSON);
                startActivity(intent);
            }
        });
    }

    public PersonResult sendPersonRequest(String personID, String auth_token)
    {
        try
        {
            PersonTask task = new PersonTask();
            String url = "http://" + this.serverHost + ":8080/person/" + personID;
            ArrayList params = new ArrayList(2);
            params.add(new URL(url));
            params.add(auth_token);
            try
            {
                PersonResult result = task.execute(params).get();
                return result;
            }
            catch(Exception e)
            {
                Toast.makeText(SearchActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(SearchActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public class PersonTask extends AsyncTask<ArrayList, Integer, PersonResult>
    {
        protected PersonResult doInBackground(ArrayList... params)
        {
            HttpClient httpClient = new HttpClient();

            String responseMsg = null;
            for (int i = 0; i < params.length; i++)
            {
                responseMsg = httpClient.getRequest((URL)params[i].get(0), (String)params[i].get(1));
            }
            return new Gson().fromJson(responseMsg, PersonResult.class);
        }
        protected void onPostExecute(PersonResult result)
        {
            if (result.getFirstName() == null)
            {
                Toast.makeText(SearchActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
