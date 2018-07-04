package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity{

    Button logout, home, myProfile;

    SharedPreferences prefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = this.getSharedPreferences("checked_login", Context.MODE_PRIVATE);

        logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("checked_login", false).apply();
                startLogInActivity();
            }
        });

        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });

        myProfile = findViewById(R.id.btn_profile_own);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity();
            }
        });
    }

    void startLogInActivity(){
        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
        i.putExtra("LogOut", true);
        startActivity(i);
    }

    void startHomeActivity(){
        Intent i = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(i);
    }

    void startProfileActivity(){
        Intent i = new Intent(SettingsActivity.this, ProfileActivity.class);
        i.putExtra("Name", "My own profile");
        startActivity(i);
    }

}
