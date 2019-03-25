package com.example.familyserverclient.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familyserverclient.Models.HttpClient;
import com.example.familyserverclient.Models.NewUser;
import com.example.familyserverclient.R;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.*;

public class LoginFragment extends Fragment
{
    private EditText serverHost;
    private EditText serverPort;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup gender;
    private Button loginBtn;
    private NewUser newUser;
    private String requestBody;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_login, parent, false);

        this.serverHost = (EditText)v.findViewById(R.id.server_host);
        this.serverPort = (EditText)v.findViewById(R.id.server_port);
        this.userName = (EditText)v.findViewById(R.id.username);
        this.password = (EditText)v.findViewById(R.id.password);
        this.firstName = (EditText)v.findViewById(R.id.firstname);
        this.lastName = (EditText)v.findViewById(R.id.lastname);
        this.email = (EditText)v.findViewById(R.id.email);
        this.gender = (RadioGroup)v.findViewById(R.id.gender);
        this.loginBtn = (Button)v.findViewById(R.id.loginBtn);

        this.loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String selectedGender = null;
                if (((int)(gender.getCheckedRadioButtonId()) == 1))
                {
                    selectedGender = "m";
                }
                else
                {
                    selectedGender = "f";
                }
                newUser = new NewUser(userName.getText().toString(),
                    password.getText().toString(),
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    email.getText().toString(),
                    selectedGender);
                Gson gson = new Gson();
                requestBody = gson.toJson(newUser);
                sendRegisterRequest();
            }
        });

        return v;
    }

    public void sendRegisterRequest()
    {
        try
        {
            RegisterTask task = new RegisterTask();
            task.execute(new URL("http://192.168.56.1:8080/clear/"));
        }
        catch (MalformedURLException e)
        {
            Log.e("MainActivity", e.getMessage(), e);
        }
    }

    public class RegisterTask extends AsyncTask<URL, Integer, String>
    {
        protected String doInBackground(URL... urls)
        {
            HttpClient httpClient = new HttpClient();

            String urlContent = null;
            for (int i = 0; i < urls.length; i++)
            {
                urlContent = httpClient.postRequest(urls[i], requestBody);
            }

            return urlContent;
        }
        protected void onPostExecute(String result)
        {
            Toast.makeText(LoginFragment.this.getContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
