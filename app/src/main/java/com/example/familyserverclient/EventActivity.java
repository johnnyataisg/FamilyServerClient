package com.example.familyserverclient;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.familyserverclient.Adapters.HttpClient;
import com.example.familyserverclient.Fragments.MapFragment;
import com.example.familyserverclient.Models.Event;
import com.example.familyserverclient.Results.EventAllResult;
import com.example.familyserverclient.Results.EventRelatedResult;
import com.example.familyserverclient.Results.LoginResult;
import com.example.familyserverclient.Results.PersonResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private String serverHost = "192.168.56.1";
    private LoginResult loginResultObject;
    private EventAllResult eventAllTaskResult;
    private Event initialEvent;
    private PersonResult selectedPerson;
    private GoogleMap mMap;
    private ImageView icon;
    private TextView eventPersonName;
    private TextView eventType;
    private LinearLayout eventSnapshot;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                startActivity(intent);
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
        setContentView(R.layout.activity_event);
        Iconify.with(new FontAwesomeModule());

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String loginExtra = getIntent().getStringExtra("LOGIN_OBJECT");
        this.loginResultObject = new Gson().fromJson(loginExtra, LoginResult.class);
        String selectedEventExtra = getIntent().getStringExtra("SELECTED_EVENT");
        this.initialEvent = new Gson().fromJson(selectedEventExtra, Event.class);
        String eventAllResultExtra = getIntent().getStringExtra("EVENT_ALL_RESULT");
        this.eventAllTaskResult = new Gson().fromJson(eventAllResultExtra, EventAllResult.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        this.eventPersonName = (TextView)findViewById(R.id.eventPersonName);
        this.eventType = (TextView)findViewById(R.id.eventType);
        this.eventSnapshot = (LinearLayout)findViewById(R.id.eventSnapshot);
        this.icon = (ImageView)findViewById(R.id.icon);

        eventSnapshot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (!eventType.getText().toString().equals("Click on a marker to see event details"))
                {
                    String loginObjectJSON = new Gson().toJson(loginResultObject);
                    String selectedPersonJSON = new Gson().toJson(selectedPerson);
                    String eventAllResultJSON = new Gson().toJson(eventAllTaskResult);

                    Intent intent = new Intent(EventActivity.this, PersonActivity.class);
                    intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
                    intent.putExtra("SELECTED_PERSON", selectedPersonJSON);
                    intent.putExtra("EVENT_ALL_RESULT", eventAllResultJSON);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.mMap = googleMap;
        for (Event event : this.eventAllTaskResult.getData())
        {
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = null;
            if (event.getEventType().equals("Birth"))
            {
                marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }
            else if (event.getEventType().equals("College Graduation"))
            {
                marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
            else if (event.getEventID().equals("Marriage"))
            {
                marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            }
            else
            {
                marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            }
            marker.setTag(event);
        }

        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(this.initialEvent.getLatitude(), this.initialEvent.getLongitude())));
        PersonResult initialPerson = sendPersonRequest(this.initialEvent.getPerson(), this.loginResultObject.getAuthToken());
        this.selectedPerson = initialPerson;
        eventPersonName.setText(initialPerson.getFirstName() + " " + initialPerson.getLastName());
        eventType.setText(this.initialEvent.getEventType() + ": " + this.initialEvent.getCity() + ", " + this.initialEvent.getCountry() + " (" + this.initialEvent.getYear() + ")");
        Drawable genderIcon = null;
        if (this.selectedPerson.getGender().equals("m"))
        {
            genderIcon = new IconDrawable(EventActivity.this, FontAwesomeIcons.fa_male).sizeDp(50);
        }
        else
        {
            genderIcon = new IconDrawable(EventActivity.this, FontAwesomeIcons.fa_female).sizeDp(50);
        }
        icon.setImageDrawable(genderIcon);

        this.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Polyline spouseLine;
            List<Polyline> ancestorLines = new ArrayList<>();
            List<Polyline> lifeLines = new ArrayList<>();
            public boolean onMarkerClick(Marker marker)
            {
                removeAncestorLines(ancestorLines);
                removeLifeLines(lifeLines);
                if (spouseLine != null)
                {
                    spouseLine.remove();
                }

                Event event = (Event)marker.getTag();
                String token = loginResultObject.getAuthToken();
                PersonResult result = sendPersonRequest(event.getPerson(), token);
                selectedPerson = result;
                if (result != null)
                {
                    Drawable genderIcon = null;
                    if (result.getGender().equals("m"))
                    {
                        genderIcon = new IconDrawable(EventActivity.this, FontAwesomeIcons.fa_male).sizeDp(50);
                    }
                    else
                    {
                        genderIcon = new IconDrawable(EventActivity.this, FontAwesomeIcons.fa_female).sizeDp(50);
                    }
                    icon.setImageDrawable(genderIcon);
                    eventPersonName.setText(result.getFirstName() + " " + result.getLastName());
                    eventType.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
                }

                String spouse = result.getSpouse();
                if (spouse != null)
                {
                    Event birth = findBirthEvent(spouse);
                    spouseLine = mMap.addPolyline(new PolylineOptions().add(new LatLng(event.getLatitude(), event.getLongitude()), new LatLng(birth.getLatitude(), birth.getLongitude())).width(5).color(Color.RED));
                }
                drawLifeLines(result, lifeLines);
                String father = result.getFather();
                String mother = result.getMother();
                drawAncestorLines(father, mother, event, ancestorLines, 10);

                return true;
            }
        });
    }

    public void removeLifeLines(List<Polyline> lifeLines)
    {
        if (lifeLines.size() > 0)
        {
            for (Polyline line : lifeLines)
            {
                line.remove();
            }
            lifeLines.clear();
        }
    }

    public void drawLifeLines(PersonResult person, List<Polyline> lifeLines)
    {
        EventRelatedResult events = sendRelatedEventRequest(person.getPersonID(), this.loginResultObject.getAuthToken());
        for (int i = 1; i < events.getData().size(); i++)
        {
            Event event1 = events.getData().get(i - 1);
            Event event2 = events.getData().get(i);
            PolylineOptions option = new PolylineOptions().add(new LatLng(event1.getLatitude(), event1.getLongitude()), new LatLng(event2.getLatitude(), event2.getLongitude())).width(5).color(Color.YELLOW);
            Polyline line = mMap.addPolyline(option);
            lifeLines.add(line);
        }
    }

    public void removeAncestorLines(List<Polyline> ancestorLines)
    {
        if (ancestorLines.size() > 0)
        {
            for (Polyline line : ancestorLines)
            {
                line.remove();
            }
            ancestorLines.clear();
        }
    }

    public void drawAncestorLines(String fatherID, String motherID, Event currentEvent, List<Polyline> ancestorLines, int width)
    {
        if (fatherID != null)
        {
            Event birth = findBirthEvent(fatherID);
            PolylineOptions option = new PolylineOptions().add(new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude()), new LatLng(birth.getLatitude(), birth.getLongitude())).width(width).color(Color.GREEN);
            Polyline line = mMap.addPolyline(option);
            ancestorLines.add(line);

            PersonResult fatherObject = sendPersonRequest(fatherID, this.loginResultObject.getAuthToken());
            drawAncestorLines(fatherObject.getFather(), fatherObject.getMother(), birth, ancestorLines, width - 2);
        }
        if (motherID != null)
        {
            Event birth = findBirthEvent(motherID);
            PolylineOptions option = new PolylineOptions().add(new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude()), new LatLng(birth.getLatitude(), birth.getLongitude())).width(width).color(Color.GREEN);
            Polyline line = mMap.addPolyline(option);
            ancestorLines.add(line);

            PersonResult motherObject = sendPersonRequest(motherID, this.loginResultObject.getAuthToken());
            drawAncestorLines(motherObject.getFather(), motherObject.getMother(), birth, ancestorLines, width - 2);
        }
    }

    public Event findBirthEvent(String personID)
    {
        for (Event event : this.eventAllTaskResult.getData())
        {
            if (event.getEventType().equals("Birth") && event.getPerson().equals(personID))
            {
                return event;
            }
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
                Toast.makeText(EventActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(EventActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
        }
        return null;
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
                Toast.makeText(EventActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(EventActivity.this, "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EventActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EventActivity.this, "Event all request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (result.getData() == null)
                {
                    Toast.makeText(EventActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
