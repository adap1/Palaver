package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    EditText addName;
    Button home, addContact;
    LinearLayout group_Buttons;

    NetworkTasks nt;
    SharedPreferences prefs;
    List<String> names;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        prefs = getSharedPreferences("", MODE_PRIVATE);
        nt = new NetworkTasks(this);

        handleExtras();

        group_Buttons = findViewById(R.id.group_buttons);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        for(String n : names){
            final String name = n;
            Button b = new Button(this);
            params.setMargins(0, 15, 0, 0);
            b.setLayoutParams(params);
            b.setText(name);
            b.setTextColor(Color.WHITE);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startProfileActivity(name);
                }
            });
            group_Buttons.addView(b);
        }


        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });

        addContact = findViewById(R.id.btn_addContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _name = findViewById(R.id.et_addName);
                String name = _name.getText().toString();

                addContact(name);
            }
        });

    }

    void startHomeActivity(){
        Intent i = new Intent(ContactsActivity.this, HomeActivity.class);
        startActivity(i);
    }

    void startProfileActivity(String owner){
        getMessages(owner);

//        Intent i = new Intent(ContactsActivity.this, ProfileActivity.class);
//        i.putExtra("Name", owner);
//        startActivity(i);
    }

    private void getMessages(String owner) {
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");
        new NetworkTasks(this).execute("getMessage", name, psw, owner);
    }

    void addContact(String friend){
        String name = prefs.getString("Username", "");
        String psw = prefs.getString("Password", "");

        new NetworkTasks(this).execute("addFriend", name, psw, friend);
    }

    private void handleExtras() {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            String s = (String) b.get("Data");
            String originalS = s;
            if(s != null && !s.isEmpty()) {
                names = new ArrayList<>();

                while (s.indexOf(",") != -1) {
                    int comma = s.indexOf(",");
                    String temp = s.substring(0, comma);
                    names.add(temp);
                    s = s.substring(comma+2);
                }
                if(!s.contains(",") && !s.isEmpty()){
                    names.add(s);
                }

//                System.out.println("String of names "+ originalS);
//                System.out.println("List of names "+ names.toString());
            }
        }
    }

}
