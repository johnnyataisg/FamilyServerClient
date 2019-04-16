package com.example.familyserverclient;

import com.example.familyserverclient.Fragments.LoginFragment;
import com.example.familyserverclient.Models.LoginResult;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{
    private LoginResult loginResultObject;
    private String serverHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.mainFragment);
        if (fragment == null)
        {
            fragment = new LoginFragment();
            fm.beginTransaction().add(R.id.mainFragment, fragment).commit();
        }
    }

    public LoginResult getLoginObject()
    {
        return this.loginResultObject;
    }

    public String getServerHost()
    {
        return this.serverHost;
    }

    public void setLoginObject(LoginResult result)
    {
        this.loginResultObject = result;
    }

    public void setServerHost(String host)
    {
        this.serverHost = host;
    }
}
