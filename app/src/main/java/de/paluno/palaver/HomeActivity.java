package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity {

    Button settings, search, contacts;
    EditText searchBox;

    NetworkTasks nt;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getSharedPreferences("main", MODE_PRIVATE);

        String name = prefs.getString("Username", "");
        Log.e("."+name+"'s Token", prefs.getString("Token", ""));

        settings = findViewById(R.id.btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivity();
            }
        });

        search = findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileWithSearch();
            }
        });

        searchBox = findViewById(R.id.edit_search);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
            }
        });

        contacts = findViewById(R.id.btn_contacts);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactsActivity();
            }
        });

    }

    void startSettingsActivity(){
        Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    void startContactsActivity(){
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");
        new NetworkTasks(this).execute("getFriends", name, psw);

    }

    void openProfileWithSearch(){
        String owner = searchBox.getText().toString();

        getMessages(owner);
    }

    private void getMessages(String owner) {
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");
        new NetworkTasks(this).execute("getMessage", name, psw, owner);
    }

}
