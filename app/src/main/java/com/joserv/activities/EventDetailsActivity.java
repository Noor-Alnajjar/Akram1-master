package com.joserv.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.config.Config;
import com.config.UIConfig;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joserv.Akram.R;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.Event;
import com.models.EventGifts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView eventName, eventDescription, eventCountry,eventCreatedDate, eventStartingDate, eventScheduled, eventGifts;
    private UserSession userSession;
    private String user_id, event_id;
    private double lat, lon;
    double radius = Config.SLIDER_RADIUS_DEFAULT;
    MarkerOptions customMarkerOptions;
    private CircleOptions circleOptions;
    Circle mapCircle = null;
    Marker customMarker;
    private GoogleMap googleMap;
    private Circle mCircle;
    private SparseArray<EventGifts> eventGiftData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent =getIntent();
        event_id= intent.getStringExtra("event_id");

        eventName = (TextView)findViewById(R.id.eventName);
        eventDescription = (TextView)findViewById(R.id.eventDescription);
        eventCountry = (TextView)findViewById(R.id.eventCountry);
        eventCreatedDate = (TextView)findViewById(R.id.eventCreatedDate);
        eventStartingDate = (TextView)findViewById(R.id.eventStartingDate);
        eventScheduled = (TextView)findViewById(R.id.eventScheduled);
        eventGifts = (TextView)findViewById(R.id.eventGifts);
        getEvent();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    void getEvent(){
        try {
            userSession = UserAccessSession.getInstance(EventDetailsActivity.this).getUserSession();
            String s_ApiKey = "";
            if (userSession != null)
                if (userSession.getId() != null)
                    s_ApiKey = userSession.getApikey();

            Map<String, String> params = new HashMap<String, String>();
            params.put("x-api-key", s_ApiKey);

            Log.e("ttest", Config.GET_Event +  event_id + " , "+userSession.getId()+ params);

            AndroidNetworking.post(Config.GET_Event)
                    .addBodyParameter("event_id", event_id)
                    .addBodyParameter("user_id", userSession.getId())
                    .addHeaders(params)
                    .setContentType("multipart/form-data; charset=utf-8")
                    .setTag("Test")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                if(response.getJSONObject("event").length() == 0){
                                    Toast.makeText(EventDetailsActivity.this,"  ",Toast.LENGTH_LONG).show();
                                }else {
                                    JSONObject JSONObject = response.getJSONObject("event");
                                    eventName.setText(JSONObject.getString("name"));
                                    eventDescription.setText(JSONObject.getString("description"));
                                    eventCountry.setText(EventDetailsActivity.this.getResources().getString(R.string.country) + JSONObject.getString("country"));
                                    eventCreatedDate.setText(EventDetailsActivity.this.getResources().getString(R.string.created_date) + JSONObject.getString("created_date"));
                                    eventStartingDate.setText(EventDetailsActivity.this.getResources().getString(R.string.starting_date) + JSONObject.getString("starting_date"));
                                    eventScheduled.setText(EventDetailsActivity.this.getResources().getString(R.string.status) + JSONObject.getString("status"));
                                    lat = Double.parseDouble(JSONObject.getString("loc_lat"));
                                    lon = Double.parseDouble(JSONObject.getString("loc_lon"));
                                    Log.e("lat,lon" , lat + "," + lon);
//                                    LatLng sydney = new LatLng(lat, lon);
//                                    mMap.addMarker(new MarkerOptions().position(sydney).title(JSONObject.getString("name")));
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                   // customContinue();
                                    drawMarkerWithCircle(new LatLng(lat, lon));
                                    //JSONObject.getString("id"),
                                    //JSONObject.getString("contract_id"),
                                    //JSONObject.getString("duration"),
                                    //JSONObject.getString("publish")
                                    try {
                                        eventGiftData = new SparseArray<>();
                                        JSONArray gift = response.getJSONArray("gift");
                                        Log.e("sssssssssss", String.valueOf(gift.length()));
                                        if (gift.length() != 0) {
                                            int i;
                                            for (i = 0; i < gift.length(); i++) {
                                                eventGiftData.put(i,
                                                        new EventGifts(
                                                                gift.getJSONObject(i).getString("id"),
                                                                gift.getJSONObject(i).getString("gift_id"),
                                                                gift.getJSONObject(i).getString("date_found"),
                                                                gift.getJSONObject(i).getString("lat"),
                                                                gift.getJSONObject(i).getString("lon"),
                                                                gift.getJSONObject(i).getString("visible"),
                                                                gift.getJSONObject(i).getString("creat_at"),
                                                                gift.getJSONObject(i).getString("update_at"),
                                                                gift.getJSONObject(i).getString("is_deleted"),
                                                                gift.getJSONObject(i).getString("country")));
                                            }
                                            eventGifts.setText(eventGiftData.size() + " " + EventDetailsActivity.this.getResources().getString(R.string.gift) + ".");
                                        } else {
                                            eventGifts.setVisibility(View.GONE);
                                        }
                                    } catch (Exception e){
                                        e.getLocalizedMessage();
                                        Log.e("ttest", e.getLocalizedMessage());
                                        eventGifts.setVisibility(View.GONE);
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

    private void drawMarkerWithCircle(LatLng position) {
        double radiusInMeters = 100.0;
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions1 = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(shadeColor).strokeWidth(1);
        mCircle = mMap.addCircle(circleOptions1);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(zoom);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    private void customContinue() {
        customMarkerOptions = new MarkerOptions();
//        customMarkerOptions.title(MGUtilities.getStringFromResource(EventDetailsActivity.this, R.string.current_location));
        customMarkerOptions.snippet(MGUtilities.getStringFromResource(EventDetailsActivity.this, R.string.drag_to_move_around));
        customMarkerOptions.position(new LatLng(lat, lon));



//        customMarkerOptions.icon(BitmapDescriptorFactory.fromResource(UIConfig.MAP_PIN_CUSTOM_LOCATION));

        circleOptions = new CircleOptions()
                .center(new LatLng(lat, lon))   //set center
                .radius(radius)   //set radius in meters
                .strokeColor(Color.TRANSPARENT)
                .fillColor(0x55525564)
                .strokeWidth(5);
        mapCircle = mMap.addCircle(circleOptions);
        customMarker = mMap.addMarker(customMarkerOptions);
        customMarker.setDraggable(false);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        mMap.moveCamera(zoom);


//        Projection projection = mMap.getProjection();
//        LatLng markerPosition = new LatLng(lat, lon);
//        Point markerPoint = projection.toScreenLocation(markerPosition);
//        Point targetPoint = new Point(markerPoint.x, markerPoint.y - viewInflate.getHeight() / 4);
//        LatLng targetPosition = projection.fromScreenLocation(targetPoint);
//        CameraUpdate center = CameraUpdateFactory.newLatLng(targetPosition);

       // googleMap.animateCamera(center);
        return;
    }

}
