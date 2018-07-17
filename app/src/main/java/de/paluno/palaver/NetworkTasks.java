package de.paluno.palaver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetworkTasks extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private String task, name, psw, target, message, token;

    NetworkTasks(Context ctx) {
        super();
        this.ctx = ctx;

    }

    @Override
    protected String doInBackground(String... strings) {

        String mainUrl = "http://palaver.se.paluno.uni-due.de";
        HttpURLConnection conn = null;

        try{
            task = strings[0];
            name = strings[1];
            psw = strings[2];
            target = strings[3];
            message = strings[4];
            token = strings[5];
        }catch (IndexOutOfBoundsException ignored){

        }

        try {
            switch (task) {
                case "register": {

                    URL url = new URL(mainUrl + "/api/user/register/");
                    conn = setupConnection("POST", url);

                    JSONObject post = new JSONObject();
                    post.put("Username", name);
                    post.put("Password", psw);

                    writeToStream(post, conn);
                    return InputStreamToString(conn);

                }
                case "login": {

                    URL url = new URL(mainUrl + "/api/user/validate/");
                    conn = setupConnection("GET", url);

                    JSONObject validation = new JSONObject();
                    validation.put("Username", name);
                    validation.put("Password", psw);

                    writeToStream(validation, conn);
                    return InputStreamToString(conn);

                }
                case "addFriend": {

                    URL url = new URL(mainUrl + "/api/friends/add");
                    conn = setupConnection("GET", url);

                    JSONObject addFriend = new JSONObject();
                    addFriend.put("Username", name);
                    addFriend.put("Password", psw);
                    addFriend.put("Friend", target);

                    writeToStream(addFriend, conn);
                    return InputStreamToString(conn);

                }
                case "getFriends": {

                    URL url = new URL(mainUrl + "/api/friends/get");
                    conn = setupConnection("GET", url);

                    JSONObject getFriends = new JSONObject();
                    getFriends.put("Username", name);
                    getFriends.put("Password", psw);

                    writeToStream(getFriends, conn);
                    return InputStreamToString(conn);

                }
                case "sendMessage": {

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

                }
                case "removeContact": {

                    URL url = new URL(mainUrl + "/api/friends/remove");
                    conn = setupConnection("POST", url);

                    JSONObject removeContact = new JSONObject();
                    removeContact.put("Username", name);
                    removeContact.put("Password", psw);
                    removeContact.put("Friend", target);

                    writeToStream(removeContact, conn);
                    return InputStreamToString(conn);

                }
                case "getMessage": {

                    URL url = new URL(mainUrl + "/api/message/get");
                    conn = setupConnection("GET", url);

                    JSONObject getMessage = new JSONObject();
                    getMessage.put("Username", name);
                    getMessage.put("Password", psw);
                    getMessage.put("Recipient", target);

                    writeToStream(getMessage, conn);
                    return InputStreamToString(conn);

                }
                case "pushtoken": {

                    URL url = new URL(mainUrl + "/api/user/pushtoken");
                    conn = setupConnection("GET", url);

                    JSONObject pushtoken = new JSONObject();
                    pushtoken.put("Username", name);
                    pushtoken.put("Password", psw);
                    pushtoken.put("PushToken", token);

                    writeToStream(pushtoken, conn);
                    return InputStreamToString(conn);
                }

            }

        } catch (IOException | JSONException e) {
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

        //TODO remove after app finished
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();

        if (s.contains(":1")) {
            switch (task) {
                case "register": {
                    Intent i = new Intent(ctx, LoginActivity.class);
                    ctx.startActivity(i);
                    break;
                }
                case "login": {
                    Intent i = new Intent(ctx, HomeActivity.class);
                    ctx.startActivity(i);
                    break;
                }
                case "addFriend":
                    break;
                case "getFriends": {
                    StringBuilder names = new StringBuilder();
                    JSONObject json = toJ(s);
                    assert json != null;
                    JSONArray jsonArray = getData(json);
                    boolean first = true;
                    assert jsonArray != null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if (first) {
                                first = false;
                            } else {
                                names.append(", ");
                            }
                            names.append(jsonArray.get(i).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent i = new Intent(ctx, ContactsActivity.class);
                    i.putExtra("Data", names.toString());
                    ctx.startActivity(i);
                    break;
                }
                case "sendMessage":
                    Toast.makeText(ctx, s, Toast.LENGTH_SHORT);
                    break;
                case "removeContact":

                    Toast.makeText(ctx, s, Toast.LENGTH_SHORT);
                    break;
                case "getMessage": {
                    StringBuilder messages = new StringBuilder();
                    JSONObject json = toJ(s);
                    assert json != null;
                    JSONArray jsonArray = getData(json);
                    List<JSONObject> msgJSON = new ArrayList<>();
                    boolean first = true;
                    assert jsonArray != null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if (first) {
                                first = false;
                            } else {
                                messages.append("; ");
                            }
                            messages.append(jsonArray.get(i).toString());
                            msgJSON.add((JSONObject) jsonArray.get(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    String[] msgArray = new String[msgJSON.size()];
                    int count = 0;
                    for (JSONObject j : msgJSON) {
                        msgArray[count++] = j.toString();
                    }

                    Intent i = new Intent(ctx, ProfileActivity.class);
                    i.putExtra("DataAsString", messages.toString());
                    i.putExtra("Data", msgArray);
                    i.putExtra("Name", target);
                    i.putExtra("rawData", s);
                    ctx.startActivity(i);
                    break;
                }
                case "pushtoken":
                    Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
                    break;
            }

        }else if(s.contains(":0")){
            Toast.makeText(ctx, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject toJ(String s) {
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

    private JSONArray getData(JSONObject json) {
        try {
            json.get("Data").toString();
            return json.getJSONArray("Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InputStreamToString(HttpURLConnection conn) {
        StringBuilder reply = new StringBuilder();
        try {
            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
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

    private HttpURLConnection setupConnection(String connectionType, URL url) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conn;
    }

    private void writeToStream(JSONObject json, HttpURLConnection conn) {
        try {
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
