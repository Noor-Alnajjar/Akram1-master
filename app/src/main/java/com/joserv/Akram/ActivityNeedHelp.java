package com.joserv.Akram;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.models.SliderAdapter2;

public class ActivityNeedHelp extends Activity {

    private ViewPager mSlideViewPage;
    private LinearLayout mDotLayout;
    private SliderAdapter2 sliderAdapter;
    private TextView[] mDots;
    private Button mNextBtn;
    private Button mBackBtn;
    private TextView txtWelcome;
    private ImageView imgAkram;
    private TextView txtwaitingforlocation;
    private ProgressBar progressBarLocation;
    private int mCureentpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);

        Activity activity = ActivityNeedHelp.this;
        Window window = activity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // only for lolipop and newer versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        }

        mSlideViewPage = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mBackBtn = (Button) findViewById(R.id.prevBtn);
        txtWelcome = (TextView) findViewById(R.id.txtAkramtitle);
        imgAkram = (ImageView) findViewById(R.id.imgAkram);
        txtwaitingforlocation = (TextView) findViewById(R.id.txtWaitingLocation);
        progressBarLocation = (ProgressBar) findViewById(R.id.PBHowToPlay);

        sliderAdapter = new SliderAdapter2(this);
        mSlideViewPage.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPage.addOnPageChangeListener(viewListener);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCureentpage == mDots.length - 1) {
                    finish();
                } else
                    mSlideViewPage.setCurrentItem(mCureentpage + 1);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPage.setCurrentItem(mCureentpage - 1);
            }
        });
    }
    public void addDotsIndicator(int postion) {
        mDots = new TextView[7];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.cardview_dark_background));
            mDotLayout.bringToFront();
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[postion].setTextColor(getResources().getColor(R.color.color_white));
        }
    }
    boolean locationpermitionrequested = false;
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (!locationpermitionrequested && positionOffset != 0.0) {
                if (position == 8) {
                    mSlideViewPage.beginFakeDrag();
                    checkLocationPermission();locationpermitionrequested = true;
                }
            }
            Log.e("postion", String.valueOf(position));
        }
        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            mCureentpage = position;


            if (position == 0) {
                mNextBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText(R.string.next);

            } else if (position == mDots.length - 1) {
                mNextBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);
                txtwaitingforlocation.setVisibility(View.GONE);
                progressBarLocation.setVisibility(View.GONE);

                mNextBtn.setText(R.string.finish);
                mBackBtn.setText(R.string.back);
            } else {
                mNextBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);
                txtwaitingforlocation.setVisibility(View.GONE);
                progressBarLocation.setVisibility(View.GONE);
                txtWelcome.setVisibility(View.GONE);
                imgAkram.setVisibility(View.GONE);

                mNextBtn.setText(R.string.next);
                mBackBtn.setText(R.string.back);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, R.anim.trans_slide_in_left);
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setMessage(R.string.location_permistion)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ActivityNeedHelp.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        txtWelcome.setVisibility(View.VISIBLE);
                        imgAkram.setVisibility(View.VISIBLE);
                        mNextBtn.setVisibility(View.VISIBLE);
                        txtwaitingforlocation.setVisibility(View.GONE);
                        progressBarLocation.setVisibility(View.GONE);
                    }
                }else{
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
