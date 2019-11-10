package com.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;


import com.joserv.Akram.MainActivity;
import com.joserv.Akram.R;

public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public Boolean notificationExist = false;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message)
    {

        if(!notificationExist){
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder = new NotificationCompat.Builder(mContext);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_stat_notification);
            mBuilder.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent);

        notificationExist = true;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.definite);
        mBuilder.setSound(soundUri);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

//            // Create pending intent, mention the Activity which needs to be
//            //triggered when user clicks on notification(StopScript.class in this case)
//
//            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
//                    new Intent(mContext, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            mBuilder.setContentIntent(contentIntent);

            notificationChannel.setSound(soundUri,audioAttributes);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }else {


        }

        assert mNotificationManager != null;
        mNotificationManager.notify(512 /* Request Code */, mBuilder.build());
        }
    }

    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(512);
        notificationExist=false;
    }


}