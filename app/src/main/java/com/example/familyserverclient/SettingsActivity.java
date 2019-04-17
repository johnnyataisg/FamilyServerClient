package com.example.familyserverclient;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity
{
    private static final String[] mapTypes = {"Normal", "Hybrid", "Satellite", "Terrain"};
    private static final String[] lifeLineColors = {"Yellow", "Green", "Red"};
    private static final String[] ancestorLineColors = {"Green", "Red", "Yellow"};
    private static final String[] spouseLineColors = {"Red", "Yellow", "Green"};
    private Spinner mapType;
    private Spinner storyLineColor;
    private Spinner spouseLineColor;
    private Spinner ancestorLineColor;
    private SwitchCompat toggleLifeLines;
    private SwitchCompat toggleFamilyLines;
    private SwitchCompat toggleSpouseLines;
    private LinearLayout logoutBtn;
    private Intent data;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                data.putExtra("SHOW_LIFE_LINES", this.toggleLifeLines.isChecked());
                data.putExtra("SHOW_FAMILY_LINES", this.toggleFamilyLines.isChecked());
                data.putExtra("SHOW_SPOUSE_LINES", this.toggleSpouseLines.isChecked());
                setResult(RESULT_OK, data);
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
        setContentView(R.layout.activity_settings);

        data = new Intent(SettingsActivity.this, MainActivity.class);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String defaultType = getIntent().getStringExtra("MAP_TYPE");
        int color1 = Integer.parseInt(getIntent().getStringExtra("STORY_LINE_COLOR"));
        int color2 = Integer.parseInt(getIntent().getStringExtra("FAMILY_LINE_COLOR"));
        int color3 = Integer.parseInt(getIntent().getStringExtra("SPOUSE_LINE_COLOR"));
        boolean toggle1 = getIntent().getBooleanExtra("SHOW_LIFE_LINES", true);
        boolean toggle2 = getIntent().getBooleanExtra("SHOW_FAMILY_LINES", true);
        boolean toggle3 = getIntent().getBooleanExtra("SHOW_SPOUSE_LINES", true);

        this.logoutBtn = (LinearLayout)findViewById(R.id.logoutBtn);
        this.logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        this.toggleLifeLines = (SwitchCompat)findViewById(R.id.toggleLifeLines);
        this.toggleLifeLines.setChecked(toggle1);
        this.toggleLifeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                toggleLifeLines.setChecked(isChecked);
            }
        });

        this.toggleFamilyLines = (SwitchCompat)findViewById(R.id.toggleFamilyLines);
        this.toggleFamilyLines.setChecked(toggle2);
        this.toggleFamilyLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                toggleFamilyLines.setChecked(isChecked);
            }
        });

        this.toggleSpouseLines = (SwitchCompat)findViewById(R.id.toggleSpouseLines);
        this.toggleSpouseLines.setChecked(toggle3);
        this.toggleSpouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                toggleSpouseLines.setChecked(isChecked);
            }
        });

        this.mapType = (Spinner)findViewById(R.id.mapType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mapTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mapType.setAdapter(adapter);
        this.mapType.setSelection(mapTypeIndex(defaultType));

        this.storyLineColor = (Spinner)findViewById(R.id.storyLineColor);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lifeLineColors);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.storyLineColor.setAdapter(adapter2);
        this.storyLineColor.setSelection(color1);

        this.ancestorLineColor = (Spinner)findViewById(R.id.ancestorLineColor);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ancestorLineColors);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.ancestorLineColor.setAdapter(adapter3);
        this.ancestorLineColor.setSelection(color2);

        this.spouseLineColor = (Spinner)findViewById(R.id.spouseLineColor);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spouseLineColors);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spouseLineColor.setAdapter(adapter4);
        this.spouseLineColor.setSelection(color3);

        this.mapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                data.putExtra("MAP_TYPE", mapTypes[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });

        this.storyLineColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                data.putExtra("STORY_LINE_COLOR", String.valueOf(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });

        this.ancestorLineColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String test = String.valueOf(position);
                data.putExtra("FAMILY_LINE_COLOR", String.valueOf(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });

        this.spouseLineColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                data.putExtra("SPOUSE_LINE_COLOR", String.valueOf(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        data.putExtra("SHOW_LIFE_LINES", this.toggleLifeLines.isChecked());
        data.putExtra("SHOW_FAMILY_LINES", this.toggleFamilyLines.isChecked());
        data.putExtra("SHOW_SPOUSE_LINES", this.toggleSpouseLines.isChecked());
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    public int mapTypeIndex(String mapType)
    {
        if (mapType.equals("Normal"))
        {
            return 0;
        }
        else if (mapType.equals("Hybrid"))
        {
            return 1;
        }
        else if (mapType.equals("Satellite"))
        {
            return 2;
        }
        else
        {
            return 3;
        }
    }
}
