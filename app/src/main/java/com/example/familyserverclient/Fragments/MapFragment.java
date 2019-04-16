package com.example.familyserverclient.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.familyserverclient.MainActivity;
import com.example.familyserverclient.Models.Event;
import com.example.familyserverclient.Models.HttpClient;
import com.example.familyserverclient.Models.LoginResult;
import com.example.familyserverclient.PersonActivity;
import com.example.familyserverclient.R;
import com.example.familyserverclient.Results.EventAllResult;
import com.example.familyserverclient.Results.PersonResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private LinearLayout eventSnapshot;
    private TextView eventPersonName;
    private TextView eventType;
    private GoogleMap mMap;
    private EventAllResult eventAllTaskResult;
    private PersonResult selectedPerson;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

                    Intent intent = new Intent(MapFragment.this.getContext(), PersonActivity.class);
                    intent.putExtra("LOGIN_OBJECT", loginObjectJSON);
                    intent.putExtra("SELECTED_PERSON", selectedPersonJSON);
                    startActivityForResult(intent, 0);
                }
            }
        });

        return v;
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

        this.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            public boolean onMarkerClick(Marker marker)
            {
                Event event = (Event)marker.getTag();
                String token = ((MainActivity)getActivity()).getLoginObject().getAuthToken();
                PersonResult result = sendPersonRequest(event.getPerson(), token);
                selectedPerson = result;
                if (result != null)
                {
                    eventPersonName.setText(result.getFirstName() + " " + result.getLastName());
                    eventType.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
                }
                return true;
            }
        });
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
}
