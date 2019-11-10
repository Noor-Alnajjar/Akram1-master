package com.joserv.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adapter.NotificationAdapter;
import com.google.gson.Gson;
import com.joserv.Akram.R;
import com.libraries.dbtiny.TinyDB;
import com.models.PushNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationHistory extends AppCompatActivity {


    private ListView lv;
    private ArrayList<PushNotification> notificationArrayList = new ArrayList<PushNotification>();

    private static NotificationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.notificationList);
        ImageView imgNoData = (ImageView) findViewById(R.id.imgNoData);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.listHistory);

        TinyDB tinydb = new TinyDB(this);
        ArrayList<String> notificationHis = new ArrayList<String>();
        notificationHis = tinydb.getListString("notificationHistory");

        if(notificationHis.size()==0){
            imgNoData.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }else {
            imgNoData.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }


        for (String str:notificationHis){
            Gson gson = new Gson();
            PushNotification obj = gson.fromJson(str, PushNotification.class);
            notificationArrayList.add(obj);

        }

        adapter= new NotificationAdapter(notificationArrayList,getApplicationContext());

        lv.setAdapter(adapter);

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
}
