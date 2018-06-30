package de.paluno.palaver;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {

    Button back, register;
    RelativeLayout rellay1, rellay2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();

            }
        });

        register = findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterProcess();

            }
        });
    }

    void startLoginActivity() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        i.putExtra("SkipSplash", true);
        startActivity(i);
    }

    void startRegisterProcess() {
        sendRegisterData();
        startLoginActivity();
    }

    void sendRegisterData() {
        //send Data to Server to register

        EditText _name =  findViewById(R.id.username);
        EditText _psw =  findViewById(R.id.password);
        String name = _name.getText().toString();
        String psw = _psw.getText().toString();

        //new BackgroundTasks().execute("register", name, psw);
        new TasksBackground("REGISTER", name, psw).execute();
    }

}
