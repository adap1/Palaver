package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkTasks extends AsyncTask<String, Void, String> {

    private final Context ctx;
    String answer;
    String task;

    public NetworkTasks(Context ctx) {
        super();
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... strings) {

        String mainUrl = "http://palaver.se.paluno.uni-due.de";
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;

        task = strings[0];
        String name = strings[1];
        String psw = strings[2];

        if(task.equals("register")){

            try{
                URL url = new URL(mainUrl+"/api/user/register/");
                conn = setupConnection("POST", url);

                JSONObject post = new JSONObject();
                post.put("Username", name);
                post.put("Password", psw);

                writeToStream(post, conn);
                return InputStreamToString(conn);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

        }else if(task.equals("login")){

            try{
                URL url = new URL(mainUrl + "/api/user/validate/");
                conn = setupConnection("GET", url);

                JSONObject validattion = new JSONObject();
                validattion.put("Username", name);
                validattion.put("Password", psw);

                writeToStream(validattion, conn);
                return InputStreamToString(conn);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(conn != null)
                    conn.disconnect();
            }
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        answer = s;
        if(s.contains(":1")){
            answer = "success";
            if(task.equals("register")){
                Intent i = new Intent(ctx, LoginActivity.class);
                ctx.startActivity(i);
            }else if(task.equals("login")){
                Intent i = new Intent(ctx, HomeActivity.class);
                ctx.startActivity(i);
            }

        }
    }

    String InputStreamToString(HttpURLConnection conn){
        StringBuilder reply = new StringBuilder();
        try{
            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuilder response = new StringBuilder();
            while((line = reader.readLine()) != null){
                response.append(line);
            }
            reply.append(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            reply.append("Error while reading InputStream");
        }
        return reply.toString();
    }

    HttpURLConnection setupConnection(String connectionType, URL url){
        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(connectionType);
            conn.setRequestProperty("Content-Type",  "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conn;
    }

    void writeToStream(JSONObject json, HttpURLConnection conn){
        try{
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getResponse(){
       return answer;
    }
}
