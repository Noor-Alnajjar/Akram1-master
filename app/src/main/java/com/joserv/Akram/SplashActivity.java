package com.joserv.Akram;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.config.Config;

import com.crashlytics.android.Crashlytics;
import com.joserv.activities.LoginActivity;
import com.libraries.usersession.UserAccessSession;

import io.fabric.sdk.android.Fabric;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SplashActivity extends AppCompatActivity {


    private long                startTimeMillis     = 0;
    private int                 timeoutMillis       = 1000;
    private static final int    PERMISSIONS_REQUEST = 1234;

    //GPS Checking

    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean gpsDialog = false;
    AlertDialog gpsAlertDialog = null;



    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;



        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions == null) {
            return new String[0];
        } else {
            return permissions.clone();
        }
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        setContentView(R.layout.activity_splash);

        startTimeMillis = System.currentTimeMillis();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }



         Activity activity = SplashActivity.this;
        Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);



            // only for lolipop and newer versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(ContextCompat.getColor(activity,R.color.color_white));
            }

            //while(!gps_enabled && !network_enabled)
            //checkGps();
            startNextActivity();


        //if (Build.VERSION.SDK_INT >= 23) {
            //checkPermissions();
        //} else {

        //}

        /*Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                Intent intent = new Intent(SplashActivity.this, LocationActivity.class);
                //startActivity(intent);
                //finish();
                checkGps();

            }
        }, 10000);*/






    }

    private  void checkGps(){


        LocationManager lm =
                (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        if(gpsDialog==false){
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        //while(!gps_enabled && !network_enabled){
        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            //dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setMessage("Please Enable Location, Open location settings?");

            //dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    gpsDialog=false;
                    //get gps
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();


                }
            });
            gpsDialog = true;
            gpsAlertDialog=dialog.show();
        }else{
            if(gpsAlertDialog!=null)
            gpsAlertDialog.dismiss();

            startNextActivity();
        }
        }

    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            checkPermissions();
        }
    }


    private void startNextActivity() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.e("loc","Granted");
            }
        });
        long delayMillis = getTimeoutMillis() - (System.currentTimeMillis() - startTimeMillis);
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(checkFirstRun() && UserAccessSession.getInstance(SplashActivity.this).getUserSession() == null ){
                    Intent intent = new Intent(SplashActivity.this, HowToPlay2.class);
                    startActivity(intent);
                    finish();
                }else if(checkFirstRun() && UserAccessSession.getInstance(SplashActivity.this).getUserSession() != null ){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("LauncherActivity","SplashActivity");
                    startActivity(intent);
                    finish();
                }else if(UserAccessSession.getInstance(SplashActivity.this).getUserSession() == null){
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("LauncherActivity","SplashActivity");
                startActivity(intent);
                finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("LauncherActivity","SplashActivity");
                    startActivity(intent);
                    finish();
                }
            }
        }, delayMillis);
    }

    private boolean checkFirstRun() {

        final String PREFS_NAME = "AkramOpening";
        final String PREF_VERSION_CODE_KEY = "30";
        final int DOESNT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
            // This is just a normal run
            return false;
        } else if (savedVersionCode == DOESNT_EXIST) {
            // Update the shared preferences with the current version code
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
          return true;
        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }
        return false;
    }

    private void checkPermissions() {
        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
        if (ungrantedPermissions.length == 0) {
            startNextActivity();
        } else {
            requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST);


        }
    }

    @TargetApi(23)
    private String[] requiredPermissionsStillNeeded() {

        Set<String> permissions = new HashSet<String>();
        for (String permission : getRequiredPermissions()) {
            permissions.add(permission);
        }
        for (Iterator<String> i = permissions.iterator(); i.hasNext();) {
            String permission = i.next();
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(SplashActivity.class.getSimpleName(),
                        "Permission: " + permission + " already granted.");
                i.remove();
            } else {
                Log.d(SplashActivity.class.getSimpleName(),
                        "Permission: " + permission + " not yet granted.");
            }
        }

        return permissions.toArray(new String[permissions.size()]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
                    startNextActivity();

        }
    }

}
