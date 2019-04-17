package com.example.familyserverclient;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;

public class FilterActivity extends AppCompatActivity
{
    private SwitchCompat birthEvents;
    private SwitchCompat collegeEvents;
    private SwitchCompat marriageEvents;
    private SwitchCompat deathEvents;
    private SwitchCompat fatherSide;
    private SwitchCompat motherSide;
    private SwitchCompat maleEvents;
    private SwitchCompat femaleEvents;
    private Intent data;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                data.putExtra("SHOW_BIRTH", this.birthEvents.isChecked());
                data.putExtra("SHOW_COLLEGE", this.collegeEvents.isChecked());
                data.putExtra("SHOW_MARRIAGE", this.marriageEvents.isChecked());
                data.putExtra("SHOW_DEATH", this.deathEvents.isChecked());
                data.putExtra("SHOW_FATHER_SIDE", this.fatherSide.isChecked());
                data.putExtra("SHOW_MOTHER_SIDE", this.motherSide.isChecked());
                data.putExtra("SHOW_MALE", this.maleEvents.isChecked());
                data.putExtra("SHOW_FEMALE", this.femaleEvents.isChecked());
                startActivity(data);
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
        setContentView(R.layout.activity_filter);

        data = new Intent(FilterActivity.this, MainActivity.class);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        boolean showBirth = getIntent().getBooleanExtra("SHOW_BIRTH", true);
        boolean showCollege = getIntent().getBooleanExtra("SHOW_COLLEGE", true);
        boolean showMarriage = getIntent().getBooleanExtra("SHOW_MARRIAGE", true);
        boolean showDeath = getIntent().getBooleanExtra("SHOW_DEATH", true);
        boolean showFatherSide = getIntent().getBooleanExtra("SHOW_FATHER_SIDE", true);
        boolean showMotherSide = getIntent().getBooleanExtra("SHOW_MOTHER_SIDE", true);
        boolean showMale = getIntent().getBooleanExtra("SHOW_MALE", true);
        boolean showFemale = getIntent().getBooleanExtra("SHOW_FEMALE", true);

        this.birthEvents = (SwitchCompat)findViewById(R.id.birthEvents);
        this.birthEvents.setChecked(showBirth);
        this.birthEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                birthEvents.setChecked(isChecked);
            }
        });

        this.collegeEvents = (SwitchCompat)findViewById(R.id.collegeEvents);
        this.collegeEvents.setChecked(showCollege);
        this.collegeEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                collegeEvents.setChecked(isChecked);
            }
        });

        this.marriageEvents = (SwitchCompat)findViewById(R.id.marriageEvents);
        this.marriageEvents.setChecked(showMarriage);
        this.marriageEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                marriageEvents.setChecked(isChecked);
            }
        });

        this.deathEvents = (SwitchCompat)findViewById(R.id.deathEvents);
        this.deathEvents.setChecked(showDeath);
        this.deathEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                deathEvents.setChecked(isChecked);
            }
        });

        this.fatherSide = (SwitchCompat)findViewById(R.id.fatherSide);
        this.fatherSide.setChecked(showFatherSide);
        this.fatherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                fatherSide.setChecked(isChecked);
            }
        });

        this.motherSide = (SwitchCompat)findViewById(R.id.motherSide);
        this.motherSide.setChecked(showMotherSide);
        this.motherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                motherSide.setChecked(isChecked);
            }
        });

        this.maleEvents = (SwitchCompat)findViewById(R.id.maleEvents);
        this.maleEvents.setChecked(showMale);
        this.maleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                maleEvents.setChecked(isChecked);
            }
        });

        this.femaleEvents = (SwitchCompat)findViewById(R.id.femaleEvents);
        this.femaleEvents.setChecked(showFemale);
        this.femaleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                femaleEvents.setChecked(isChecked);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        data.putExtra("SHOW_BIRTH", this.birthEvents.isChecked());
        data.putExtra("SHOW_COLLEGE", this.collegeEvents.isChecked());
        data.putExtra("SHOW_MARRIAGE", this.marriageEvents.isChecked());
        data.putExtra("SHOW_DEATH", this.deathEvents.isChecked());
        data.putExtra("SHOW_FATHER_SIDE", this.fatherSide.isChecked());
        data.putExtra("SHOW_MOTHER_SIDE", this.motherSide.isChecked());
        data.putExtra("SHOW_MALE", this.maleEvents.isChecked());
        data.putExtra("SHOW_FEMALE", this.femaleEvents.isChecked());
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}
