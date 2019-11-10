package com.application;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import com.config.Config;
import com.config.UIConfig;
import com.db.DbHelper;
import com.db.Queries;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.libraries.location.MGLocationManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.test.GPSTracker;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mg on 19/07/16.
 */
public class PokemonApplication extends MultiDexApplication implements GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "PokemonApplication";
    private final int INTERVAL = 5000;
    private final int FAST_INTERVAL = 1000;
    private static DisplayImageOptions options;
    private static ImageLoader imageLoader;
    private static DisplayImageOptions optionsThumb;
    public static Location currentLocation;
    private static Queries q;
    private static SQLiteDatabase db;
    private static DbHelper dbHelper;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public static    OnLocationListener mCallbackLocation;
    public GetAddressTask getAddressTask;
    public static List<Address> address;
    public static ArrayList<NameValuePair> params;
    private Activity mActivity;
    private MGLocationManager manager;
    private static PokemonApplication instance;
    private Handler customHandler = new Handler();
    public GPSTracker gpsTracker;
    Runnable updateGpsThread = new Runnable() {
        public void run() {
            if (gpsTracker.canGetLocation()) {
                Location lastLocation = gpsTracker.getLocation();
                double longitude = lastLocation.getLongitude();
                double latitude = lastLocation.getLatitude();
                Log.i(TAG, "run: " + longitude + ", " + latitude);
                final Location location = new Location("currentLocation");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                currentLocation = location;
                if(mCallbackLocation != null)
                    mCallbackLocation.onLocationChanged(currentLocation, location);
            }
            customHandler.postDelayed(this, 1000 *1 );
        }
    };

    public static DisplayImageOptions getDisplayImageOptionsThumbInstance() {
        if(optionsThumb == null) {
            optionsThumb = new DisplayImageOptions.Builder()
                    .showImageOnLoading(UIConfig.IMAGE_PLACEHOLDER_PROFILE_THUMB)
                    .showImageForEmptyUri(UIConfig.IMAGE_PLACEHOLDER_PROFILE_THUMB)
                    .showImageOnFail(UIConfig.IMAGE_PLACEHOLDER_PROFILE_THUMB)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }
        return optionsThumb;
    }

    public static Queries getQueriesInstance(Context context) {
        if(q == null) {
            dbHelper = new DbHelper(context);
            q = new Queries(db, dbHelper);
        }
        return q;
    }

    public static DisplayImageOptions getDisplayImageOptionsInstance() {
        if(options == null) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(UIConfig.IMAGE_PLACEHOLDER_POKEMON)
                    .showImageForEmptyUri(UIConfig.IMAGE_PLACEHOLDER_POKEMON)
                    .showImageOnFail(UIConfig.IMAGE_PLACEHOLDER_POKEMON)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }
        return options;
    }

    public static ImageLoader getImageLoaderInstance(Context context) {

        if(imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                 .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

//        if(!mGoogleApiClient.isConnected())
//            mGoogleApiClient.connect();
        getDebugKey();

        //startGPSLocation();
    }

    public void startGPSLocation() {
        gpsTracker = new GPSTracker(this);
        customHandler.post(updateGpsThread);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(FAST_INTERVAL); // Update location every second
            mLocationRequest.setFastestInterval(FAST_INTERVAL);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed" + connectionResult.getErrorMessage());
    }

    public void connectLocation() {
        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

        q.closeDatabase();
    }

    //    @Override
    public void onLocationChanged(Location loc) {
        Log.e(TAG, "onLocationChanged" + " lon = " + String.valueOf(loc.getLongitude()) +", Lat =" + String.valueOf(loc.getLatitude()));

        if(Config.SHOW_LOCATION_COORDINATES_LOG || (currentLocation == null && loc != null) )
            Log.e("Location LOG", "Location Updated [" + loc.getLatitude() + "," + loc.getLongitude() + "]");
        if(Config.DEBUG_LOCATION) {
            currentLocation.setLatitude(Config.DEBUG_LATITUDE);
            currentLocation.setLongitude(Config.DEBUG_LONGITUDE);
        }

        currentLocation = loc;


        if(mCallbackLocation != null)
            mCallbackLocation.onLocationChanged(currentLocation, loc);

        // if(address == null) {
        //     getAddressTask = new GetAddressTask(this);
        //     getAddressTask.execute(currentLocation);
        // }
    }

    protected class GetAddressTask extends AsyncTask<Location, Void, String> {
        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;
        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {
            // Required by the semantics of AsyncTask
            super();
            // Set a Context for the background task
            localContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location location = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            // Try to get an address for the current location. Catch IO or network problems.
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                // Catch network or other I/O problems.
            } catch (IOException exception1) {
                // print the stack trace
                exception1.printStackTrace();
                // Catch incorrect latitude or longitude values
            } catch (IllegalArgumentException exception2) {
                exception2.printStackTrace();
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                address = addresses;
                Address _address = address.get(0);
                String locality = _address.getLocality();
                String countryName = _address.getCountryName();
                String addressStr = String.format("%s, %s", locality, countryName);
                Log.e("Location LOG", addressStr);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String address) { }
    }

    public void getDebugKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", "------------------------------------------");
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e("KeyHash:", "------------------------------------------");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // ====================================================================================
    // ====================================================================================
    // ====================================================================================
    // ====================================================================================
    // ====================================================================================
    // ====================================================================================
    public interface OnLocationListener {
        public void onLocationChanged(Location prevLoc, Location currentLoc);
        public void onLocationRequestDenied();
    }

    public void setOnLocationListener(OnLocationListener listener, Activity activity) {
        try {
            mCallbackLocation = (OnLocationListener) listener;
            mActivity = activity;


            // checkLocationIsInit();
        } catch (ClassCastException e)  {
            throw new ClassCastException(this.toString() + " must implement OnLocationListener");
        }
    }

    private void checkLocationIsInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(manager == null && mActivity != null) {
                manager = new MGLocationManager(mActivity);
                manager.setOnLocationListenerM(new MGLocationManager.OnLocationListenerM() {
                    @Override
                    public void onLocationChanged(Location currentLoc) {
                        if(mCallbackLocation != null) {
                            updateLocation(currentLoc);
                        }
                    }

                    @Override
                    public void onLocationRequestDenied() {
                        if(mCallbackLocation != null)
                            mCallbackLocation.onLocationRequestDenied();
                    }
                });
            }
            else {
                manager.removeLocationUpdates();
            }

            Location location = manager.checkLocationPermission();
            if(location != null)
                updateLocation(location);
        }
    }

    public MGLocationManager getMGLocationManager() {
        return manager;
    }

    private void updateLocation(Location loc) {
        if(Config.SHOW_LOCATION_COORDINATES_LOG || (currentLocation == null && loc != null) )
            Log.e("Location LOG", "Location Updated [" + loc.getLatitude() + "," + loc.getLongitude() + "]");

        currentLocation = loc;
        if(Config.DEBUG_LOCATION) {
            currentLocation.setLatitude(Config.DEBUG_LATITUDE);
            currentLocation.setLongitude(Config.DEBUG_LONGITUDE);
        }

        if(mCallbackLocation != null)
            mCallbackLocation.onLocationChanged(currentLocation, loc);

        // if(address == null) {
        //     getAddressTask = new GetAddressTask(this);
        //     getAddressTask.execute(currentLocation);
        // }
    }

    public GoogleApiClient getGoogleApiClientInstance() {
        return mGoogleApiClient;
    }

    public static PokemonApplication getInstance() {
        return instance;
    }


}
