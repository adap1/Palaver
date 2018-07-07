package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    String owner;
    Button home, send, remove;

    NetworkTasks nt;
    SharedPreferences prefs;
    List<String> messages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nt = new NetworkTasks(this);
        prefs = getSharedPreferences("main", MODE_PRIVATE);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null){
            String s = b.getString("Name");
            TextView tv = findViewById(R.id.view_profile_name);
            tv.setText(s);
            owner = s;
        }

        getMessages();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleExtras();
            }
        }, 2000);

        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });

        send = findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });

        remove = findViewById(R.id.btn_removeContact);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact();
            }
        });

    }

    private void getMessages() {
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");
        new NetworkTasks(this).execute("getMessage", name, psw, owner);
    }

    void startHomeActivity(){
        Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(i);
    }

    void sendMessage(){
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");
        EditText _message = findViewById(R.id.et_message);
        String message = _message.getText().toString();

        if(!message.isEmpty()){
            new NetworkTasks(this).execute("sendMessage", name, psw, owner, message);
        }
    }

    void removeContact(){
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");

        new NetworkTasks(this).execute("removeContact", name, psw, owner);
    }

    private void handleExtras() {
        System.out.println("handle extras messages");
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            String s = (String) b.get("Data");
            String originalS = s;
            if(s != null && !s.isEmpty()) {
                messages = new ArrayList<>();

                while (s.contains(",")) {
                    int comma = s.indexOf(",");
                    String temp = s.substring(0, comma);
                    messages.add(temp);
                    s = s.substring(comma+2);
                }
                if(!s.contains(",") && !s.isEmpty()){
                    messages.add(s);
                }

                System.out.println("original String of messages "+ originalS);
                System.out.println("List of messages "+ messages.toString());
            }
        }
    }


}
