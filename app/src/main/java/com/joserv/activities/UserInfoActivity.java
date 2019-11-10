package com.joserv.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.joserv.Akram.R;
import com.models.UserInfo;

public class UserInfoActivity extends AppCompatActivity {
    private int userID;
    private TextView name;
    private ImageView imgFacebook,imgInstagram,imgdailer,imgEmail,imgProfile;
    private UserInfo data;
    private String thumbUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userID = this.getIntent().getIntExtra("user_id",0);
        thumbUrl = this.getIntent().getStringExtra("thumbUrl");

        name = (TextView) findViewById(R.id.popFullName);
        imgFacebook = (ImageView) findViewById(R.id.imgFaceBook);
        imgInstagram = (ImageView) findViewById(R.id.imgInstagram);
        imgEmail = (ImageView) findViewById(R.id.imgEmail);
        imgdailer = (ImageView) findViewById(R.id.imgDail) ;
        imgProfile = (ImageView) findViewById(R.id.imgViewThumbUserpop) ;

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

        getUser();
    }

    private void openDailer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+data.getPhone()));
        startActivity(intent);
    }

    private void OpenInsta() {

        Uri uri = Uri.parse(data.getInsta_profile());
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(data.getInsta_profile())));
        }
    }

    private void getUser() {
        if(userID == 0){
            Toast.makeText(UserInfoActivity.this,getResources().getString(R.string.sync_error),Toast.LENGTH_SHORT).show();
            finish();
        }else {}
    }

    public void openFacebookIntent() {

        startActivity(newFacebookIntent(getPackageManager(),data.getFacebook_profile()));
        Log.e("facebooklink",data.getFacebook_profile());

    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

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
