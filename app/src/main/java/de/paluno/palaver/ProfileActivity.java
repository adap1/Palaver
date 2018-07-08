package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    LinearLayout group_Messages;

    NetworkTasks nt;
    SharedPreferences prefs;
    List<String> messages;
    List<JSONObject> msgJson;

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

//        getMessages();
        handleExtras();

        group_Messages = findViewById(R.id.group_messages);


        for (JSONObject j : msgJson){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            try{
                String sender = j.getString("Sender");
                String msg = j.getString("Data");
                String time = j.getString("DateTime");
                TextView tv = new TextView(this);
                params.setMargins(0, 15, 0, 0);
                if(sender.equals(prefs.getString("Username", ""))){
//                    params.setMarginEnd(150);
//                    params.setMargins(350, 15, 10, 0);
                    params.setMarginStart(500);
                    System.out.println("Ist vom Sender " + msg);
                }else{
//                    params.setMarginStart(150);
//                    params.setMargins(10, 15, 350, 0);
                    params.setMarginStart(150);
                    System.out.println("Ist vom anderen " + msg);
                }
                tv.setLayoutParams(params);
                tv.setText(msg);
                tv.setTextColor(Color.WHITE);

                group_Messages.addView(tv);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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

        System.out.println("Username= " + name);
        System.out.println("Password= " + psw);
        System.out.println("Recipient= " + owner);

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
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
//            String[] msg = (String[]) b.get("Data");
            String s = (String) b.get("rawData");
            String originalS = s;
            if(s != null && !s.isEmpty()) {

//                messages = new ArrayList<>();
//
//                while (s.contains(",")) {
//                    int comma = s.indexOf(",");
//                    String temp = s.substring(0, comma);
//                    messages.add(temp);
//                    s = s.substring(comma+2);
//                }
//                if(!s.contains(",") && !s.isEmpty()){
//                    messages.add(s);
//                }

                JSONObject json = toJ(s);
                JSONArray jsonArray = getData(json);
                msgJson = new ArrayList<>();
                for ( int i = 0; i < jsonArray.length(); i++){
                    try{
                        msgJson.add((JSONObject)jsonArray.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (JSONObject j : msgJson){
                    toString(j);
                }
            }
        }
    }

    void toString(JSONObject json) {
        System.out.println("\nJSONObject to String\n.");
        try {
            System.out.println("\nSender " + json.get("Sender"));
            System.out.println("\nRecipient " + json.get("Recipient"));
            System.out.println("\nData " + json.get("Data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    JSONObject toJ(String s) {
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    JSONArray getData(JSONObject json) {
        try {
            json.get("Data").toString();
            return json.getJSONArray("Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
