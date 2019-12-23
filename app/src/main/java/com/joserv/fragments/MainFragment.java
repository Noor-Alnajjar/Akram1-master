package com.joserv.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.EventsAdapter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ankushgrover.hourglass.Hourglass;
import com.application.PokemonApplication;
import com.config.Config;
import com.config.UIConfig;
import com.db.Queries;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.joserv.Akram.BuildConfig;
import com.joserv.Akram.R;
import com.joserv.activities.DetailActivity;
import com.joserv.activities.EventsActivity;
import com.joserv.activities.LoginActivity;
import com.joserv.activities.ProfileActivity;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.imageview.MGHSquareImageView;
import com.libraries.location.MGLocationManagerUtils;
import com.libraries.seekbar.MGVerticalSeekBar;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Event;
import com.models.Gifts;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.test.PermissionManager;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static android.content.Context.SENSOR_SERVICE;

public class MainFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, SensorEventListener
        , GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainFragment";
    private View viewInflate;
    private GoogleMap googleMap;
    SupportMapFragment supportMapFragment;
    SwipeRefreshLayout swipeRefresh;
    MGAsyncTaskNoDialog task;
    ArrayList<Gifts> gifts;
    private HashMap<String, Gifts> markers;
    private ArrayList<Marker> markerList;
    private Gifts selectedSighting;
    double lat = 0;
    double lon = 0;
    Queries q;
    MarkerOptions customMarkerOptions;
    Marker customMarker;
    MGVerticalSeekBar seekBarRadius;
    SeekBar seekBar;
    TextView tvRadius, tvRadius1;
    TextView tvDistance;
    ImageView ivMyLocation;
    Circle mapCircle = null;
    double radius = Config.SLIDER_RADIUS_DEFAULT;
    float speed = 0;
    private ProgressBar progressBarTime, progressBarTime2, progressBarTime3;
    TextView tvSpeed, degree1;
    Boolean alertShown = false;
    //spoofing
    AlertDialog.Builder builder1;
    AlertDialog alertDialog;
    AlertDialog.Builder alert = null;
    //item info
    AlertDialog.Builder builder;
    AlertDialog alertDialog1;
    boolean notificationExist = false;
    String notificationItemID = "";
    String latestItemTakenID = "";
    int vibrateCounter = 0;
    boolean itemremoved = false;
    AlertDialog dismissAlert = null;

    //03-29
    private GoogleApiClient client;
    public static final int REQUEST_LOCATION_CODE = 99;
    private LocationRequest locationRequest;
    private final int FAST_INTERVAL = 1000;
    public static Location currentLocation;
    private UserSession userSession;

    final Handler handler = new Handler();

    //bearing smothing
    float oldbearing = 0.1f;
    float oldestbearing = 0.1f;

    private int countnumber = 0;
    private String nextmode;

    private ImageLoader imageLoader;
    private Drawable d;
    private List<String> itemsfound = new ArrayList<String>();

    private CircleOptions circleOptions;

    private Hourglass refreshCounter;
    private Hourglass counter;

    private boolean isInBackground = false;
    private String imageUrl = "";

    private FusedLocationProviderClient mFusedLocationClient;

    //Gray Gifts
    private DatabaseReference databaseUserCollection;
    private ArrayList<String> collectedGiftlist = new ArrayList<String>();//Creating arraylist.

    //Progress Bar for get gifts
    private ProgressBar getGiftProgressbar;

    //connectionrefresh + dialog
    private Hourglass hourglass;
    private MGLocationManagerUtils utils = null;

    //compass
    float currentDegree;
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Location locationForCompass;

    private Circle mCircle;
    private Marker mMarker;
    private SparseArray<Event> advData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_map, null);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        return viewInflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (task != null)
            task.cancel(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        isLocationEnable();
        internetConnection();
        checkConnection();
        currentLocation = new Location("currentLocation");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        getTempLocation();
        userSession = UserAccessSession.getInstance(getContext()).getUserSession();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));

        gifts = new ArrayList<Gifts>();
        swipeRefresh = (SwipeRefreshLayout) viewInflate.findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0, 100);
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Refreshed token:", "Refreshed token: " + refreshedToken);

        if (userSession != null) {
            databaseUserCollection = FirebaseDatabase.getInstance().getReference().child("Akram")
                    .child(String.valueOf(userSession.getId())).child("Collection");
            getGiftsList();
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        showRefresh(false);

        q = PokemonApplication.getQueriesInstance(getContext());

        FragmentManager fManager = getActivity().getSupportFragmentManager();
        supportMapFragment = ((SupportMapFragment) fManager.findFragmentById(R.id.googleMap));
        if (supportMapFragment == null) {
            fManager = getChildFragmentManager();
            supportMapFragment = ((SupportMapFragment) fManager.findFragmentById(R.id.googleMap));
        }
        supportMapFragment.getMapAsync(this);

        markers = new HashMap<String, Gifts>();
        markerList = new ArrayList<Marker>();

        seekBarRadius = (MGVerticalSeekBar) viewInflate.findViewById(R.id.seekBarRadius);
        getGiftProgressbar = (ProgressBar) viewInflate.findViewById(R.id.PBGetGifts);
        tvRadius = (TextView) viewInflate.findViewById(R.id.tvRadius);
        tvRadius1 = (TextView) viewInflate.findViewById(R.id.tvRadius1);
        tvDistance = (TextView) viewInflate.findViewById(R.id.tvMode);
        tvSpeed = (TextView) viewInflate.findViewById(R.id.tvSpeed);
        degree1 = (TextView) viewInflate.findViewById(R.id.tvMode2);
        progressBarTime = (ProgressBar) viewInflate.findViewById(R.id.progressTimeMode);
        progressBarTime2 = (ProgressBar) viewInflate.findViewById(R.id.progressTimeMode2);
        progressBarTime3 = (ProgressBar) viewInflate.findViewById(R.id.progressTimeMode3);


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            progressBarTime.setVisibility(View.GONE);
            progressBarTime2.setVisibility(View.GONE);
            progressBarTime3.setVisibility(View.GONE);
        }
        seekBarRadius.setMax(Config.SLIDER_RADIUS_MAX);
        seekBarRadius.setProgress(Config.SLIDER_RADIUS_DEFAULT);
        updateSlider(seekBarRadius.getProgress());
        seekBarRadius.setOnSeekBarChangeListener(new MGVerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress > 0) {
                    updateSlider(progress);
                } else {
                    seekBarRadius.setProgress(1);
                    updateSlider(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getData();
                // updateProfile();
            }
        });
        ivMyLocation = (ImageView) viewInflate.findViewById(R.id.ivMyLocation);
        initSeekbar();
        counter();
    }

    private void getGiftsList() {
        databaseUserCollection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectedGiftlist.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshotCollection : dataSnapshot.getChildren()) {
                        if (snapshotCollection.child("item_id").exists()) {
                            collectedGiftlist.add(snapshotCollection.child("item_id").getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void getTempLocation() {
        if (checkLocationPermission()) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        currentLocation = location;
                    }
                }
            });
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        degree1.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        //image.startAnimation(ra);
        currentDegree = -((degree + 21) % 360);

        if (locationForCompass != null && googleMap != null) {
            //updateCameraBearing(googleMap, -currentDegree,locationForCompass);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void checkConnection() {
        try {
            hourglass = new Hourglass(10000, 1000) {
                @Override
                public void onTimerTick(long timeRemaining) {
                }

                @Override
                public void onTimerFinish() {
                    try {
                        // Timer finished
                        isLocationEnable();
                        internetConnection();
                        checkConnection();
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    }
                }
            };
            hourglass.startTimer();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void refreshCounter() {
        try {
            refreshCounter = new Hourglass(50000, 1000) {
                @Override
                public void onTimerTick(long timeRemaining) {
                    // Update UI
                    ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                    ActivityManager.getMyMemoryState(myProcess);
                    isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;

                    if (isInBackground && !counter.isPaused()) {
                        //refreshCounter.pauseTimer();
                        counter.pauseTimer();
                        //hourglass.pauseTimer();
                    } else if (counter.isPaused() && !isInBackground) {
                        onLocationChanged(currentLocation);
                        counter.resumeTimer();
                        //hourglass.resumeTimer();
                    }
                }

                @Override
                public void onTimerFinish() {
                    // Timer finished
                    getData();
                    // updateProfile();
                    Log.e("Counter", "Finished");
                }
            };
            refreshCounter.startTimer();
        } catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void counter() {
        try {
            mFusedLocationClient.getApplicationContext();
            //Todo add arabic translation
            if (countnumber == 0) {
                nextmode = mFusedLocationClient.getApplicationContext().getResources().getString(R.string.to_advance);
            } else if (countnumber == 1) {
                nextmode = mFusedLocationClient.getApplicationContext().getResources().getString(R.string.to_extreme);
            } else {
                nextmode = mFusedLocationClient.getApplicationContext().getResources().getString(R.string.max_stage);
            }

            if (!tvDistance.getText().equals(mFusedLocationClient.getApplicationContext().getResources().getString(R.string.extreme))) {

                counter = new Hourglass(300000, 1000) {
                    @Override
                    public void onTimerTick(long timeRemaining) {
                        // Update UI
                        tvSpeed.setText("" + String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes(timeRemaining),
                                TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemaining))) + nextmode
                        );

                        if (countnumber == 0)
                            progressBarTime.setProgress((int) TimeUnit.MILLISECONDS.toSeconds((300000 - timeRemaining)));
                        else if (countnumber == 1)
                            progressBarTime2.setProgress((int) TimeUnit.MILLISECONDS.toSeconds((300000 - timeRemaining)));
                        else
                            progressBarTime3.setProgress((int) TimeUnit.MILLISECONDS.toSeconds((300000 - timeRemaining)));
                    }

                    @Override
                    public void onTimerFinish() {
                        // Timer finished
                        try {
                            countnumber++;
                            if (countnumber == 1) {
                                counter();
                                radius = radius + 10;
                                tvDistance.setText(getContext().getResources().getString(R.string.advance));
                                updateSeekbar((int) radius);

                            } else if (countnumber == 2) {
                                tvDistance.setText(getContext().getResources().getString(R.string.extreme));
                                radius = radius + 10;
                                updateSeekbar((int) radius);
                                tvSpeed.setText(getContext().getResources().getString(R.string.max_stage));
                            }
                            //Todo add arabic translation

                        } catch (Exception e) {

                        }
                    }
                };
                counter.startTimer();
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return false;
        } else
            return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(FAST_INTERVAL); // Update location every second
            locationRequest.setFastestInterval(FAST_INTERVAL);
            try {
                if (ContextCompat.checkSelfPermission(MainFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
                }
            } catch (Exception ex) {
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            locationForCompass = location;
            if (counter.isPaused()) {
                counter.resumeTimer();
                refreshCounter.resumeTimer();
            }
            try {
                if (location.isFromMockProvider() == true && !BuildConfig.DEBUG) {
                    if (builder1 == null) {
                        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        final View promptView = layoutInflater.inflate(R.layout.mocking_on, null);

                        builder1 = new AlertDialog.Builder(getContext());
                        builder1.setView(promptView);

                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        Button btnclossapp = (Button) promptView.findViewById(R.id.btncloseapp);
                        btnclossapp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.exit(1);
                            }
                        });
                        alertDialog.show();
                    }
                }
            } catch (Exception e) {
                Log.e("Error mocking", "Error occurred on mocking");
            }
            currentLocation = location;
            lat = location.getLatitude();
            lon = location.getLongitude();
            speed = location.getSpeed();
            Log.e(TAG, "onLocationChanged" + " lon = " + lon + ", Lat =" + lat);

            if (speed > 10 && !alertShown) {

                if (builder1 == null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                    final View promptView = layoutInflater.inflate(R.layout.mocking_on, null);

                    builder1 = new AlertDialog.Builder(getContext());
                    builder1.setView(promptView);
                    alertDialog = builder1.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    Button btnclossapp = (Button) promptView.findViewById(R.id.btncloseapp);
                    TextView txtmsg = (TextView) promptView.findViewById(R.id.txtmsg);

                    txtmsg.setText(mFusedLocationClient.getApplicationContext().getResources().getString(R.string.foryoursefty));
                    btnclossapp.setText(mFusedLocationClient.getApplicationContext().getResources().getString(R.string.ok));
                    alertShown = true;


                    btnclossapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            builder1 = null;
                        }
                    });

                    alertDialog.show();
                }
            }
            if (googleMap != null) {
                createCustomMarker();
                visibleMarker();
                if (!markerList.isEmpty()) {
                    updateCameraBearing(googleMap, -currentDegree, location);
                }
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void updateCameraBearing(GoogleMap googleMap, float bearing, Location location) {
        try {
            if (googleMap == null) return;
            if (countnumber == 0 || countnumber == 1) {
                Log.e("bearing1", String.valueOf(bearing));
                LatLng markerPosition = new LatLng(location.getLatitude(), location.getLongitude());

                CameraPosition camPos = CameraPosition
                        .builder(
                                googleMap.getCameraPosition() // current Camera
                        )
                        .target(markerPosition)
                        .zoom(Config.MAP_ZOOM_LEVEL)
                        .tilt(50f)
                        .bearing(bearing)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                Log.e("bearing", String.valueOf(bearing));
            } else {
                Log.e("bearing1", String.valueOf(bearing));
                LatLng markerPosition = new LatLng(location.getLatitude(), location.getLongitude());

                CameraPosition camPos = CameraPosition
                        .builder(
                                googleMap.getCameraPosition() // current Camera
                        )
                        .target(markerPosition)
                        .zoom(18)
                        .tilt(50f)
                        .bearing(bearing)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                Log.e("bearing", String.valueOf(bearing));
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // for the system's orientation sensor registered listeners
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == PermissionManager.CODE_CUSTOM_PERM) {
                if (PermissionManager.checkPermissionGrantResult(grantResults)) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(false);
                        googleMap.getUiSettings().setCompassEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
//                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);
//                        googleMap.getUiSettings().setRotateGesturesEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_LEVEL));
                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {
                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                // TODO Auto-generated method stub
                                lat = marker.getPosition().latitude;
                                lon = marker.getPosition().longitude;
                                getData();
                                // updateProfile();
                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {
                            }
                        });

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                animateMarker(marker);

                                String title = MGUtilities.getStringFromResource(getActivity(), R.string.current_location);
                                String titleMarker = marker.getTitle();
                                if (customMarker != null && titleMarker.compareTo(title) == 0) {
                                    Log.e("marker.getId()", marker.getId());
                                    Log.e("customMarker.getId()", customMarker.getId());
                                }
                                return true;
                            }
                        });

                        googleMap.setOnInfoWindowClickListener(this);
                        createCustomMarker();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 1000ms
                                getData();
                                // updateProfile();
                            }
                        }, 10000);
                    }
                }
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void animateMarker(final Marker marker) {
        try {
            //vibrate
            try {
                Vibrator v = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                v.vibrate(200);
            } catch (Exception e) {
                Log.e("Vibrat", "Error vibrate device");
            }


            //jump animation
            final Handler handler = new Handler();

            final long startTime = SystemClock.uptimeMillis();
            final long duration = 300; // ms

            Projection proj = googleMap.getProjection();
            final LatLng markerLatLng = marker.getPosition();
            Point startPoint = proj.toScreenLocation(markerLatLng);
            startPoint.offset(0, -10);
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - startTime;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later (60fps)
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            client = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            client.connect();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    void initSeekbar() {
        try {
            seekBar = (SeekBar) viewInflate.findViewById(R.id.camera_sb_raius);
            seekBar.setMax(Config.SLIDER_RADIUS_MAX);
            seekBar.setProgress(Config.SLIDER_RADIUS_DEFAULT);
            updateSeekbar(seekBar.getProgress());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    if (progress >= 1) {
                        updateSeekbar(progress);
                    } else {
                        seekBar.setProgress(1);
                        updateSeekbar(1);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    public void updateSeekbar(int progress) {
        try {
            String val = String.format("%d %s", progress, MGUtilities.getStringFromResource(getActivity(), R.string.m));
            tvRadius1.setText(val);
            radius = (double) progress;
            if (googleMap != null && radius > 0) {
                if (countnumber == 1) {
                    circleOptions = new CircleOptions()
                            .center(new LatLng(lat, lon))   //set center
                            .radius(radius)   //set radius in meters
                            .strokeColor(Color.TRANSPARENT)
                            .fillColor(0x555751FF)
                            .strokeWidth(5);
                    Circle newMapCircle = googleMap.addCircle(circleOptions);
                    if (mapCircle != null) mapCircle.remove();
                    mapCircle = newMapCircle;
                } else {
                    circleOptions = new CircleOptions()
                            .center(new LatLng(lat, lon))   //set center
                            .radius(radius)   //set radius in meters
                            .strokeColor(Color.TRANSPARENT)
                            .fillColor(0x55ff5751)
                            .strokeWidth(5);
                    Circle newMapCircle = googleMap.addCircle(circleOptions);
                    if (mapCircle != null) mapCircle.remove();
                    mapCircle = newMapCircle;
                }
                if (gifts != null && gifts.size() > 0) {
                    visibleMarker();
                }
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    void isLocationEnable() {
        try {
            if (!MGUtilities.isLocationEnabled(getActivity()) && PokemonApplication.getInstance().currentLocation == null && utils == null) {
                utils = new MGLocationManagerUtils();
                utils.setOnAlertListener(new MGLocationManagerUtils.OnAlertListener() {
                    @Override
                    public void onPositiveTapped() {
                        startActivityForResult(
                                new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                Config.PERMISSION_REQUEST_LOCATION_SETTINGS);
                        utils = null;
                    }

                    @Override
                    public void onNegativeTapped() {
                        showRefresh(false);
                        utils = null;
                    }
                });
                utils.showAlertView(
                        getActivity(),
                        R.string.location_error,
                        R.string.gps_not_on,
                        R.string.go_to_settings,
                        R.string.cancel);
            } else {
                if (PokemonApplication.currentLocation != null) {
                    lat = PokemonApplication.currentLocation.getLatitude();
                    lon = PokemonApplication.currentLocation.getLongitude();
                }
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    public void updateSlider(int progress) {
        try {
            String val = String.format("%d %s", progress, MGUtilities.getStringFromResource(getActivity(), R.string.km));
            tvRadius.setText(val);
            radius = (double) progress;

            if (googleMap != null && radius > 0) {
                if (mapCircle != null) mapCircle.remove();
                circleOptions = new CircleOptions()
                        .center(new LatLng(lat, lon))   //set center
                        .radius(radius * 1000)   //set radius in meters
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(0x555751FF)
                        .strokeWidth(5);
                mapCircle = googleMap.addCircle(circleOptions);
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        googleMap = _googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setIndoorEnabled(true);
        googleMap.getFocusedBuilding();
        googleMap.setBuildingsEnabled(true);

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            private float currentZoom = -1;

            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom != currentZoom){
                    currentZoom = pos.zoom;
                    Log.e("test", "1");
                    getEvent();
                }
            }
        });

        //getData();

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            // boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle));

//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        //03-29
        if (PermissionManager.isAccessLocationAllowed(getContext())) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setPadding(0, 0, 0, 0);
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
//            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
//            googleMap.getUiSettings().setRotateGesturesEnabled(true);
//            googleMap.setMinZoomPreference(Config.MAP_ZOOM_LEVEL);
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_LEVEL));
                }
            });
            Log.e("zoom to ", "" + Config.MAP_ZOOM_LEVEL);

            bulidGoogleApiClient();


            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    try {
                        if (marker.getTag().toString().equals("1")){
                            Log.e("showInfoWindow", "showInfoWindow");
                            marker.showInfoWindow();
                        } else {
                            Log.e("showInfoWindow1", "showInfoWindow1");
                            animateMarker(marker);
                            String title = MGUtilities.getStringFromResource(getActivity(), R.string.current_location);
                            String titleMarker = marker.getTitle();
                            if (customMarker != null && titleMarker.compareTo(title) == 0) {
                                Log.e("marker.getId()", marker.getId());
                                Log.e("customMarker.getId()", customMarker.getId());

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 1000ms
                                        marker.showInfoWindow();
                                    }
                                }, 10000);
                                marker.showInfoWindow();
                            } else {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 1000ms
                                        showInfoView(marker);
                                    }
                                }, 100);
                            }
                        }
                    } catch (Exception e){
                        e.getLocalizedMessage();
                    }

                    return true;
                }
            });

            googleMap.setOnInfoWindowClickListener(this);
            createCustomMarker();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 1000ms
                    getData();
                    //updateProfile();
                }
            }, 10000);
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionManager.CODE_CUSTOM_PERM);
        }
    }

    private void internetConnection() {
        try {
            //Check if there is an internet connection
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {

                if (alert == null) {
                    //we are  not connected to a network
                    alert = new AlertDialog.Builder(getContext());
                    alert.setMessage(getContext().getResources().getText(R.string.no_network_connection));
                    alert.setCancelable(false);
                    alert.setPositiveButton(getContext().getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    dismissAlert = null;
                                    alert = null;

                                }
                            });
                    alert.setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });

                    dismissAlert = alert.show();
                }
            } else {
                if (dismissAlert != null)
                    dismissAlert.dismiss();
            }
        } catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    private double lat1, lng1;
    private float v;
    private LatLng startPosition;
    private LatLng start;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Config.REQUEST_CODE_SIGHTING_DETAIL) {
            if (googleMap != null) {
                getData();
                // updateProfile();
            }
        }
        if (requestCode == Config.PERMISSION_REQUEST_LOCATION_SETTINGS) {
            if (MGUtilities.isLocationEnabled(getActivity())) {
                getData();
                //  updateProfile();
            } else {
                showRefresh(false);
                Toast.makeText(getActivity(), R.string.location_error_not_turned_on, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 14 && resultCode == Activity.RESULT_OK) {
            getData();
            // updateProfile();
            itemremoved = data.getBooleanExtra("removed", false);
            vibrateCounter = 0;
        }
    }
    //get all gifts in 0.7 raduis  arround my location
    private void getData() {
        try {
            task = new MGAsyncTaskNoDialog(getActivity());
            task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {
                DataResponse response;

                @Override
                public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {

                    if (currentLocation != null && MGUtilities.hasConnection(getActivity())) {
                        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                        if (userSession != null && userSession.getId() != null)
                            params.add(new BasicNameValuePair("user_id", userSession.getId()));
                        params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
                        params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
                        params.add(new BasicNameValuePair("radius", "0.7"));
                        response = DataParser.getJSONFromUrlWithPostRequest(Config.GIFT_ON_MAP, params, getContext());
                        if (response == null) {
                            showLogoutDialog();
                            return;
                        }
                        try {
                            Log.e("responseGift", String.valueOf(response.getGifts().size()));
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                        }

                        gifts.clear();
                        if (response.getGifts() != null && response.getGifts().size() > 0) {
                            int sizecount = 0;
                            for (Gifts obj : response.getGifts()) {
                                sizecount++;
                                q.deleteGift(Integer.valueOf(obj.getGift_map_id()));
                                q.insertGift(obj);
                                gifts.add(obj);
                                if (sizecount == 700)
                                    break;
                            }
                        }
                    } else {
                        gifts = q.getGifts();
                        if (currentLocation != null) {
                            for (Gifts sighting : gifts) {
                                Location locStore = new Location("Sighting");
                                Log.e(TAG, "Sighting  lon =" + sighting.getLon() + "lat = " + sighting.getLat());
                                locStore.setLatitude(Double.valueOf(sighting.getLat()));
                                locStore.setLongitude(Double.valueOf(sighting.getLon()));
                                double userDistanceFromStore = currentLocation.distanceTo(locStore) * Config.CONVERSION_OFFLINE_DATA_DISTANCE_TO_KM;
                                sighting.setDistance(String.valueOf(userDistanceFromStore));
                            }
                            Collections.sort(gifts, new Comparator<Gifts>() {
                                @Override
                                public int compare(Gifts o1, Gifts o2) {
                                    if (Double.valueOf(o1.getDistance()) < Double.valueOf(o2.getDistance()))
                                        return -1;
                                    if (Double.valueOf(o1.getDistance()) > Double.valueOf(o2.getDistance()))
                                        return 1;
                                    return 0;
                                }

                            });
                        }
                    }
                }

                @Override
                public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) {

                }

                @Override
                public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                    showRefresh(false);
                    addMarkers(gifts);
                    visibleMarker();
                    Log.e("api=>", "count" + gifts.size());
                    getGiftProgressbar.setVisibility(View.GONE);
                }

                @Override
                public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) {
                    getGiftProgressbar.setVisibility(View.VISIBLE);
                }

            });
            task.execute();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void showLogoutDialog() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                    final View promptView = layoutInflater.inflate(R.layout.logout_dialog, null);

                    builder1 = new AlertDialog.Builder(getContext());
                    builder1.setView(promptView);

                    alertDialog = builder1.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    Button btnclossapp = (Button) promptView.findViewById(R.id.btncloseapp);
                    btnclossapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logoutUser();
                        }
                    });
                    alertDialog.show();
                }
            });
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }


    private void logoutUser() {
        try {
            UserAccessSession accessSession = UserAccessSession.getInstance(getContext());
            if (accessSession != null)
                accessSession.clearUserSession();
            LoginManager.getInstance().logOut();
            Intent logingIntent = new Intent(getContext(), LoginActivity.class);
            logingIntent.putExtra("LauncherActivity", "MainFragment");
            startActivity(logingIntent);
            getActivity().finish();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }


    private void addMarkers(ArrayList<Gifts> arrayList) {
        try {
            if (googleMap != null && customMarkerOptions != null) {
                googleMap.clear();
                customMarker = googleMap.addMarker(customMarkerOptions);
                mapCircle = googleMap.addCircle(circleOptions);
                refreshCounter();
            }

            try {
                markers.clear();
                markerList.clear();
                for (Gifts entry : arrayList) {
                    if (Float.valueOf(entry.getLat()) == 0 || Float.valueOf(entry.getLon()) == 0)
                        continue;
                    Marker mark = createMarker(entry);
                    mark.setVisible(false);
                    markerList.add(mark);
                    markers.put(mark.getId(), entry);
                }
                visibleMarker();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // drawMarkerWithCircle(new LatLng(lat, lon));
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }


    public void clearNotification() {
        try {
            NotificationManager notificationManager = (NotificationManager) getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(512);
            notificationExist = false;
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void showNotification() {
        try {
            notificationExist = true;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Akram")
                            .setContentText(mFusedLocationClient.getApplicationContext().getResources().getString(R.string.notificationGift));
            mBuilder.setSound(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.definite));
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(512, mBuilder.build());
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void visibleMarker() {
        try {
            if (markerList != null) {
                Log.e("markerlist", markerList.toString());
                for (Marker item : markerList) {
                    Location storeLocation = new Location("item");
                    storeLocation.setLatitude(item.getPosition().latitude);
                    storeLocation.setLatitude(item.getPosition().longitude);
                    double userDistanceFromStore = meterDistanceBetweenPoints((float) lat, (float) lon, (float) item.getPosition().latitude, (float) (item.getPosition().longitude));
                    if (userDistanceFromStore < radius) {
                        if (itemremoved && latestItemTakenID.equals(item.getId())) {
                            item.setVisible(false);
                        } else {
                            item.setVisible(true);
                            if (isInBackground && !notificationExist) {
                                //this notificationitemid used to remove notification if (this item) become out of range
                                notificationItemID = item.getId();
                                showNotification();
                            } else if (notificationExist && !isInBackground) {
                                clearNotification();
                            }
                            try {
                                Vibrator v = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                                if (!BuildConfig.DEBUG) {
                                    if (vibrateCounter <= 10 && !seenBefore(item.getId())) {
                                        vibrateCounter++;
                                        v.vibrate(200);
                                        itemsfound.add(item.getId());

                                        if (!notificationExist) {
                                            //this notificationitemid used to remove notification if (this item) become out of range
                                            notificationItemID = item.getId();
                                            showNotification();
                                        } else if (notificationExist) {
                                            clearNotification();
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("Vibrateerror", "Error vibrate device");
                            }
                            Log.e("Vibrateerror", "Error vibrate device");
                        }

                    } else {
                        item.setVisible(false);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("markerer", e.toString());
        }
    }

    private boolean seenBefore(String itemid) {
        boolean found = false;
        try {
            for (String oneItem : itemsfound) {
                if (oneItem.equals(itemid)) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e){
            e.getLocalizedMessage();
        }
        return found;
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    private Marker createMarker(Gifts sighting) {
        final MarkerOptions markerOptions = new MarkerOptions();
        Spanned title = Html.fromHtml(sighting.getName());
        title = Html.fromHtml(title.toString());

        Spanned subtitle = Html.fromHtml(sighting.getName());
        subtitle = Html.fromHtml(subtitle.toString());

        markerOptions.title(title.toString());
        markerOptions.snippet(subtitle.toString());
        markerOptions.position(new LatLng(Double.valueOf(sighting.getLat()), Double.valueOf(sighting.getLon())));
        if (collectedGiftlist.contains(String.valueOf(sighting.getMerchant_id())))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(UIConfig.MAP_PIN_PLACEHOLDER_TAKEN));
        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(UIConfig.MAP_PIN_PLACEHOLDER));

        markerOptions.visible(false);
        markerOptions.draggable(false);
        Marker mark = googleMap.addMarker(markerOptions);
        mark.setInfoWindowAnchor(Config.MAP_INFO_WINDOW_X_OFFSET, 0);

        if (sighting != null) {
            MGHSquareImageView imgView = new MGHSquareImageView(getActivity());
            imgView.setMarker(mark);
            imgView.setMarkerOptions(markerOptions);
            imgView.setTag(sighting);
        }
        return mark;
    }


    public void showInfoView(Marker marker) {
        try {
            final Gifts obj = markers.get(marker.getId());
            latestItemTakenID = marker.getId();
            selectedSighting = obj;
            if (PokemonApplication.currentLocation != null) {
                Location loc = new Location("marker");
                loc.setLatitude(marker.getPosition().latitude);
                loc.setLongitude(marker.getPosition().longitude);

                Location loc1 = new Location("marker");
                loc1.setLatitude(lat);
                loc1.setLongitude(lon);

                double meters = loc1.distanceTo(loc);
                double km = meters * Config.METERS_TO_KM;
                String str = String.format("%.1f %s",
                        km,
                        MGUtilities.getStringFromResource(getActivity(), R.string.km));

                TextView tvDistance = (TextView) viewInflate.findViewById(R.id.tvDistance);
                tvDistance.setText(str);
            }


            String fullName = MGUtilities.getStringFromResource(getActivity(), R.string.empty);

            fullName = selectedSighting.getMerchant_name();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            final View promptView = layoutInflater.inflate(R.layout.new_info_slide, null);

            builder = new AlertDialog.Builder(getContext());
            builder.setView(promptView);
            alertDialog1 = builder.create();

            Button btnOpenDetails = (Button) promptView.findViewById(R.id.btnOpenDetails);
            TextView txtName = (TextView) promptView.findViewById(R.id.txtprizename);
            TextView txtAddedBy = (TextView) promptView.findViewById(R.id.txtprizeaddedby);

            Spanned spannedSubtitle = Html.fromHtml(selectedSighting.getMerchant_name());
            spannedSubtitle = Html.fromHtml(spannedSubtitle.toString());

            txtAddedBy.setText(spannedSubtitle);
            txtName.setText(selectedSighting.getName());

            btnOpenDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("sighting", obj);
                    alertDialog1.getWindow().getAttributes().windowAnimations = R.anim.slide_up;
                    alertDialog1.dismiss();
                    getActivity().startActivityForResult(i, 14);
                }
            });
            alertDialog1.show();
        } catch (Exception e) {
            final Snackbar snackbar = Snackbar.make(viewInflate, "error occurred", Snackbar.LENGTH_LONG);
            snackbar.setAction(mFusedLocationClient.getApplicationContext().getResources().getText(R.string.Dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMapClick(LatLng point) {
        // TODO Auto-generated method stub
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    private void createCustomMarker() {
        try {
            if (customMarker != null && currentLocation != null) {
                LatLng currentPos = new LatLng(lat, lon);
                customMarker.setPosition(currentPos);
                if (mapCircle != null) {
                    mapCircle.setCenter(currentPos);
                }
                Projection projection = googleMap.getProjection();
                LatLng markerPosition = new LatLng(lat, lon);
                Point markerPoint = projection.toScreenLocation(markerPosition);
                Point targetPoint = new Point(markerPoint.x, markerPoint.y - viewInflate.getHeight() / 4);
                LatLng targetPosition = projection.fromScreenLocation(targetPoint);

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition));
                return;
            }
            if (lat == 0 && lon == 0 && currentLocation != null) {
                lat = currentLocation.getLatitude();
                lon = currentLocation.getLongitude();
            }
            if (userSession == null || userSession.getImage().equals("")) {
                imageUrl = "http://www.joserv.org/streethunter/uploads/bg_image_thumb_placeholder.png";
            } else {
                imageUrl = userSession.getImage();
            }

            Activity activity = getActivity();
            if (activity != null) {
                imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        try {
                            d = new BitmapDrawable(getResources(), loadedImage);
                            customContinue(true);
                        } catch (Exception e) {
                            customContinue(false);
                        }
                        return;
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        customContinue(false);
                        super.onLoadingFailed(imageUri, view, failReason);
                    }
                });
            } else {
                customContinue(false);
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
        return;
    }

    private void customContinue(Boolean loaded) {
        customMarkerOptions = new MarkerOptions();
        customMarkerOptions.title(MGUtilities.getStringFromResource(getActivity(), R.string.current_location));
        customMarkerOptions.snippet(MGUtilities.getStringFromResource(getActivity(), R.string.drag_to_move_around));
        customMarkerOptions.position(new LatLng(lat, lon));

        if (loaded) {
            try {
                customMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(d)));
            } catch (Exception ex) {
                customMarkerOptions.icon(BitmapDescriptorFactory.fromResource(UIConfig.MAP_PIN_CUSTOM_LOCATION));
            }
        } else {
            customMarkerOptions.icon(BitmapDescriptorFactory.fromResource(UIConfig.MAP_PIN_CUSTOM_LOCATION));
        }
        circleOptions = new CircleOptions()
                .center(new LatLng(lat, lon))   //set center
                .radius(radius)   //set radius in meters
                .strokeColor(Color.TRANSPARENT)
                .fillColor(0x55525564)
                .strokeWidth(5);
        mapCircle = googleMap.addCircle(circleOptions);
        customMarker = googleMap.addMarker(customMarkerOptions);
        customMarker.setDraggable(false);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_LEVEL);
        googleMap.moveCamera(zoom);


        Projection projection = googleMap.getProjection();
        LatLng markerPosition = new LatLng(lat, lon);
        Point markerPoint = projection.toScreenLocation(markerPosition);
        Point targetPoint = new Point(markerPoint.x, markerPoint.y - viewInflate.getHeight() / 4);
        LatLng targetPosition = projection.fromScreenLocation(targetPoint);
        CameraUpdate center = CameraUpdateFactory.newLatLng(targetPosition);

        googleMap.animateCamera(center);
        return;
    }

    private void drawMarkerWithCircle(LatLng position, String name, String description) {
        double radiusInMeters = 10.0;
        int shadeColor = 0x4Dff0000; //opaque red fill

//        CircleOptions circleOptions1 = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(shadeColor).strokeWidth(1);
//        circleOptions1.isClickable();
//        mCircle = googleMap.addCircle(circleOptions1);

        CircleOptions circleOptions1 = new CircleOptions()
                .center(position)   //set center
                .radius(radiusInMeters)   //set radius in meters
                .strokeColor(Color.TRANSPARENT)
                .fillColor(0x4Dff0000)
                .strokeWidth(1);
        Circle newMapCircle = googleMap.addCircle(circleOptions1);
        mCircle = newMapCircle;

        MarkerOptions customMarkerOptions1 = new MarkerOptions();
        customMarkerOptions1.title(MGUtilities.getStringFromResource(getActivity(), R.string.current_location));
        customMarkerOptions1.draggable(false);
        customMarkerOptions1.alpha(0);
        customMarkerOptions1.snippet(MGUtilities.getStringFromResource(getActivity(), R.string.drag_to_move_around));
        customMarkerOptions1.position(position);


        customMarkerOptions1.icon(null);


        Marker customMarker1 = googleMap.addMarker(customMarkerOptions1);
        customMarker1.setTag("1");
        customMarker1.setTitle(name);
        customMarker1.setSnippet(description);
        customMarker1.setDraggable(false);

        LatLng currentPos = position;
        customMarker1.setPosition(currentPos);
        if (mCircle != null) {
            mCircle.setCenter(currentPos);
        }

    }

    private Bitmap getMarkerBitmapFromView(Drawable resId) {

        View customMarkerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.circle_imahe_on_map, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.user_marker_icon);
        markerImageView.setImageDrawable(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public void updateProfile() {

        String s_userSession = "";
        if (userSession != null && userSession.getId() != null)
            s_userSession = userSession.getId();

        String s_ApiKey = "";
        if (userSession != null)
            if (userSession.getId() != null)
                s_ApiKey = userSession.getApikey();

        Map<String, String> params = new HashMap<String, String>();
        params.put("x-api-key", s_ApiKey);
        AndroidNetworking.upload(Config.GIFT_ON_MAP)
                .addMultipartParameter("user_id", s_userSession)
                .addMultipartParameter("lat", String.valueOf(lat))
                .addMultipartParameter("lon", String.valueOf(lon))
                .addMultipartParameter("radius", "0.7")
                .addHeaders(params)
                .setContentType("multipart/form-data; charset=utf-8")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Snackbar snackbar = Snackbar.make(imgViewThumb, "Error occurred", Snackbar.LENGTH_LONG);
                        //snackbar.show();
                    }
                });
    }


    void getEvent(){
        try {
            userSession = UserAccessSession.getInstance(getContext()).getUserSession();
            String s_ApiKey = "";
            if (userSession != null)
                if (userSession.getId() != null)
                    s_ApiKey = userSession.getApikey();

            Map<String, String> params = new HashMap<String, String>();
            params.put("x-api-key", s_ApiKey);

            String user_id = "";
            if (userSession != null && userSession.getId() != null)
                user_id = userSession.getId();

            Log.e("Scheduled_Event", Config.GET_Scheduled_Event +","+ user_id +","+ String.valueOf(lat)  +","+ String.valueOf(lon)+","+ s_ApiKey);
            AndroidNetworking.post(Config.GET_Scheduled_Event)
                    .addBodyParameter("user_id", user_id)
                    .addBodyParameter("radius", "100.0")
                    .addBodyParameter("lat", String.valueOf(lat))
                    .addBodyParameter("lon", String.valueOf(lon))
                    .addHeaders(params)
                    .setContentType("multipart/form-data; charset=utf-8")
                    .setTag("Test")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            advData = new SparseArray<>();
                            try {
                                Log.e("yes", response.toString());
                                if(response.getJSONArray("event").length() == 0){
                                    Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                                }else {
                                    Log.e("yes", "yes2");
                                    JSONArray jsonArray = response.getJSONArray("event");
                                    int i;
                                    Log.e("yes", "yes3");
                                    for (i = 0; i < jsonArray.length();i++){
                                        Log.e("loc_lat", String.valueOf(Double.valueOf(jsonArray.getJSONObject(i).getString("loc_lat"))));
                                        Log.e("loc_lon", String.valueOf(Double.valueOf(jsonArray.getJSONObject(i).getString("loc_lon"))));
                                        // googleMap.addMarker(new MarkerOptions().position(new LatLng(31.996233600000004, 35.885124499999996)).title("zzzzz"));
                                         //drawMarkerWithCircle(new LatLng(31.9961876, 35.8851656) , "test", "test");

                                         String loc_lat =  jsonArray.getJSONObject(i).getString("loc_lat");
                                         String loc_lon =  jsonArray.getJSONObject(i).getString("loc_lon");
                                         String name =  jsonArray.getJSONObject(i).getString("name");
                                        String description =  jsonArray.getJSONObject(i).getString("description");

                                        //drawMarkerWithCircle(new LatLng(31.981908416106, 35.848515238437), "test", "test");
                                        Log.e("loc_lat", String.valueOf(new LatLng(Double.valueOf(loc_lat), Double.valueOf(loc_lon))));

                                        try {
                                            drawMarkerWithCircle((new LatLng(Double.valueOf(loc_lat), Double.valueOf(loc_lon))), name, description);
                                        } catch (Exception e){
                                            e.getLocalizedMessage();
                                            Log.e("get_error", e.getLocalizedMessage());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ttest", e.getLocalizedMessage());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {}
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
