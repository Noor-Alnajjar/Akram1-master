package com.joserv.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils;
import com.config.Config;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joserv.Akram.R;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

public class QrcodeReader extends AppCompatActivity {


    private View mView;
    private Button btnopebQr,btnredeem,btncancel;
    private TextView name,type,txtredeem,txttradeInfo;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseItems;
    private DatabaseReference databaseReferencetaken,userCollectionDatabase;
    private String user_id;
    private String itemID ="";
    private String rule = "";
    private String takenItem = "";
    private String expiry = "";
    private UserSession userSession;
    private RelativeLayout scannedlayout;
    private RelativeLayout notscannedlayout;
    private ImageView imageViewTrade;

    private int counterItem = 0;
    private String authItems;



    private InterstitialAd mInterstitialAd;
    private boolean isShowingIntersitital = false;
    //private Timer timerInterstitial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            UpdateTexts(data.getStringExtra("scannedInfo"));
            Log.e("info",data.getStringExtra("scannedInfo"));

        }else if (requestCode == 0) { // for refresh favourite list
            super.onActivityResult(requestCode, resultCode, data);
        }


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

    private void init(){

        //todo for ads
        //showIntersitial();
        userSession = UserAccessSession.getInstance(this).getUserSession();

        btnopebQr=(Button)findViewById(R.id.btnQrScanner);
        btnredeem=(Button)findViewById(R.id.btnRedeem);
        btncancel=(Button)findViewById(R.id.btncancel);
        //id=(TextView)findViewById(R.id.id);
        type=(TextView)findViewById(R.id.type);
        name=(TextView)findViewById(R.id.name);
        txtredeem=(TextView)findViewById(R.id.txtRedemedItem);
        scannedlayout=(RelativeLayout) findViewById(R.id.layoutscanned);
        notscannedlayout = (RelativeLayout) findViewById(R.id.layoutnotscanned);
        imageViewTrade = (ImageView)findViewById(R.id.imageViewtrade);
        txttradeInfo = (TextView)findViewById(R.id.txttradeInfo);

        setTitle(R.string.trade);
        reset();


        btnopebQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQr=new Intent(QrcodeReader.this,QrcodeRaederCamera.class);
                startActivityForResult(intentQr,2);
            }
        });

        userCollectionDatabase= FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(userSession.getId()));


    }



    private void UpdateTexts(final String info){

        Utils.showLoading(QrcodeReader.this,true);

        scannedlayout.setVisibility(View.VISIBLE);
        notscannedlayout.setVisibility(View.GONE);

        Vibrator v = (Vibrator) getSystemService(QrcodeReader.this.VIBRATOR_SERVICE);

        v.vibrate(200);


        final String infoArray[]=info.split(",");
        Log.e("Info",info + "length : "+infoArray.length);

        if(infoArray.length < 6 ){
            Toast.makeText(QrcodeReader.this,getResources().getString(R.string.old_qr_version),Toast.LENGTH_LONG).show();
            reset();
        }
        try{



//            id.setText(id.getText()+infoArray[0]);
            type.setText(type.getText()+infoArray[2]);
            name.setText(name.getText()+infoArray[1]);
            //loc.setText(loc.getText()+infoArray[4]);
            //dis.setText(dis.getText()+infoArray[3]);
            user_id=infoArray[5];

            //mAuth = FirebaseAuth.getInstance();
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Akram").child(user_id)
                    .child("Collection").child(String.valueOf(infoArray[0]));
            databaseItems=FirebaseDatabase.getInstance().getReference().child("Akram").child(infoArray[0]);
            databaseReferencetaken=FirebaseDatabase.getInstance().getReference().child("Akram").child("Items").child(infoArray[0]);

            //databaseAuth = FirebaseDatabase.getInstance().getReference().child("Akram").child("MarketToItems").child(mAuth.getCurrentUser().getUid());





            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    Utils.hideLoading();
                    if(dataSnapshot.exists()){
                        databaseReference.child("scan").setValue("1");
                        itemID= dataSnapshot.child("item_id").getValue().toString();
                        checkHas(infoArray[1]);
                        if(dataSnapshot.child("expiry").exists()){
                            expiry=dataSnapshot.child("expiry").getValue().toString();
                        }else{
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_YEAR, 7);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                            expiry = format.format(cal.getTime());
                        }

                        if(dataSnapshot.child("rules").exists())
                            rule = dataSnapshot.child("rules").getValue().toString();

                        btnredeem.setEnabled(true);
                        btnredeem.setTextColor(getResources().getColor(R.color.colorWhite));

                        btnredeem.setVisibility(View.VISIBLE);
                        btncancel.setVisibility(View.VISIBLE);
                        btnopebQr.setVisibility(View.GONE);

                        Utils.showLoading(QrcodeReader.this,true);

                    } else{

                        //show error item not found
                        final Snackbar snackbar = Snackbar
                                .make(btnredeem, getResources().getString(R.string.item_not_available), Snackbar.LENGTH_INDEFINITE);

                        snackbar.setAction( getResources().getText(R.string.Dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });


                        View view = snackbar.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snackbar.show();
                        reset();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            databaseReference.child("scan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){

                        reset();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





            btnredeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    databaseReference.setValue(null);
                    databaseReferencetaken.setValue(null);
                    databaseItems.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("Sighting_id").setValue(infoArray[0]);
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("Name").setValue(infoArray[1]);
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("Type").setValue(infoArray[2]);
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("Loc").setValue(String.valueOf(infoArray[4]));
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("Distance").setValue(String.valueOf(infoArray[3]));
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("scan").setValue("0");
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("item_id").setValue(String.valueOf(itemID));
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("expiry").setValue(expiry);
                                if(!rule.equals(""))
                                userCollectionDatabase.child("Collection").child(infoArray[0]).child("rules").setValue(rule);



                            }

                        }
                    });

                    imageViewTrade.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_mark_dark_512_2));
                    txttradeInfo.setText(getResources().getString(R.string.trade_again));
                    reset();

                    final Snackbar snackbar = Snackbar
                            .make(btnredeem, getResources().getString(R.string.item_added_in_collection), Snackbar.LENGTH_LONG);
                    snackbar.setAction( getResources().getText(R.string.Dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });

                    snackbar.show();
                }
            });

            btncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("scan").setValue("0");
                    reset();
                }
            });

        }catch (Exception ex){

            reset();
            final Snackbar snackbar = Snackbar
                    .make(btnredeem, getResources().getString(R.string.item_qr_not_supported), Snackbar.LENGTH_LONG);

            View view = snackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.YELLOW);
            snackbar.setAction( getResources().getText(R.string.Dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();

        }



    }
    private void checkHas(final String item){


        userCollectionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                        Log.e("itemID",itemID);
                        if(checkItem(dataSnapshot,itemID)){

                            final Snackbar snackbar = Snackbar
                                    .make(btnredeem, getResources().getString(R.string.there_is)+" "+item+" "+getResources().getString(R.string.in_your_collection), Snackbar.LENGTH_INDEFINITE);

                            View view = snackbar.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.RED);

                            snackbar.setAction( getResources().getText(R.string.Dismiss), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });

                            snackbar.show();
                            reset();
                        }else {

                        }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private boolean checkItem(DataSnapshot userspace,String itemID) {

        for(DataSnapshot snapshotCollection :userspace.child("Collection").getChildren()){
            if(snapshotCollection.child("item_id").exists()){
                Log.e("gettting", "gettting");
                Log.e("snapcollection", snapshotCollection.child("item_id").getValue().toString());
                Log.e("findthis", itemID);
                if(snapshotCollection.child("item_id").getValue().toString().equals(itemID)){
                    return true;
                }
            }else {
                return false;
            }

        }
        return false;

    }

    private void reset(){
        btnopebQr.setVisibility(View.VISIBLE);
        btnredeem.setVisibility(View.GONE);
        btncancel.setVisibility(View.GONE);
        Utils.hideLoading();
        type.setText("Item Name: ");
        name.setText("Type: ");
        txtredeem.setText("Redeemed: ");
        scannedlayout.setVisibility(View.GONE);
        notscannedlayout.setVisibility(View.VISIBLE);
//todo ads
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//            isShowingIntersitital = true;
//            Log.e("showingadd","loaded and viwed");
//        }
    }





    public void showIntersitial() {


        if(mInterstitialAd == null)
            mInterstitialAd = new InterstitialAd(this);

        if(Config.SHOW_INTERSTITIAL) {
            // set the ad unit ID
            mInterstitialAd.setAdUnitId(Config.INTERSTITIAL_AD_UNIT_ID);
            Log.e("showingadd","aass");
            loadRequest();
            Log.e("showingadd","aass1");



            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    isShowingIntersitital = false;


                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    isShowingIntersitital = false;
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    isShowingIntersitital = false;
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    isShowingIntersitital = true;
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    isShowingIntersitital = false;
                    //Toast.makeText(QrcodeReader.this,"add ready",Toast.LENGTH_LONG).show();
                }
            });


        }

    }


    private void loadRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if(Config.TEST_ADS_USING_TESTING_DEVICE)
            builder.addTestDevice(Config.TESTING_DEVICE_HASH);

        AdRequest adRequest = builder.build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);


    }

}


