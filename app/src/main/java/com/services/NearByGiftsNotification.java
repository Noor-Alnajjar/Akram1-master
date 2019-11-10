package com.services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.config.Config;
import com.joserv.Akram.MainActivity;
import com.joserv.Akram.R;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;

import java.util.List;


public class NearByGiftsNotification extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1800000;
  //  private static final int LOCATION_INTERVAL = 18000;
    private static final float LOCATION_DISTANCE = 0f;
    private MGAsyncTaskNoDialog task;
    private NearByGiftsNotification myService;
    private NotificationHelper notificationHelper;
    private  DataResponse data;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e("k", "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            if(!MainActivity.active)
            isThereGift(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private void sendNotification() {
        notificationHelper=new NotificationHelper(this);
        notificationHelper. createNotification("Akram",getApplication().getResources().getString(R.string.notificationGift));
    }


    private void isThereGift(final Location location){
        task = new MGAsyncTaskNoDialog();
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {
            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) {

            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                data = null;
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub

                    try {
                        String strUrl = String.format("%s?api_key=%s&lat=%f&lon=%f&radius=%f&autorefresh=%s",
                                Config.GET_SIGHTING_URL,
                                Config.API_KEY,
                                location.getLatitude(),
                                location.getLongitude(),
                                0.20,
                                "True");
                        strUrl = convertToEnglishDigits(strUrl);

                        Log.e("URL1=>", strUrl);

                        DataParser parser = new DataParser();
                        DataResponse data = parser.getData(strUrl,getApplicationContext());
                        if (data == null) {
                            return;
                        }
                        if (data.getSightings() != null && data.getSightings().size() > 0) {
                            Log.e("URL1=>",data.getSightings().toString() );
                            sendNotification();
                        }else {
                            //notificationHelper.clearNotification();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });
        task.execute();


    }

    public static String convertToEnglishDigits(String value)
    {

        String newValue = value.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("٨", "8").replace("٩", "9").replace("٠", "0")
                .replace("٫",",").replace("٧","7");

        return newValue;
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        myService = new NearByGiftsNotification();
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        } catch (Exception e){
            Log.d(TAG, "network provider does not exist, " + e.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
