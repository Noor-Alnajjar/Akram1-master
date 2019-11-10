package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joserv.Akram.R;

public class SendNotificationAfterDays extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private NotificationHelper notificationHelper;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String idUser = intent.getExtras().getString("ID_User");
        String idItem = intent.getExtras().getString("ID_Item");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Akram").child(idUser).child("Collection").child(idItem);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(!dataSnapshot.child("scan").getValue().toString().equals("3")){
                        sendNotification("AkramAPP Item",
                                dataSnapshot.child("Name").getValue().toString() + getString(R.string.will_expire));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void sendNotification(String title,String desc) {
        notificationHelper=new NotificationHelper(this);
        notificationHelper. createNotification(title,desc);
    }
}
