package com.joserv.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.application.PokemonApplication;
import com.config.Config;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.joserv.Akram.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.dataparser.DataParser;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.Collection;
import com.models.Contracts;
import com.models.DataResponse;
import com.models.Merchant;
import com.models.Status;
import com.wang.avi.AVLoadingIndicatorView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class QrItem extends AppCompatActivity {
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH=500;
    ImageView imageView;
    private String id,name,type,loc,dis,item_id;
    private String user_id;
    private String Scanning;
    private DatabaseReference databaseCollection;
    private Button btnGiftRule,btnLocation;
    TextView scan;
    ProgressBar progressBar;
    TextView itemName,itemTime;
    AVLoadingIndicatorView loadnoscan ,loadscaned,locationloading;
    
    //akram 2.2
    private MGAsyncTask task;
    private ImageView imgFacebook,imgInstagram,imgdailer,imgEmail,imgProfile;
    private LinearLayout userLayout;
    private TextView txtname;

    //location related
    //private FirebaseRecyclerAdapter<Collection,MyCollection.CollectionAdapter> firebaseRecyclerAdapter1;

    private DatabaseReference databaselocation;
    private RecyclerView locationList;

    //akram 3.0
    private Merchant responseMerchant;
    private UserSession userSession;
    private DataResponse contacts;
    private HashMap<String,String> locations = new HashMap<String, String>();

    private int rateUser = 0;
    private InterstitialAd mInterstitialAd;
    private FirebaseRecyclerAdapter<Collection, MyCollection.CollectionAdapter> firebaseRecyclerAdapter1;

    private Dialog dialogRateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userSession = UserAccessSession.getInstance(QrItem.this).getUserSession();
        imageView=(ImageView)findViewById(R.id.im);
        itemName=(TextView)findViewById(R.id.txtItemName);
        itemTime = (TextView) findViewById(R.id.txtItemType) ;
        progressBar=(ProgressBar)findViewById(R.id.progressBar2) ;
        scan=(TextView)findViewById(R.id.txtscaning);
        btnGiftRule = (Button) findViewById(R.id.btnQrGiftRule);
        btnLocation = (Button) findViewById(R.id.btnQRLocation);

        loadnoscan =(AVLoadingIndicatorView)findViewById(R.id.avloadingIndicatorView1);
        loadscaned =(AVLoadingIndicatorView)findViewById(R.id.avloadingIndicatorView2);
        locationloading =(AVLoadingIndicatorView)findViewById(R.id.avloadingIndicatorView3);
        userLayout = (LinearLayout) findViewById(R.id.mershant_layout) ;

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationList();
            }
        });
        btnGiftRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGiftRules();
            }
        });

        Intent intent = getIntent();
        id=intent.getStringExtra("uid");
        name=intent.getStringExtra("Name");
        type=intent.getStringExtra("Type");
        dis=intent.getStringExtra("Dis");
        loc=intent.getStringExtra("Loc");
        Log.e("loc",intent.getStringExtra("Loc"));
        user_id=intent.getStringExtra("user_id");
        item_id=intent.getStringExtra("item_id");
        
        getUser(item_id);
       // getContract(item_id);
        getContractLocation(item_id);

        itemTime.setText(type);

        databaseCollection= FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(user_id)).child("Collection").child(id);
        databaseCollection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()||dataSnapshot == null){

                    rateUserDialog();

                }
                else if(dataSnapshot.child("scan").exists() && dataSnapshot.child("scan").getValue().equals("1")){
                    scan.setText(getResources().getString(R.string.item_got_read));
                    loadnoscan.setVisibility(View.GONE);
                    loadscaned.setVisibility(View.VISIBLE);
                }else if(dataSnapshot.child("scan").exists() && dataSnapshot.child("scan").getValue().equals("0")){
                    scan.setText(getResources().getString(R.string.readingqr));
                    loadnoscan.setVisibility(View.VISIBLE);
                    loadscaned.setVisibility(View.GONE);
                }
                if(dataSnapshot.child("rules").exists()&&!dataSnapshot.child("rules").getValue().equals(""))
                    btnGiftRule.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        itemName.setText(name);
        try {
            Bitmap bmp =  encodeAsBitmap(id+","+name+","+type+","+dis+","+loc+","+user_id);
            imageView.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getContract(final String user_id_merchant) {
        task = new MGAsyncTask(QrItem.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {
            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }
            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {asyncTask.dialog.setMessage(MGUtilities.getStringFromResource(QrItem.this, R.string.loading) );
            }
            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                if(locations.size()!=0)
                    btnLocation.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", userSession.getId()));
                params.add(new BasicNameValuePair("id",user_id_merchant ));
                contacts = DataParser.getJSONFromUrlWithPostRequest(Config.GET_MERCHANT_CONTRACT, params,getApplicationContext());
                Log.e("contactsLocations",Config.GET_MERCHANT_CONTRACT + "," + userSession.getId() + "," + user_id_merchant + "," + userSession.getApikey());
                if (contacts == null)
                    return;

                try{
                        for(Contracts con: contacts.getContracts()){
                            locations.put(con.getCompany_name() + "",con.getShop_lon()+","+con.getShop_lat());
                            Log.e("contactsLocations",con.getShop_lon()+","+con.getShop_lat());
                            Log.e("contactsLocations",con.getCompany_name()+ "");
                        }
                }catch (Exception e){
                    btnLocation.setVisibility(View.GONE);
                }
            }
        });
        task.execute();
    }

    private void getUser(final String user_id_merchant) {
        try {
            task = new MGAsyncTask(QrItem.this);
            task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {
                @Override
                public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) {
                }

                @Override
                public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                    asyncTask.dialog.setMessage(MGUtilities.getStringFromResource(QrItem.this, R.string.loading));
                }

                @Override
                public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    if (responseMerchant != null && responseMerchant.getName() != null)
                        updateTextsDialog();
                    else
                        userLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("user_id", userSession.getId()));
                    params.add(new BasicNameValuePair("id", user_id_merchant));
                    responseMerchant = DataParser.getJSONFromUrlWithPostRequestUser(Config.GET_MERCHANT, params, getApplicationContext());
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
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }
    private void updateTextUser() {

        txtname = (TextView) findViewById(R.id.tvFullName);
        imgFacebook = (ImageView) findViewById(R.id.imgFaceBook);
        imgInstagram = (ImageView) findViewById(R.id.imgInstagram);
        imgEmail = (ImageView) findViewById(R.id.imgEmail);
        imgdailer = (ImageView) findViewById(R.id.imgDail) ;
        imgProfile = (ImageView) findViewById(R.id.imgViewThumbUser) ;

        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openFacebookIntent();
                }catch (Exception e){
                    e.getLocalizedMessage();
                }
            }
        });

        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OpenInsta();
                }catch (Exception e){
                    e.getLocalizedMessage();
                }
            }
        });

        imgdailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openDailer();
                }catch (Exception e){
                    e.getLocalizedMessage();
                }
            }
        });
    }

    private void updateTextsDialog() {
        try {
            if (!responseMerchant.getName().equals(null))
                txtname.setText(responseMerchant.getName());

            userLayout.setVisibility(View.VISIBLE);

            if (responseMerchant.getFace_book().equals("None") || responseMerchant.getFace_book().equals("null") || responseMerchant.getFace_book().equals("")) {
                imgFacebook.setVisibility(View.GONE);
            }
            if (responseMerchant.getInstagram().equals("None") || responseMerchant.getInstagram().equals("null") || responseMerchant.getInstagram().equals("")) {
                imgInstagram.setVisibility(View.GONE);
            }
            if (responseMerchant.getEmail().equals("None") || responseMerchant.getEmail().equals("null") || responseMerchant.getEmail().equals("")) {
                imgEmail.setVisibility(View.GONE);
            }
            if (responseMerchant.getPhone_number().equals("None") || responseMerchant.getPhone_number().equals("null") || responseMerchant.getPhone_number().equals("")) {
                imgdailer.setVisibility(View.GONE);
            }
            if (!responseMerchant.getImage().equals("")) {
                PokemonApplication.getImageLoaderInstance(this).displayImage(
                        responseMerchant.getImage(), imgProfile, PokemonApplication.getDisplayImageOptionsThumbInstance());
            }
        }catch (Exception e){
            e.getLocalizedMessage();
        }
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
            Toast.makeText(QrItem.this,getResources().getString(R.string.action_error),Toast.LENGTH_SHORT).show();
        }
    }

    public void openFacebookIntent() {
        try {
            Log.e("facebooklink", responseMerchant.getFace_book());
            startActivity(newFacebookIntent(getPackageManager(), responseMerchant.getFace_book()));
        }catch (Exception e){
            Toast.makeText(QrItem.this,getResources().getString(R.string.action_error),Toast.LENGTH_SHORT).show();
        }
    }

    public  Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        } catch (Exception e){
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private void showGiftRules() {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(QrItem.this);
            final View promptView = layoutInflater.inflate(R.layout.gift_dialog, null);

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(QrItem.this);
            builder.setView(promptView);

            Button btnAck = (Button) promptView.findViewById(R.id.btnAck);
            final android.support.v7.app.AlertDialog alertDialog = builder.create();
            btnAck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });


            databaseCollection.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    ListView locationlist1 = (ListView) promptView.findViewById(R.id.locationlist1);

                    String itemList = dataSnapshot.child("rules").getValue().toString();
                    String a[] = itemList.split(",");
                    ArrayAdapter<String> at = new ArrayAdapter<String>(QrItem.this, R.layout.location_item_view, a);

                    locationlist1.setAdapter(at);

                    locationlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            alertDialog.show();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    private void showLocationList() {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(QrItem.this);
            final View promptView = layoutInflater.inflate(R.layout.locations_list, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(QrItem.this);
            builder.setView(promptView);

            ListView locationlist1 = (ListView) promptView.findViewById(R.id.locationlist1);
            ArrayList<String> itemList = new ArrayList<String>();
            for (String loc : locations.keySet()) {
                itemList.add(loc);
            }

            ArrayAdapter<String> at = new ArrayAdapter<String>(QrItem.this, R.layout.location_item_view, itemList);

            locationlist1.setAdapter(at);

            locationlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String loc = locations.get(parent.getItemAtPosition(position).toString());
                    Log.e("Loc", loc);
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + loc));
                    startActivity(intent);
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                Log.e("back", "yes");
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
    Bitmap encodeAsBitmap(String str) throws WriterException {
        Bitmap bitmap=null;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);

            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            BitMatrix bitMatrix = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 500, 500, hintMap);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        }catch (Exception e){
            e.getLocalizedMessage();
        }
        return bitmap;
    }
    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }
    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void getContractLocation(final String user_id_merchant) {
        try {
            String s_ApiKey = "";
            if (userSession != null)
                if (userSession.getId() != null)
                    s_ApiKey = userSession.getApikey();

            Map<String, String> params = new HashMap<String, String>();
            params.put("x-api-key", s_ApiKey);
            AndroidNetworking.post(Config.GET_CONTRACT_LOCATION)
                    .addBodyParameter("user_id", String.valueOf(userSession.getId()))
                    .addBodyParameter("id", user_id_merchant)
                    .addHeaders(params)
                    .setContentType("multipart/form-data; charset=utf-8")
                    .setTag("test")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject status = response.getJSONObject("status");
                                String status_code = status.getString("status_code");
                                if (status_code.equals("1")) {

                                    JSONArray contracts = response.getJSONArray("contracts");
                                    if (contracts.length() != 0) {

                                        String branch_name = contracts.getJSONObject(0).getString("branch_name");
                                        String shop_lat = contracts.getJSONObject(0).getString("shop_lat");
                                        String shop_lon = contracts.getJSONObject(0).getString("shop_lon");

                                        locations.put(branch_name + "", shop_lon + "," + shop_lat);
                                        btnLocation.setVisibility(View.VISIBLE);
                                    } else {
                                        btnLocation.setVisibility(View.GONE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("anExceptionError", e.toString());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                        Log.e("anError", anError.toString());
                        }
                    });
        } catch (Exception e){
            e.getLocalizedMessage();
            Log.e("anExceptionError1", e.toString());
        }
    }

    public void rate_merchant(float v) {

        String s_ApiKey = "";
        UserSession userSession = UserAccessSession.getInstance(QrItem.this).getUserSession();
        if (userSession != null)
            if (userSession != null)
                s_ApiKey = userSession.getApikey();

        Map<String, String> params = new HashMap<String, String>();
        params.put("x-api-key", s_ApiKey);

        Log.e("RATE_MERCHANT", Config.RATE_MERCHANT + "," + userSession.getId() + "," + responseMerchant.getId() + "," + s_ApiKey);
        AndroidNetworking.post(Config.RATE_MERCHANT)
                .addBodyParameter("user_id", user_id)
                .addBodyParameter("merchant_id", responseMerchant.getId())
                .addBodyParameter("rating", String.valueOf(v))
                .addBodyParameter("x-api-key", s_ApiKey)
                .addHeaders(params)
                .setContentType("multipart/form-data; charset=utf-8")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject status = response.getJSONObject("status");
                            if (status.getString("status_code").equals("1")) {

                                String status_text = status.getString("status_text");

                                final Dialog dialog = new Dialog(QrItem.this);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.custom_dialog);

                                final TextView txtmsg = (TextView) dialog.findViewById(R.id.txtmsg);
                                Button btnokDialog = (Button) dialog.findViewById(R.id.btnokDialog);
                                Button btncancelDialog = (Button) dialog.findViewById(R.id.btncancelDialog);

                                txtmsg.setText(status_text);
                                btncancelDialog.setVisibility(View.INVISIBLE);
                                btncancelDialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                btnokDialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        try{
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(QrItem.this);
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
                                            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialog) {
                                                    finish();
                                                }
                                            });
                                            alert.create();
                                            alert.show();
                                        }catch (Exception ex){}

                                    }
                                });
                                dialog.show();
                            }
                        } catch (Exception e) {
                            Log.e("test5", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("test6", anError.toString());
                    }
                });
    }

    private void rateUserDialog(){

        dialogRateUser = new Dialog(QrItem.this);
        dialogRateUser.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogRateUser.setContentView(R.layout.rate_dailog);

        dialogRateUser.setCancelable(false);
        RatingBar rateBar = (RatingBar) dialogRateUser.findViewById(R.id.ratingBarGift);

        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                dialogRateUser.dismiss();
                rate_merchant(v);
            }
        });
        dialogRateUser.show();
    }
    @Override
    public void onBackPressed() {
        try {
            databaseCollection = FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(user_id)).child("Collection").child(id);
            databaseCollection.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.e("yes", "exist");
                        QrItem.super.onBackPressed();
                    } else {
                        Log.e("no", "not exist");
                        rateUserDialog();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            e.getLocalizedMessage();
            QrItem.super.onBackPressed();
        }
    }
}
