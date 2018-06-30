package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.app.PendingIntent.getActivity;

public class LoginActivity extends AppCompatActivity {


    Button login, register;
    RelativeLayout rellay1, rellay2, splash;
    CheckBox checkBox;
    Handler handler = new Handler();

    String url = "@http://palaver.se.paluno.uni-due.de/";

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
            splash.setVisibility(View.GONE);
        }
    };

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = this.getSharedPreferences("test", Context.MODE_PRIVATE);

        splash =  findViewById(R.id.splashScreen);
        rellay1 = findViewById(R.id.rellay1);
        rellay2 =  findViewById(R.id.rellay2);
        checkBox = findViewById(R.id.box_keepLoggedIn);

        long delay = 2000;

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null){
            Boolean bool = (Boolean) b.get("LogOut");
            Boolean bool2= (Boolean) b.get("SkipSplash");
            if((bool != null && bool) || (bool2 != null && bool2)) {
                delay = 0;
                prefs.edit().putBoolean("checked_login", false).apply();
            }
        }

        if(prefs.getBoolean("checked_login", false)){
            startHomeActivity();
        }

        handler.postDelayed(runnable, delay);


        register = findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterActivity();
            }
        });

        login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogInProcess();
            }
        });


    }

    void startHomeActivity(){
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
    }

    void startRegisterActivity(){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    void startLogInProcess(){
        EditText _name =  findViewById(R.id.username);
        EditText _psw =  findViewById(R.id.password);
        String name = _name.getText().toString();
        String psw = _psw.getText().toString();

        //new BackgroundTasks().execute("login", name, psw);
        new TasksBackground("LOGIN", name, psw).execute();
    }

    void validateDataAndLogIn(String name, String psw){

        if(validateData(name, psw)){
            handleBox();
            startHomeActivity();
        }else{
            TextView error = findViewById(R.id.view_invalid);
            error.setText("Invalid Log In Data");
        }
    }

    boolean validateData(final String name, final String psw){



        return true;
    }

    void handleBox(){
        if(checkBox.isChecked()){
            prefs.edit().putBoolean("checked_login", true).apply();
        }else{
            prefs.edit().putBoolean("checked_login", false).apply();
        }
    }

}
