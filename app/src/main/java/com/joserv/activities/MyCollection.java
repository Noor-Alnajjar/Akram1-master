package com.joserv.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joserv.Akram.R;
import com.models.Collection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyCollection extends AppCompatActivity {

    private FirebaseRecyclerAdapter<Collection,CollectionAdapter> firebaseRecyclerAdapter1;
    private DatabaseReference databaseCollection;
    private DatabaseReference databasehas; // when item expired will delete from has as well
    private DatabaseReference databaseItemsTaken; // to remove item id from items (takenItems)
    private String user_id;
    private RecyclerView collictionList;
    private TextView textView;
    private ImageView imgView;
    private String has[];
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.showLoading(MyCollection.this,true);

        textView = (TextView)findViewById(R.id.mTextField) ;
        imgView = (ImageView) findViewById(R.id.imgNoData);

        Intent intent =getIntent();
        user_id=intent.getStringExtra("User_id");
        databaseCollection= FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(user_id)).child("Collection");

        databasehas=FirebaseDatabase.getInstance().getReference().child("Akram").child(String.valueOf(user_id));

        databaseItemsTaken = FirebaseDatabase.getInstance().getReference().child("Akram").child("Items");

        collictionList =(RecyclerView)findViewById(R.id.collection_list);
        collictionList.setHasFixedSize(true);
        collictionList.setLayoutManager(new LinearLayoutManager(this));

        databasehas.child("has").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                has=dataSnapshot.getValue().toString().split(",");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2373364756954365/9080377931");
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });
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

        databaseCollection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    firebaseRecyclerAdapter1=new
                            FirebaseRecyclerAdapter<Collection, CollectionAdapter>(Collection.class, R.layout.collection_item_view, CollectionAdapter.class, databaseCollection) {
                                @Override
                                protected void populateViewHolder(final CollectionAdapter viewHolder, final Collection model, int position) {

                                    Utils.hideLoading();
                                    Log.e("Collection", model.toString());
                                    DatabaseReference postRef = getRef(position);
                                    final String name = postRef.getKey();


                                    viewHolder.setName(model.getName());
                                    viewHolder.setType(String.valueOf(model.getType()));
                                    if(model.getExpiry() != null){
                                    viewHolder.setExpiry(model.getExpiry());
                                        Calendar calNow = Calendar.getInstance();
                                        Calendar calExpiry = Calendar.getInstance();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        try {
                                            calExpiry.setTime(sdf.parse(model.getExpiry()));// all done
                                            if(calNow.getTimeInMillis()>calExpiry.getTimeInMillis()){ //check if item expired
                                                databasehas.child("has").setValue(null);
                                                databaseCollection.child(name).setValue(null);
                                                databaseItemsTaken.child(name).setValue(null);

                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                    }else {

                                        Calendar cal = Calendar.getInstance();
                                        cal.add(Calendar.DAY_OF_YEAR, 7);
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                                        String formatted = format.format(cal.getTime());
                                        viewHolder.setExpiry(formatted);
                                        model.setExpiry(formatted);
                                        databaseCollection.child(name)
                                                .child("expiry").setValue(formatted);

                                    }
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(model.getScan().equals("3")){
                                                Toast.makeText(MyCollection.this,"This item got redeemed",Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            Intent QrItem = new Intent(MyCollection.this, QrItem.class);

                                            Log.e("uid", name);
                                            QrItem.putExtra("uid", name);
                                            QrItem.putExtra("Name", model.getName());
                                            QrItem.putExtra("Type", model.getType());
                                            QrItem.putExtra("Dis", model.getDistance());
                                            QrItem.putExtra("Loc", model.getLoc());
                                            QrItem.putExtra("user_id", user_id);
                                            QrItem.putExtra("item_id", model.getItem_id());
                                            startActivity(QrItem);

                                            if (mInterstitialAd.isLoaded()) {
                                                mInterstitialAd.show();
                                            } else {
                                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                                            }
                                        }
                                    });
                                }
                            };
                    collictionList.setAdapter(firebaseRecyclerAdapter1);
                }else{
                    textView.setVisibility(View.VISIBLE);
                    imgView.setVisibility(View.VISIBLE);
                    Utils.hideLoading();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static class CollectionAdapter extends RecyclerView.ViewHolder{
        View mView;
        public CollectionAdapter(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setExpiry(String status){
            TextView txtExpiry =(TextView) mView.findViewById(R.id.tvExpirey);
            txtExpiry.setText(mView.getResources().getString(R.string.expiry_date) + status);

        }
        public void setName(String status){
            TextView txtAmount=(TextView) mView.findViewById(R.id.collectionName);
            //String k = mView.getResources().getString(R.string.transaction_status);
            txtAmount.setVisibility(View.VISIBLE);
            txtAmount.setText(status);
        }
        public void setType(String status){
            TextView txtAmount=(TextView) mView.findViewById(R.id.collectionType);
            //String k = mView.getResources().getString(R.string.transaction_status);
            //txtAmount.setVisibility(View.VISIBLE);
            txtAmount.setText("Type : " + status);
        }
    }
}
