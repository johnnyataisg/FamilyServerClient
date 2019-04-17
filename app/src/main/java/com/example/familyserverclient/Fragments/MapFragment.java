package com.example.familyserverclient.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.familyserverclient.FilterActivity;
import com.example.familyserverclient.MainActivity;
import com.example.familyserverclient.Models.Event;
import com.example.familyserverclient.Adapters.HttpClient;
import com.example.familyserverclient.Results.EventRelatedResult;
import com.example.familyserverclient.Results.LoginResult;
import com.example.familyserverclient.PersonActivity;
import com.example.familyserverclient.R;
import com.example.familyserverclient.Results.EventAllResult;
import com.example.familyserverclient.Results.PersonResult;
import com.example.familyserverclient.SearchActivity;
import com.example.familyserverclient.SettingsActivity;
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

public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private LinearLayout eventSnapshot;
    private TextView eventPersonName;
    private TextView eventType;
    private ImageView icon;
    private GoogleMap mMap;
    private static EventAllResult eventAllTaskResult;
    private List<Event> filteredEvents = new ArrayList<>();
    private PersonResult selectedPerson;
    private static final int[] lifeLineColors = {Color.YELLOW, Color.GREEN, Color.RED};
    private static final int[] ancestorLineColors = {Color.GREEN, Color.RED, Color.YELLOW};
    private static final int[] spouseLineColors = {Color.RED, Color.YELLOW, Color.GREEN};
    private int lifeLineColor = 0;
    private int familyLineColor = 0;
    private int spouseLineColor = 0;
    private String map_type = "Normal";
    private boolean showLifeLines = true;
    private boolean showFamilyLines = true;
    private boolean showSpouseLines = true;
    private boolean showBirth = true;
    private boolean showCollege = true;
    private boolean showMarriage = true;
    private boolean showDeath = true;
    private boolean showFatherSide = true;
    private boolean showMotherSide = true;
    private boolean showMale = true;
    private boolean showFemale = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0)
        {
            String mapType = data.getStringExtra("MAP_TYPE");
            this.map_type = mapType;
            changeMapType(mapType);

            this.lifeLineColor = Integer.parseInt(data.getStringExtra("STORY_LINE_COLOR"));
            this.familyLineColor = Integer.parseInt(data.getStringExtra("FAMILY_LINE_COLOR"));
            this.spouseLineColor = Integer.parseInt(data.getStringExtra("SPOUSE_LINE_COLOR"));

            this.showLifeLines = data.getBooleanExtra("SHOW_LIFE_LINES", true);
            this.showFamilyLines = data.getBooleanExtra("SHOW_FAMILY_LINES", true);
            this.showSpouseLines = data.getBooleanExtra("SHOW_SPOUSE_LINES", true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        String token = ((MainActivity)getActivity()).getLoginObject().getAuthToken();
        sendEventAllRequest(token);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map, parent, false);

        this.eventSnapshot = (LinearLayout)v.findViewById(R.id.eventSnapshot);
        this.eventPersonName = (TextView)v.findViewById(R.id.eventPersonName);
        this.eventType = (TextView)v.findViewById(R.id.eventType);
        this.icon = (ImageView)v.findViewById(R.id.icon);

        Intent data = ((MainActivity)getActivity()).getIntent();
        if (data.hasExtra("SHOW_BIRTH"))
        {
            this.showBirth = data.getBooleanExtra("SHOW_BIRTH", true);
            this.showCollege = data.getBooleanExtra("SHOW_COLLEGE", true);
            this.showMarriage = data.getBooleanExtra("SHOW_MARRIAGE", true);
            this.showDeath = data.getBooleanExtra("SHOW_DEATH", true);
            this.showFatherSide = data.getBooleanExtra("SHOW_FATHER_SIDE", true);
            this.showMotherSide = data.getBooleanExtra("SHOW_MOTHER_SIDE", true);
            this.showMale = data.getBooleanExtra("SHOW_MALE", true);
            this.showFemale = data.getBooleanExtra("SHOW_FEMALE", true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null)
        {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        eventSnapshot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (!eventType.getText().toString().equals("Click on a marker to see event details"))
                {
                    LoginResult loginObject = ((MainActivity)getActivity()).getLoginObject();
                    String loginObjectJSON = new Gson().toJson(loginObject);
                    String selectedPersonJSON = new Gson().toJson(selectedPerson);
                    String eventAllResultJSON = new Gson().toJson(eventAllTaskResult);

                    Intent intent = new Intent(MapFragment.this.getContext(), PersonActivity.class);
                    intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
                    intent.putExtra("SELECTED_PERSON", selectedPersonJSON);
                    intent.putExtra("EVENT_ALL_RESULT", eventAllResultJSON);
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        LoginResult loginObject = ((MainActivity)getActivity()).getLoginObject();
        String loginObjectJSON = new Gson().toJson(loginObject);
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(MapFragment.this.getContext(), SettingsActivity.class);
            intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
            intent.putExtra("MAP_TYPE", this.map_type);
            intent.putExtra("STORY_LINE_COLOR", String.valueOf(this.lifeLineColor));
            intent.putExtra("FAMILY_LINE_COLOR", String.valueOf(this.familyLineColor));
            intent.putExtra("SPOUSE_LINE_COLOR", String.valueOf(this.spouseLineColor));
            intent.putExtra("SHOW_LIFE_LINES", this.showLifeLines);
            intent.putExtra("SHOW_FAMILY_LINES", this.showFamilyLines);
            intent.putExtra("SHOW_SPOUSE_LINES", this.showSpouseLines);
            startActivityForResult(intent, 0);
        }
        else if (id == R.id.action_filter)
        {
            Intent intent = new Intent(MapFragment.this.getContext(), FilterActivity.class);
            intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
            intent.putExtra("SHOW_BIRTH", this.showBirth);
            intent.putExtra("SHOW_COLLEGE", this.showCollege);
            intent.putExtra("SHOW_MARRIAGE", this.showMarriage);
            intent.putExtra("SHOW_DEATH", this.showDeath);
            intent.putExtra("SHOW_FATHER_SIDE", this.showFatherSide);
            intent.putExtra("SHOW_MOTHER_SIDE", this.showMotherSide);
            intent.putExtra("SHOW_MALE", this.showMale);
            intent.putExtra("SHOW_FEMALE", this.showFemale);
            startActivityForResult(intent, 1);
        }
        else if (id == R.id.action_search)
        {
            Intent intent = new Intent(MapFragment.this.getContext(), SearchActivity.class);
            EventAllResult passedResultData = new EventAllResult(this.filteredEvents);
            String eventListJSON = new Gson().toJson(passedResultData);
            intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
            intent.putExtra("EVENT_LIST", eventListJSON);
            startActivity(intent);
        }

        return true;
    }

    public void fillSideEvents(List<Event> events, String personID)
    {
        for (Event event : this.eventAllTaskResult.getData())
        {
            if (event.getPerson().equals(personID))
            {
                events.add(event);
            }
        }
        PersonResult personObject = sendPersonRequest(personID, ((MainActivity)getActivity()).getLoginObject().getAuthToken());
        if (personObject.getFather() != null)
        {
            fillSideEvents(events, personObject.getFather());
        }
        if (personObject.getMother() != null)
        {
            fillSideEvents(events, personObject.getMother());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.mMap = googleMap;
        this.mMap.clear();
        String currentUserID = ((MainActivity)getActivity()).getLoginObject().getPersonID();
        String auth_token = ((MainActivity)getActivity()).getLoginObject().getAuthToken();
        PersonResult currentUser = sendPersonRequest(currentUserID, auth_token);
        List<Event> fatherSideEvents = new ArrayList<>();
        List<Event> motherSideEvents = new ArrayList<>();
        List<Event> ownEvents = sendRelatedEventRequest(currentUserID, auth_token).getData();
        fillSideEvents(fatherSideEvents, currentUser.getFather());
        fillSideEvents(motherSideEvents, currentUser.getMother());

        for (Event event : ownEvents)
        {
            String gender = currentUser.getGender();
            if ((this.showMale == true && gender.equals("m")) || ((this.showFemale == true) && gender.equals("f")))
            {
                LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker = null;
                if (this.showBirth == true && event.getEventType().equals("Birth"))
                {
                    marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                else if (this.showCollege == true && event.getEventType().equals("College Graduation"))
                {
                    marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
                else if (this.showMarriage == true && event.getEventType().equals("Marriage"))
                {
                    marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                }
                else if(this.showDeath == true && event.getEventType().equals("Death"))
                {
                    marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                }
                if (marker != null)
                {
                    marker.setTag(event);
                    this.filteredEvents.add(event);
                }
            }
        }

        if (this.showFatherSide == true)
        {
            for (Event event : fatherSideEvents)
            {
                PersonResult person = sendPersonRequest(event.getPerson(), auth_token);
                String gender = person.getGender();
                if ((this.showMale == true && gender.equals("m")) || ((this.showFemale == true) && gender.equals("f")))
                {
                    LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                    Marker marker = null;
                    if (this.showBirth == true && event.getEventType().equals("Birth"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    }
                    else if (this.showCollege == true && event.getEventType().equals("College Graduation"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    else if (this.showMarriage == true && event.getEventType().equals("Marriage"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    }
                    else if(this.showDeath == true && event.getEventType().equals("Death"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    }
                    if (marker != null)
                    {
                        marker.setTag(event);
                        this.filteredEvents.add(event);
                    }
                }
            }
        }
        if (this.showMotherSide == true)
        {
            for (Event event : motherSideEvents)
            {
                PersonResult person = sendPersonRequest(event.getPerson(), auth_token);
                String gender = person.getGender();
                if ((this.showMale == true && gender.equals("m")) || ((this.showFemale == true) && gender.equals("f")))
                {
                    LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                    Marker marker = null;
                    if (this.showBirth == true && event.getEventType().equals("Birth"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    }
                    else if (this.showCollege == true && event.getEventType().equals("College Graduation"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    else if (this.showMarriage == true && event.getEventType().equals("Marriage"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    }
                    else if(this.showDeath == true && event.getEventType().equals("Death"))
                    {
                        marker = this.mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    }
                    if (marker != null)
                    {
                        marker.setTag(event);
                        this.filteredEvents.add(event);
                    }
                }
            }
        }

        this.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Polyline spouseLine;
            List<Polyline> ancestorLines = new ArrayList<>();
            List<Polyline> lifeLines = new ArrayList<>();

            public boolean onMarkerClick(Marker marker)
            {
                removeSpouseLine();
                removeAncestorLines(ancestorLines);
                removeLifeLines(lifeLines);

                Event event = (Event)marker.getTag();
                String token = ((MainActivity)getActivity()).getLoginObject().getAuthToken();
                PersonResult result = sendPersonRequest(event.getPerson(), token);
                selectedPerson = result;
                if (result != null)
                {
                    Drawable genderIcon = null;
                    if (result.getGender().equals("m"))
                    {
                        genderIcon = new IconDrawable(MapFragment.this.getContext(), FontAwesomeIcons.fa_male).sizeDp(50);
                    }
                    else
                    {
                        genderIcon = new IconDrawable(MapFragment.this.getContext(), FontAwesomeIcons.fa_female).sizeDp(50);
                    }
                    icon.setImageDrawable(genderIcon);
                    eventPersonName.setText(result.getFirstName() + " " + result.getLastName());
                    eventType.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
                }

                if (showSpouseLines == true)
                {
                    String spouse = result.getSpouse();
                    drawSpouseLine(spouse, event);
                }
                if (showLifeLines == true)
                {
                    drawLifeLines(result, lifeLines);
                }
                if (showFamilyLines == true)
                {
                    String father = result.getFather();
                    String mother = result.getMother();
                    drawAncestorLines(father, mother, event, ancestorLines, 10);
                }
                return true;
            }
            public void removeSpouseLine()
            {
                if (spouseLine != null)
                {
                    spouseLine.remove();
                }
            }
            public void drawSpouseLine(String spouseID, Event event)
            {
                if (spouseID != null)
                {
                    Event birth = findBirthEvent(spouseID);
                    Event college = findCollegeEvent(spouseID);
                    Event marriage = findMarriageEvent(spouseID);
                    Event death = findDeathEvent(spouseID);
                    if (birth != null && filteredEvents.contains(birth))
                    {
                        spouseLine = mMap.addPolyline(new PolylineOptions().add(new LatLng(event.getLatitude(), event.getLongitude()), new LatLng(birth.getLatitude(), birth.getLongitude())).width(5).color(spouseLineColors[spouseLineColor]));
                    }
                    else if (college != null && filteredEvents.contains(college))
                    {
                        spouseLine = mMap.addPolyline(new PolylineOptions().add(new LatLng(event.getLatitude(), event.getLongitude()), new LatLng(college.getLatitude(), college.getLongitude())).width(5).color(spouseLineColors[spouseLineColor]));
                    }
                    else if (marriage != null && filteredEvents.contains(marriage))
                    {
                        spouseLine = mMap.addPolyline(new PolylineOptions().add(new LatLng(event.getLatitude(), event.getLongitude()), new LatLng(marriage.getLatitude(), marriage.getLongitude())).width(5).color(spouseLineColors[spouseLineColor]));
                    }
                    else if (death != null && filteredEvents.contains(death))
                    {
                        spouseLine = mMap.addPolyline(new PolylineOptions().add(new LatLng(event.getLatitude(), event.getLongitude()), new LatLng(death.getLatitude(), death.getLongitude())).width(5).color(spouseLineColors[spouseLineColor]));
                    }
                }
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
                EventRelatedResult events = sendRelatedEventRequest(person.getPersonID(), ((MainActivity)getActivity()).getLoginObject().getAuthToken());
                List<Event> eList = events.getData();
                for (int i = 1; i < eList.size(); i++)
                {
                    Event event1 = eList.get(i - 1);
                    Event event2 = eList.get(i);
                    PolylineOptions option = new PolylineOptions().add(new LatLng(event1.getLatitude(), event1.getLongitude()), new LatLng(event2.getLatitude(), event2.getLongitude())).width(5).color(lifeLineColors[lifeLineColor]);
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
                    Event college = findCollegeEvent(fatherID);
                    Event marriage = findMarriageEvent(fatherID);
                    Event death = findDeathEvent(fatherID);

                    Event selected = null;
                    if (showBirth == true && birth != null)
                    {
                        selected = birth;
                    }
                    else if(showCollege == true && college != null)
                    {
                        selected = college;
                    }
                    else if(showMarriage == true && marriage != null)
                    {
                        selected = marriage;
                    }
                    else if(showDeath == true && death != null)
                    {
                        selected = death;
                    }

                    if (selected != null)
                    {
                        PolylineOptions option = new PolylineOptions().add(new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude()), new LatLng(birth.getLatitude(), birth.getLongitude())).width(width).color(ancestorLineColors[familyLineColor]);
                        Polyline line = mMap.addPolyline(option);
                        ancestorLines.add(line);
                    }
                    PersonResult fatherObject = sendPersonRequest(fatherID, ((MainActivity)getActivity()).getLoginObject().getAuthToken());
                    drawAncestorLines(fatherObject.getFather(), fatherObject.getMother(), birth, ancestorLines, width - 2);
                }
                if (motherID != null)
                {
                    Event birth = findBirthEvent(fatherID);
                    Event college = findCollegeEvent(fatherID);
                    Event marriage = findMarriageEvent(fatherID);
                    Event death = findDeathEvent(fatherID);

                    Event selected = null;
                    if (showBirth == true && birth != null)
                    {
                        selected = birth;
                    }
                    else if(showCollege == true && college != null)
                    {
                        selected = college;
                    }
                    else if(showMarriage == true && marriage != null)
                    {
                        selected = marriage;
                    }
                    else if(showDeath == true && death != null)
                    {
                        selected = death;
                    }

                    if (selected != null)
                    {
                        PolylineOptions option = new PolylineOptions().add(new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude()), new LatLng(birth.getLatitude(), birth.getLongitude())).width(width).color(ancestorLineColors[familyLineColor]);
                        Polyline line = mMap.addPolyline(option);
                        ancestorLines.add(line);
                    }
                    PersonResult motherObject = sendPersonRequest(motherID, ((MainActivity)getActivity()).getLoginObject().getAuthToken());
                    drawAncestorLines(motherObject.getFather(), motherObject.getMother(), birth, ancestorLines, width - 2);
                }
            }
        });
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

    public Event findCollegeEvent(String personID)
    {
        for (Event event : this.eventAllTaskResult.getData())
        {
            if (event.getEventType().equals("College Graduation") && event.getPerson().equals(personID))
            {
                return event;
            }
        }
        return null;
    }

    public Event findMarriageEvent(String personID)
    {
        for (Event event : this.eventAllTaskResult.getData())
        {
            if (event.getEventType().equals("Marriage") && event.getPerson().equals(personID))
            {
                return event;
            }
        }
        return null;
    }

    public Event findDeathEvent(String personID)
    {
        for (Event event : this.eventAllTaskResult.getData())
        {
            if (event.getEventType().equals("Death") && event.getPerson().equals(personID))
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
            String url = "http://" + ((MainActivity)getActivity()).getServerHost() + ":8080/event/person/" + personID;
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
                Toast.makeText(MapFragment.this.getContext(), "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(MapFragment.this.getContext(), "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void sendEventAllRequest(String auth_token)
    {
        try
        {
            EventAllTask task = new EventAllTask();
            String url = "http://" + ((MainActivity)getActivity()).getServerHost() + ":8080/event";
            ArrayList params = new ArrayList(2);
            params.add(new URL(url));
            params.add(auth_token);
            task.execute(params);
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(MapFragment.this.getContext(), "Event all request failed, please check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    public PersonResult sendPersonRequest(String personID, String auth_token)
    {
        try
        {
            PersonTask task = new PersonTask();
            String url = "http://" + ((MainActivity)getActivity()).getServerHost() + ":8080/person/" + personID;
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
                Toast.makeText(MapFragment.this.getContext(), "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(MapFragment.this.getContext(), "Person request failed, please check your connection", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MapFragment.this.getContext(), "Event all request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (result.getData() == null)
                {
                    Toast.makeText(MapFragment.this.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class EventAllTask extends AsyncTask<ArrayList, Integer, EventAllResult>
    {
        protected EventAllResult doInBackground(ArrayList... params)
        {
            HttpClient httpClient = new HttpClient();

            String responseMsg = null;
            for (int i = 0; i < params.length; i++)
            {
                responseMsg = httpClient.getRequest((URL)params[i].get(0), (String)params[i].get(1));
            }
            return new Gson().fromJson(responseMsg, EventAllResult.class);
        }
        protected void onPostExecute(EventAllResult result)
        {
            if (result == null)
            {
                Toast.makeText(MapFragment.this.getContext(), "Event all request failed, please check your connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (result.getData() == null)
                {
                    Toast.makeText(MapFragment.this.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    eventAllTaskResult = result;
                }
            }
        }
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
                Toast.makeText(MapFragment.this.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeMapType(String mapType)
    {
        if (mapType.equals("Normal"))
        {
            this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if (mapType.equals("Hybrid"))
        {
            this.mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else if (mapType.equals("Satellite"))
        {
            this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else
        {
            this.mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
    }
}
