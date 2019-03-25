package com.example.familyserverclient.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.familyserverclient.Models.*;
import com.example.familyserverclient.R;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    private Button registerBtn;
    private Button loginBtn;

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
        this.registerBtn = (Button)v.findViewById(R.id.registerBtn);

        this.loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ExistingUser user = new ExistingUser(userName.getText().toString(), password.getText().toString());
                String requestBody = new Gson().toJson(user);
                sendLoginRequest(requestBody);
            }
        });

        this.registerBtn.setOnClickListener(new View.OnClickListener() {
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
                NewUser newUser = new NewUser(userName.getText().toString(),
                    password.getText().toString(),
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    email.getText().toString(),
                    selectedGender);
                String requestBody = new Gson().toJson(newUser);
                sendRegisterRequest(requestBody);
            }
        });

        return v;
    }

    public void sendLoginRequest(String requestBody)
    {
        try
        {
            LoginTask task = new LoginTask();
            String url = "http://" + this.serverHost.getText() + ":" + this.serverPort.getText() + "/user/login";
            ArrayList params = new ArrayList(2);
            params.add(new URL(url));
            params.add(requestBody);
            task.execute(params);
        }
        catch(MalformedURLException e)
        {
            Toast.makeText(LoginFragment.this.getContext(), "Request failed, please check your server host or port", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendRegisterRequest(String requestBody)
    {
        try
        {
            RegisterTask task = new RegisterTask();
            String url = "http://" + this.serverHost.getText() + ":" + this.serverPort.getText() + "/user/register";
            ArrayList params = new ArrayList(2);
            params.add(new URL(url));
            params.add(requestBody);
            task.execute(params);
        }
        catch (MalformedURLException e)
        {
            Toast.makeText(LoginFragment.this.getContext(), "Request failed, please check your server host or port", Toast.LENGTH_SHORT).show();
        }
    }

    public class LoginTask extends AsyncTask<ArrayList, Integer, LoginResult>
    {
        protected LoginResult doInBackground(ArrayList... params)
        {
            HttpClient httpClient = new HttpClient();

            String responseMsg = null;
            for (int i = 0; i < params.length; i++)
            {
                responseMsg = httpClient.postRequest((URL)params[i].get(0), (String)params[i].get(1));
            }
            return new Gson().fromJson(responseMsg, LoginResult.class);
        }
        protected void onPostExecute(LoginResult result)
        {
            if (result.getMessage() != null)
            {
                Toast.makeText(LoginFragment.this.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                try
                {
                    ArrayList params = new ArrayList(2);
                    params.add(new URL("http://" + serverHost.getText() + ":" + serverPort.getText() + "/person"));
                    params.add(result.getAuthToken());
                    PersonAllTask task = new PersonAllTask();
                    task.execute(params);
                }
                catch (MalformedURLException e)
                {
                    Toast.makeText(LoginFragment.this.getContext(), "Request failed, please check your server host or port", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class RegisterTask extends AsyncTask<ArrayList, Integer, LoginResult>
    {
        protected LoginResult doInBackground(ArrayList... params)
        {
            HttpClient httpClient = new HttpClient();

            String responseMsg = null;
            for (int i = 0; i < params.length; i++)
            {
                responseMsg = httpClient.postRequest((URL)params[i].get(0), (String)params[i].get(1));
            }

            return new Gson().fromJson(responseMsg, LoginResult.class);
        }
        protected void onPostExecute(LoginResult result)
        {
            if (result.getMessage() != null)
            {
                Toast.makeText(LoginFragment.this.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                try
                {
                    ArrayList params = new ArrayList(2);
                    params.add(new URL("http://" + serverHost.getText() + ":" + serverPort.getText() + "/person"));
                    params.add(result.getAuthToken());
                    PersonAllTask task = new PersonAllTask();
                    task.execute(params);
                }
                catch (MalformedURLException e)
                {
                    Toast.makeText(LoginFragment.this.getContext(), "Request failed, please check your server host or port", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
            String message = result.getData().get(0).getFirstName() + " " + result.getData().get(0).getLastName();
            Toast.makeText(LoginFragment.this.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
