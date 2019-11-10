package com.joserv.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.Utils;
import com.application.PokemonApplication;
import com.config.Config;
import com.db.Queries;
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
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.dataparser.DataParser;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Gifts;
import com.models.Merchant;
import com.models.Status;
import com.models.Rules;
import com.joserv.Akram.R;
import com.services.SendNotificationAfterDays;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    Gifts gifts;
    Queries q;
    MGAsyncTask task;
    DatabaseReference databaseCollection;
    DatabaseReference databaseItems,dataBaseItemDuplecate;
    UserSession userSession = null;
    boolean empty = false;


    InterstitialAd mInterstitialAd;
    boolean isShowingIntersitital = false;
    Timer timerInterstitial;

    //2.2 Akram
    private RatingBar rateBar;

    //popup
    private DataParser data;
    private ImageView imgFacebook,imgInstagram,imgdailer,imgEmail,imgProfile;
    private TextView name;
    private Button btndismis;

    //Akram 3.0
    private Merchant responseMerchant;
    private android.support.v7.app.AlertDialog alertDialog;
    private ValueEventListener valueEventListenerDuplecate;
    private ValueEventListener valueEventListenerDatabaseItems;





    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.view_activity_detail);


        try{
            userSession = UserAccessSession.getInstance(this).getUserSession();
        }catch (Exception e){
            Toast.makeText(DetailActivity.this,"Please update your information, if this problem continue please create another user",Toast.LENGTH_LONG).show();
        }


        if(Config.WILL_SHOW_ADS)
        showIntersitial();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.showLoading(DetailActivity.this,false);
        //init
        q = PokemonApplication.getQueriesInstance(this);
        gifts = (Gifts) this.getIntent().getSerializableExtra("sighting");
        Log.e("Gift_id",gifts.getGift_map_id());
        Log.e("contract",gifts.getContract_id());
        //todo re add this on rules
        //Log.e("Rules1",String.valueOf(gifts.getRules()));
        //final User user = q.getUserByUserId(Integer.valueOf(gifts.getMarchent_id()));
        //user.getUser_id();

        getUser(String.valueOf(gifts.getMerchant_id()));

        LinearLayout linearLike = (LinearLayout) findViewById(R.id.linearLike);
        linearLike.setOnClickListener(this);

        LinearLayout linearDislike = (LinearLayout) findViewById(R.id.linearDislike);
        linearDislike.setOnClickListener(this);

        LinearLayout layoutOpenMerchant = (LinearLayout) findViewById(R.id.mershant_layout);
        layoutOpenMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // showUserInfo();
            }
        });

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(gifts.getName());


        final Button btncollect =(Button)findViewById(R.id.btnCollect);
        Button btnRules = (Button) findViewById(R.id.btnGiftRules);
        rateBar = (RatingBar) findViewById(R.id.ratingBarGift);

        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateGift(v);
            }
        });
        //todo re add this on rules
        if(gifts.getRules().size()==0)
            btnRules.setVisibility(View.GONE);

        btnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGiftRules();
            }
        });

        btncollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collect();
            }
        });

        final TextView plslogin = (TextView) findViewById(R.id.txtplslog);

        //showIntersitial();
        //Log.e("PokemonId123",String.valueOf(sighting.getSighting_id()));
        if(userSession != null) {
            //to add to collection
            databaseCollection= FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(userSession.getId()))
                    .child("Collection").child(String.valueOf(gifts.getGift_map_id()));




            //to check if this item has been taken before and still has it
            dataBaseItemDuplecate=FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(userSession.getId()));
            //databasefakeid = FirebaseDatabase.getInstance().getReference().child("Akram").child("fakeId").child(String.valueOf(user.getUser_id()));
            databaseItems= FirebaseDatabase.getInstance().getReference().child("Akram").child("Items").child(String.valueOf(gifts.getGift_map_id()));


                     valueEventListenerDuplecate = dataBaseItemDuplecate.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            empty = false;
                            if(dataSnapshot1.exists()){

                                String itemID = String.valueOf(gifts.getMerchant_id());
                                if(checkItem(dataSnapshot1,itemID)){
                                    btncollect.setEnabled(false);
                                    btncollect.setVisibility(View.GONE);
                                    snakBarMsg(getResources().getString(R.string.there_is)+" "+gifts.getMerchant_name()
                                            +" "+getResources().getString(R.string.in_your_collection),3);
                                }else{
                                    empty =true;
                                }
                            }else
                                empty=true;

                            valueEventListenerDatabaseItems = databaseItems.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Log.e("PokemonIdtheforth",String.valueOf(empty));
                                    if(dataSnapshot.exists()){
                                        btncollect.setEnabled(false);
                                        btncollect.setVisibility(View.GONE);
                                        plslogin.setVisibility(View.GONE);
                                        Utils.hideLoading();
                                        snakBarMsg(getResources().getString(R.string.item_taken),3);


                                    }else if(empty && !dataSnapshot.exists()){
                                        btncollect.setVisibility(View.VISIBLE);
                                        btncollect.setEnabled(true);
                                        plslogin.setVisibility(View.GONE);
                                        Utils.hideLoading();

                                    }
                                    else{
                                        Utils.hideLoading();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    btncollect.setVisibility(View.GONE);
                                    Toast.makeText(DetailActivity.this,"Error occurred please try again",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            btncollect.setVisibility(View.GONE);
                            Toast.makeText(DetailActivity.this,"Error occurred please try again",Toast.LENGTH_LONG).show();

                        }
                    });
        }
        else {

            btncollect.setEnabled(false);
            btncollect.setVisibility(View.GONE);
            plslogin.setVisibility(View.VISIBLE);
            Utils.hideLoading();
        }
    }

    private void getUser(final String item_id) {
        task = new MGAsyncTask(DetailActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {
            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(DetailActivity.this, R.string.loading) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub

                if(responseMerchant.getName()!=null)
                    updateTextsDialog();
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", userSession.getId()));

                //todo fix this to handle both merchant marchent
                params.add(new BasicNameValuePair("id",gifts.getMerchant_id() ));
                responseMerchant = DataParser.getJSONFromUrlWithPostRequestUser(Config.GET_MERCHANT, params,getApplicationContext());
                if (responseMerchant == null)
                    return;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        updateTextUser();
                    }
                });

            }
        });
        task.execute();

    }

    private void updateTextUser() {

        imgFacebook = (ImageView) findViewById(R.id.imgFaceBook);
        imgInstagram = (ImageView) findViewById(R.id.imgInstagram);
        imgEmail = (ImageView) findViewById(R.id.imgEmail);
        imgdailer = (ImageView) findViewById(R.id.imgDail) ;
        imgProfile = (ImageView) findViewById(R.id.imgViewThumbUser) ;



        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFacebookIntent();
            }
        });

        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenInsta();
            }
        });

        imgdailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDailer();
            }
        });


    }

    private void rateGift(final float v) {
        task = new MGAsyncTask(DetailActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(DetailActivity.this, R.string.rating) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                snakBarMsg(getResources().getString(R.string.gift_rated),3);
                rateBar.setEnabled(false);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("rate", String.valueOf(v)));
//                params.add(new BasicNameValuePair("user_id", String.valueOf(gifts.getMarchent_id())));
                params.add(new BasicNameValuePair("user_id", userSession.getId()));
                params.add(new BasicNameValuePair("gift_id", gifts.getId()));
                response = DataParser.getJSONFromUrlWithPostRequest(Config.RATE_GIFT, params,getApplicationContext());
            }
        });
        task.execute();
    }

    private void snakBarMsg (String msg, int dangerLevel){

        if(alertDialog==null){
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
                final View promptView = layoutInflater.inflate(R.layout.alert_dialog, null);

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DetailActivity.this);
                builder.setView(promptView);

                Button btnAck = (Button) promptView.findViewById(R.id.btnok);
                TextView txtmsg = (TextView) promptView.findViewById(R.id.txtmsg);

                txtmsg.setText(msg);

                alertDialog = builder.create();
                btnAck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        alertDialog = null;
                    }
                });
                alertDialog.show();
            }catch (Exception ex){

            }
        }
    }

    private void showGiftRules() {
//todo add this when you finish rules
        LayoutInflater layoutInflater= LayoutInflater .from(DetailActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.gift_dialog,null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DetailActivity.this);
        builder.setView(promptView);

        ListView locationlist1=(ListView)promptView.findViewById(R.id.locationlist1);
        Button btnAck = (Button) promptView.findViewById(R.id.btnAck);
        ArrayList<String> allRules = new ArrayList<String>() ;
        for (Rules num : gifts.getRules()) {
            allRules.add(num.getName());
        }
        ArrayAdapter<String> at=new ArrayAdapter<String>(DetailActivity.this, R.layout.location_item_view,allRules);

        locationlist1.setAdapter(at);
        final android.support.v7.app.AlertDialog alertDialog= builder.create();
        btnAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();



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

    //todo not finnished yet
//    private void fixHas(DataSnapshot userspace,String itemID){
//        if(userspace.child("has").exists()){
//        String hasItems[] = userspace.child("has").getValue().toString().split(",");
//
//        for (int i = 0 ; i < hasItems.length; i++){
//            if(itemID.equals(hasItems[i])){
//                return;
//            }
//            if(!hasItems[i].equals("")){
//                hasFix = hasItems[i] + ",";


//            }
//
//        }
//        }
//
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.linearLike:
                syncRating(1, 0);
                break;

            case R.id.linearDislike:
                syncRating(0, 1);
                break;
        }
    }

    public void collect() {
        //setUpNotifcation();
        CollectFromServer();

    }

    private void setUpNotifcation() {

        Intent myIntent = new Intent(DetailActivity.this , SendNotificationAfterDays.class);
//        myIntent.putExtra("ID_Item",gifts.getGift_map_id());
//        myIntent.putExtra("ID_User",userSession.getId());
        myIntent.putExtra("ID_Item","53773");
        myIntent.putExtra("ID_User","44361");
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(DetailActivity.this, 0, myIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , pendingIntent);
    }

    public static String convertToEnglishDigits(String value)
    {

        String newValue = value.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("٨", "8").replace("٩", "9").replace("٠", "0")
                .replace("٫",",").replace("٧","7");

        return newValue;
    }

    public void CollectFromServer() {
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }

        if(task != null)
            task.cancel(true);

        task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(
                                DetailActivity.this,
                                R.string.to_collection));
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateDelete(response);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = syncDelete();
            }
        });
        task.execute();
    }

    public DataResponse syncDelete() {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", userSession.getId()));
        params.add(new BasicNameValuePair("gift_map_id", String.valueOf(gifts.getGift_map_id())) );
        params.add(new BasicNameValuePair("contract_id", String.valueOf(gifts.getContract())));

        DataResponse response = DataParser.getJSONFromUrlWithPostRequest(Config.COLLECT_GIFT, params,getApplicationContext());
        return response;
    }


    public void updateDelete(DataResponse response) {
        if(response == null) {
            MGUtilities.showAlertView(
                    this,
                    R.string.sync_error,
                    R.string.problems_encountered_while_syncing);
            return;
        }

        Status status = response.getStatus();
        if(response != null && status != null) {
            if(status.getStatus_code() == 1 ) {
                Intent i = new Intent();
                i.putExtra("is_deleted", 1);
                i.putExtra("removed",true);
                setResult(Activity.RESULT_OK, i);

                databaseCollection.child("Sighting_id").setValue(String.valueOf(gifts.getGift_map_id())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            databaseCollection.child("Name").setValue(gifts.getName());
                            databaseCollection.child("Loc").setValue(String.valueOf(gifts.getLat()+"/"+gifts.getLon()));
                            databaseCollection.child("Distance").setValue(String.valueOf(gifts.getDistance()));
                            databaseCollection.child("scan").setValue("0");
                            databaseCollection.child("item_id").setValue(String.valueOf(gifts.getMerchant_id()));
                            databaseCollection.child("type").setValue(gifts.getMerchant_name());
                            databaseItems.setValue("Taken");
                            dataBaseItemDuplecate.removeEventListener(valueEventListenerDuplecate);
                            databaseItems.removeEventListener(valueEventListenerDatabaseItems);

                            //send rules
                            String rules = "";
                            for (int i = 0; i < gifts.getRules().size(); i++) {
                                rules =rules+ gifts.getRules().get(i).getName() +",";
                            }
                            if(rules!="")
                                databaseCollection.child("rules").setValue(rules);

                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_YEAR, 7);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                            String formatted = format.format(cal.getTime());
                            databaseCollection.child("expiry").setValue(formatted);

                            rate();
                        }
                    }
                });

//                if(Config.WILL_SHOW_ADS)
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                    isShowingIntersitital = true;
//                    Log.e("showingadd","aass2");
//                }else{
//                    rate();
//
//                    }
            }
            else {
                MGUtilities.showAlertView(this, R.string.action_error, status.getStatus_text());
            }
        }
    }

    public void rate(){

        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(DetailActivity.this);
        //alert.setTitle(getResources().getString(R.string.Redeem_items_confirm));
        alert.setMessage(getResources().getString(R.string.Rate_Us));
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rateApp();
                finish();
            }


        });
        alert.setNegativeButton(getResources().getString(R.string.Dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alert.create();
        alert.show();

    }


    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.menuReport:
                Intent i = new Intent(this, ReportActivity.class);
                i.putExtra("sighting",gifts);
                startActivityForResult(i, Config.REQUEST_CODE_REPORT);
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroy()  {
        super.onDestroy();
        if(task != null)
            task.cancel(true);

        if(timerInterstitial != null)
            timerInterstitial.cancel();
    }

    public void syncRating(int like, int dislike) {

        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("like", String.valueOf(like) ));
        params.add(new BasicNameValuePair("dislike", String.valueOf(dislike) ));
        params.add(new BasicNameValuePair("sighting_id", String.valueOf(gifts.getGift_map_id())) );
        params.add(new BasicNameValuePair("api_key", Config.API_KEY ));
        UserSession userSession = UserAccessSession.getInstance(this).getUserSession();
        params.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getId())) );


        task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(DetailActivity.this, R.string.evaluating));
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateSync(response);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = sync(params);
            }
        });
        task.execute();

    }

    public DataResponse sync(ArrayList<NameValuePair> params) {
        DataResponse response = DataParser.getJSONFromUrlWithPostRequest(Config.RATE_GIFT, params,getApplicationContext());
        return response;
    }

    public void updateSync(DataResponse response) {
        if(response == null) {
            MGUtilities.showAlertView(
                    this,
                    R.string.sync_error,
                    R.string.problems_encountered_while_syncing);
            return;
        }

        Status status = response.getStatus();
        if(response != null && status != null) {
            if(status.getStatus_code() == -1) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                //showIntersitial();
                finish();
            }
            else {
                MGUtilities.showAlertView(this, R.string.network_error, status.getStatus_text());
            }
        }
    }





    public void showIntersitial() {
        Log.e("showingadd","aass5");
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
                    loadRequest();
                    rate();
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
                    //Toast.makeText(DetailActivity.this,"add ready",Toast.LENGTH_LONG).show();
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



    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showUserInfo(){
         LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
         final View promptView = layoutInflater.inflate(R.layout.user_info_dialog, null);

         android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(DetailActivity.this);
         builder1.setView(promptView);
        //builder.setTitle("Please select a location");
         //builder1.setCancelable(false);

         btndismis = (Button) promptView.findViewById(R.id.btnDissmiss) ;
         name = (TextView) promptView.findViewById(R.id.popFullName);
         imgFacebook = (ImageView) promptView.findViewById(R.id.imgFaceBook);
         imgInstagram = (ImageView) promptView.findViewById(R.id.imgInstagram);
         imgEmail = (ImageView) promptView.findViewById(R.id.imgEmail);
         imgdailer = (ImageView) promptView.findViewById(R.id.imgDail) ;
         imgProfile = (ImageView) promptView.findViewById(R.id.imgViewThumbUserpop) ;




        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFacebookIntent();
            }
        });

        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenInsta();
            }
        });

        imgdailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDailer();
            }
        });

         getDataUser();

         final android.support.v7.app.AlertDialog alertDialog = builder1.create();
         btndismis.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 alertDialog.dismiss();
             }
         });


         alertDialog.show();
    }

    private void openDailer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+responseMerchant.getPhone_number()));
        startActivity(intent);
    }

    private void OpenInsta() {

        try {


            Uri uri = Uri.parse(responseMerchant.getInstagram());

            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(responseMerchant.getInstagram())));
            }}
        catch (Exception ex){
            Toast.makeText(DetailActivity.this,getResources().getString(R.string.action_error),Toast.LENGTH_SHORT).show();
        }
    }
    public void openFacebookIntent() {

        try {
            Log.e("facebooklink", responseMerchant.getFace_book());
            startActivity(newFacebookIntent(getPackageManager(), responseMerchant.getFace_book()));
        }catch (Exception e){
            Toast.makeText(DetailActivity.this,getResources().getString(R.string.action_error),Toast.LENGTH_SHORT).show();
        }

    }

    public  Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);

        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        } catch (Exception e){
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        }

        return new Intent(Intent.ACTION_VIEW, uri);
    }



    private void getDataUser(){
        task = new MGAsyncTask(DetailActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(DetailActivity.this, R.string.loading) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("user_id", userSession.getId()));
                    params.add(new BasicNameValuePair("id",gifts.getMerchant_id() ));
                    responseMerchant = DataParser.getJSONFromUrlWithPostRequestUser(Config.GET_MERCHANT, params,getApplicationContext());
                    if (responseMerchant == null)
                        return;

                     runOnUiThread(new Runnable() {

                          @Override
                        public void run() {
                        // Stuff that updates the UI
                              updateTextUser();
                        }
                      });

            }
        });
        task.execute();
    }

    private void updateTextsDialog() {



        if(responseMerchant.getFace_book().equals("None")||responseMerchant.getFace_book().equals("null")||responseMerchant.getFace_book().equals("")){
            imgFacebook.setVisibility(View.GONE);
        }
        if(responseMerchant.getInstagram().equals("None")||responseMerchant.getInstagram().equals("null")||responseMerchant.getInstagram().equals("")){
            imgInstagram.setVisibility(View.GONE);
        }
        if(responseMerchant.getEmail().equals("None")||responseMerchant.getEmail().equals("null")||responseMerchant.getEmail().equals("")){
            imgEmail.setVisibility(View.GONE);
        }
        if(responseMerchant.getPhone_number().equals("None")||responseMerchant.getPhone_number().equals("null")||responseMerchant.getPhone_number().equals("")){
            imgdailer.setVisibility(View.GONE);
        }
        if(!responseMerchant.getName().equals("None")||!responseMerchant.getName().equals("null")||!responseMerchant.getName().equals("")){
            TextView tvFullName = (TextView) findViewById(R.id.tvFullName);
            tvFullName.setText(responseMerchant.getName());
        }
        //todo add merchant
        if(!responseMerchant.getImage().equals("")){


            PokemonApplication.getImageLoaderInstance(this).
                    displayImage(
                            responseMerchant.getImage(),
                            imgProfile,
                            PokemonApplication.getDisplayImageOptionsThumbInstance());


        }
    }
}
