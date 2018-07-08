package de.paluno.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NetworkTasks extends AsyncTask<String, Void, String> {

    private final Context ctx;
    String answer;
    String task, name, psw, target, message;

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

        try{
            task = strings[0];
            name = strings[1];
            psw = strings[2];
            target = strings[3];
            message = strings[4];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        try {
            if (task.equals("register")) {

                URL url = new URL(mainUrl + "/api/user/register/");
                conn = setupConnection("POST", url);

                JSONObject post = new JSONObject();
                post.put("Username", name);
                post.put("Password", psw);

                writeToStream(post, conn);
                return InputStreamToString(conn);

            } else if (task.equals("login")) {

                URL url = new URL(mainUrl + "/api/user/validate/");
                conn = setupConnection("GET", url);

                JSONObject validation = new JSONObject();
                validation.put("Username", name);
                validation.put("Password", psw);

                writeToStream(validation, conn);
                return InputStreamToString(conn);

            } else if (task.equals("addFriend")) {

                System.out.println("in networktask addFriends");
                URL url = new URL(mainUrl + "/api/friends/add");
                conn = setupConnection("GET", url);

                JSONObject addFriend = new JSONObject();
                addFriend.put("Username", name);
                addFriend.put("Password", psw);
                addFriend.put("Friend", target);

                writeToStream(addFriend, conn);
                return InputStreamToString(conn);

            } else if (task.equals("getFriends")) {

                URL url = new URL(mainUrl + "/api/friends/get");
                conn = setupConnection("GET", url);

                JSONObject getFriends = new JSONObject();
                getFriends.put("Username", name);
                getFriends.put("Password", psw);

                writeToStream(getFriends, conn);
                return InputStreamToString(conn);

            } else if( task.equals("sendMessage")){

                URL url = new URL(mainUrl + "/api/message/send");
                conn = setupConnection("POST", url);

                JSONObject sendMessage = new JSONObject();
                sendMessage.put("Username", name);
                sendMessage.put("Password", psw);
                sendMessage.put("Recipient", target);
                sendMessage.put("Mimetype", "text/plain");
                sendMessage.put("Data", message);

                writeToStream(sendMessage, conn);
                return InputStreamToString(conn);

            } else if(task.equals("removeContact")) {

                URL url = new URL(mainUrl + "/api/friends/remove");
                conn = setupConnection("POST", url);

                JSONObject removeContact = new JSONObject();
                removeContact.put("Username", name);
                removeContact.put("Password", psw);
                removeContact.put("Friend", target);

                writeToStream(removeContact, conn);
                return InputStreamToString(conn);

            } else if(task.equals("getMessage")){

                URL url = new URL(mainUrl + "/api/message/get");
                conn = setupConnection("GET", url);

                JSONObject getMessage = new JSONObject();
                getMessage.put("Username", name);
                getMessage.put("Password", psw);
                getMessage.put("Recipient", target);

                writeToStream(getMessage, conn);
                return InputStreamToString(conn);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        answer = s;
        if (s.contains(":1")) {
            answer = "success";
            if (task.equals("register")) {
                Intent i = new Intent(ctx, LoginActivity.class);
                ctx.startActivity(i);
            } else if (task.equals("login")) {
                Intent i = new Intent(ctx, HomeActivity.class);
                ctx.startActivity(i);
            } else if (task.equals("addFriend")) {
                System.out.println("postExecute addFriend " + getData(toJ(s)));
            } else if (task.equals("getFriends")) {
                StringBuilder names = new StringBuilder();
                JSONObject json = toJ(s);
                JSONArray jsonArray = getData(json);
                boolean first = true;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if (first){
                            first = false;
                        }else{
                            names.append(", ");
                        }
                        names.append(jsonArray.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Friends = " + names);
                Intent i = new Intent(ctx, ContactsActivity.class);
                i.putExtra("Data", names.toString());
                ctx.startActivity(i);
            } else if(task.equals("sendMessage")){

            } else if(task.equals("removeContact")){

            } else if(task.equals("getMessage")){
                StringBuilder messages = new StringBuilder();
                JSONObject json = toJ(s);
                JSONArray jsonArray = getData(json);
                List<JSONObject> msgJSON = new ArrayList<>();
                boolean first = true;
                for ( int i = 0; i < jsonArray.length(); i++){
                    try{
                        if(first){
                            first = false;
                        }else{
                            messages.append("; ");
                        }
                        messages.append(jsonArray.get(i).toString());
                        msgJSON.add((JSONObject)jsonArray.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String[] msgArray = new String[msgJSON.size()];
                int count = 0;
                for (JSONObject j : msgJSON){
                    msgArray[count++] = j.toString();
                }

                System.out.println("messages abgerufen "+ messages.toString());
                Intent i = new Intent(ctx, ProfileActivity.class);
                i.putExtra("DataAsString", messages.toString());
                i.putExtra("Data", msgArray);
                i.putExtra("Name", target);
                i.putExtra("rawData", s);
                ctx.startActivity(i);
            }

        }else if(s.contains(":0")){
            answer = "failure";
        }
    }

    JSONObject toJ(String s) {
        s.replace("{", "");
        s.replace("}", "");
        s.replace("[", "");
        s.replace("]", "");

        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getData(String s) {
        int start = s.indexOf("[") + 2;
        int end = s.indexOf("]") - 2;

        if (start != -1) {
            return s.substring(start, end);
        } else {
            return "";
        }
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

    String InputStreamToString(HttpURLConnection conn) {
        StringBuilder reply = new StringBuilder();
        try {
            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reply.append(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            reply.append("Error while reading InputStream");
        }
        return reply.toString();
    }

    HttpURLConnection setupConnection(String connectionType, URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(connectionType);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(15000);
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

    void writeToStream(JSONObject json, HttpURLConnection conn) {
        try {
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getResponse() {
        return answer;
    }
}
