package de.paluno.palaver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

        nt = new NetworkTasks(this);
//        prefs = getPreferences(MODE_PRIVATE);
        prefs = getSharedPreferences("main", MODE_PRIVATE);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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
        System.out.println("name " + name +"\npsw " + psw);
        new NetworkTasks(this).execute("getFriends", name, psw);

//        Intent i = new Intent(HomeActivity.this, ContactsActivity.class);
//        startActivity(i);
    }

    void openProfileWithSearch(){
        String name = searchBox.getText().toString();

        Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
        i.putExtra("Name", name);
        startActivity(i);
    }
}
