package de.paluno.palaver;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("onMessageReceived", "You received a new message");
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("onNewToken", s);

        SharedPreferences prefs = getSharedPreferences("main", MODE_PRIVATE);
        prefs.edit().putString("Token", s).apply();

        try {
            String name = prefs.getString("Username", "");
            String psw = prefs.getString("Password", "");

            URL url = new URL("http://palaver.se.paluno.uni-due.de/api/user/pushtoken");
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            JSONObject pushtoken = new JSONObject();
            pushtoken.put("Username", name);
            pushtoken.put("Password", psw);
            pushtoken.put("PushToken", s);

            System.out.println("Pushtoken json sent\n"+pushtoken.toString()+"\n");

            OutputStream os = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(pushtoken.toString());
            writer.close();

            InputStream is = conn.getInputStream();

            StringBuilder reply = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reply.append(response.toString());

            Log.e("Reply to pushtoken to Server", reply.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);

        Log.e("onMessageSent", s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);

        Log.e("onSendError", s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        Log.e("onDeletedMessages", "deleted");
    }




}
