package com.joserv.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.joserv.Akram.R;

public class AboutUsActivity extends AppCompatActivity {


    private View viewInflate;
    private ImageView facebook,instagram,email;
    private TextView txtLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {

        txtLink = (TextView) findViewById(R.id.akramlink);
        facebook = (ImageView) findViewById(R.id.imgFaceBook);
        instagram = (ImageView) findViewById(R.id.imgInstagram);
        email = (ImageView) findViewById(R.id.imgEmail);
        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openwebsite();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFacebookIntent();
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenInsta();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmail();
            }
        });
    }


    private void openwebsite() {
        String url = "http://www.akramapp.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void openEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, "info@joserv.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contacting AkramApp");
        intent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }


    private void OpenInsta() {

        Uri uri = Uri.parse("https://insta-stalker.com/profile/akram_app/");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://insta-stalker.com/profile/akram_app/")));
        }
    }

    public void openFacebookIntent() {

        startActivity(newFacebookIntent(getPackageManager(),"https://www.facebook.com/AkramAppOffical/"));


    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

}
