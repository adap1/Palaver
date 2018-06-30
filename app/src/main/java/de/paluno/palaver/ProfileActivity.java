package de.paluno.palaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    Button home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null){
            String s = b.getString("Name");
            TextView tv = findViewById(R.id.view_profile_name);
            tv.setText(s);
        }

        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });

    }

    void startHomeActivity(){
        Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(i);
    }

}
