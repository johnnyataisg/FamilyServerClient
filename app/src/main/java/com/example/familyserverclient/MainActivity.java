package com.example.familyserverclient;

import com.example.familyserverclient.Fragments.LoginFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.loginFragment);
        if (fragment == null)
        {
            fragment = new LoginFragment();
            fm.beginTransaction().add(R.id.loginFragment, fragment).commit();
        }
    }
}
