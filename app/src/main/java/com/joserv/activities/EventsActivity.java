package com.joserv.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils;
import com.adapter.EventsAdapter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.config.Config;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joserv.Akram.R;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.models.Collection;
import com.models.Event;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private String user_id;
    private RecyclerView event_list;
    private TextView textView;
    private ImageView imgView;
    private UserSession userSession;
    private SparseArray<Event> advData;
    private EventsAdapter EventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        Utils.showLoading(EventsActivity.this,true);

        textView = (TextView)findViewById(R.id.mTextField) ;
        imgView = (ImageView) findViewById(R.id.imgNoData);
        event_list = (RecyclerView) findViewById(R.id.event_list);
        event_list.setLayoutManager(new GridLayoutManager(EventsActivity.this,1));

        Intent intent =getIntent();
        user_id=intent.getStringExtra("User_id");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        getEvents();
    }

    void getEvents(){
        try {
            userSession = UserAccessSession.getInstance(EventsActivity.this).getUserSession();
            String s_ApiKey = "";
            if (userSession != null)
                if (userSession.getId() != null)
                    s_ApiKey = userSession.getApikey();

            Map<String, String> params = new HashMap<String, String>();
            params.put("x-api-key", s_ApiKey);

            Log.e("GET_Scheduled_Event", Config.GET_All_Event +user_id + "," + s_ApiKey );
            AndroidNetworking.post(Config.GET_All_Event)
                    .addBodyParameter("user_id", user_id)
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
                                if(response.getJSONArray("event").length() == 0){
                                    Toast.makeText(EventsActivity.this,"  ",Toast.LENGTH_LONG).show();
                                }else {
                                        JSONArray jsonArray = response.getJSONArray("event");
                                        int i;
                                        for (i = 0; i < jsonArray.length();i++){
                                            advData.put(i,
                                                    new Event(
                                                            jsonArray.getJSONObject(i).getString("id"),
                                                            jsonArray.getJSONObject(i).getString("contract_id"),
                                                            jsonArray.getJSONObject(i).getString("name"),
                                                            jsonArray.getJSONObject(i).getString("description"),
                                                            jsonArray.getJSONObject(i).getString("country"),
                                                            jsonArray.getJSONObject(i).getString("created_date"),
                                                            jsonArray.getJSONObject(i).getString("starting_date"),
                                                            jsonArray.getJSONObject(i).getString("duration"),
                                                            jsonArray.getJSONObject(i).getString("loc_lat"),
                                                            jsonArray.getJSONObject(i).getString("loc_lon"),
                                                            jsonArray.getJSONObject(i).getString("status"),
                                                            jsonArray.getJSONObject(i).getString("publish")));
                                        }
                                        if (advData.size() == 0){
                                            textView.setVisibility(View.VISIBLE);
                                            imgView.setVisibility(View.VISIBLE);
                                            event_list.setVisibility(View.GONE);
                                        } else {
                                            textView.setVisibility(View.GONE);
                                            imgView.setVisibility(View.GONE);
                                            event_list.setVisibility(View.VISIBLE);
                                            setData();
                                        }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                textView.setVisibility(View.VISIBLE);
                                imgView.setVisibility(View.VISIBLE);
                                event_list.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onError(ANError anError) {}
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void setData(){

        try {
            EventsAdapter = new EventsAdapter(EventsActivity.this,advData);
            event_list.setAdapter(EventsAdapter);
        } catch (Exception e){
            e.getLocalizedMessage();
            Log.e("error_test", e.getLocalizedMessage());
        }

    }
}
