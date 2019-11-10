package com.joserv.FirebaseService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.joserv.Akram.BuildConfig;
import com.joserv.Akram.MainActivity;
import com.joserv.Akram.R;
import com.joserv.Akram.SplashActivity;
import com.joserv.activities.NotificationHistory;
import com.libraries.dbtiny.TinyDB;
import com.models.PushNotification;
import com.services.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;
/**
 * Created by user on 3/19/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationHelper notificationHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FireBaseMessage", "Message Notification Body: ");
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

        }

        if(remoteMessage.getNotification().getTitle().equals("test") && !BuildConfig.DEBUG){
            Log.d("FireBaseMessage", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            return;
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null ) {
            Log.d("FireBaseMessage", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            PushNotification pushNotification = new PushNotification();
            pushNotification.setBody(remoteMessage.getNotification().getBody());
            pushNotification.setTitle(remoteMessage.getNotification().getTitle());
            pushNotification.setDate(formattedDate);

            TinyDB tinydb = new TinyDB(MyFirebaseMessagingService.this);
            ArrayList<String> notificationHis = new ArrayList<String>();
            notificationHis = tinydb.getListString("notificationHistory");

            Gson gson = new Gson();
            String json = gson.toJson(pushNotification);

            notificationHis.add(json);

            tinydb.putListString("notificationHistory",notificationHis);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }


    private void sendNotification(String title,String desc) {
        notificationHelper=new NotificationHelper(this);
        notificationHelper. createNotification(title,desc);
    }
}
