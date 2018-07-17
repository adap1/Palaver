package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class SettingsActivity extends AppCompatActivity{

    Button logout, home, myProfile;
    CheckBox push;

    SharedPreferences prefs;
    Context ctx;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = this.getSharedPreferences("main", Context.MODE_PRIVATE);

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
//        myProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
        myProfile.setText(prefs.getString("Username", ""));

        push = findViewById(R.id.box_notification);

        if (prefs.getBoolean("checked_push", false)){
            push.setChecked(true);
        }else{
            push.setChecked(false);
        }

        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                prefs.edit().putBoolean("checked_push", true).apply();
                getToken();
            } else {
                prefs.edit().putBoolean("checked_push", false).apply();
                deleteToken();
            }

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

    void getToken(){

        ctx = this;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SettingsActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String name = prefs.getString("Username", "");
                String psw = prefs.getString("Password", "");

                String token = instanceIdResult.getToken();
                prefs.edit().putString("Token", token).apply();

                new NetworkTasks(ctx).execute("pushtoken", name, psw, "", "", token);

            }
        });

    }

    void deleteToken(){

    }


}
