package com.joserv.Akram;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.application.PokemonApplication;
import com.config.Config;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.joserv.activities.AboutUsActivity;
import com.joserv.activities.EventsActivity;
import com.joserv.activities.MyCollection;
import com.joserv.activities.NotificationHistory;
import com.joserv.activities.TradeActivity;
import com.joserv.activities.TermsActivity;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.dbtiny.TinyDB;
import com.libraries.imageview.RoundedImageView;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.joserv.activities.LoginActivity;
import com.joserv.activities.MySightingsActivity;
import com.joserv.activities.ProfileActivity;
import com.joserv.activities.RegisterActivity;
import com.joserv.fragments.MainFragment;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.models.Category;
import com.models.DataResponse;
import com.models.Interest;
import com.services.NearByGiftsNotification;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static boolean active = false;

    Fragment currFragment;
    boolean isLoggedPrev, isLoggedCurrent;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    //todo ad this
//    private AdView adView;
//    InterstitialAd mInterstitialAd;
    boolean isShowingIntersitital = false;
    Timer timerInterstitial;
    private UserSession userSession;

    //Akram 3.0
    private Interest interest;
    private LinearLayout LLCheckBoxesIntresrt;
    private Button SubmitIntresrt;
    private ProgressBar progressBarIntresrt;
    private ArrayList<String> checkBoxesListIntresrtArray = new ArrayList<String>();
    private AlertDialog alertDialog1;

    private AdView adView;
    InterstitialAd mInterstitialAd;

    private AdView mAdView;
    private String mAppUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar).bringToFront();

        HashMap<String, String> hashMap = new HashMap<String, String>();
        active = true;
        startService(new Intent(this, NearByGiftsNotification.class));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        //coloring the title for the both categories
        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(R.id.tools);
        MenuItem toolsCategory = menu.findItem(R.id.toolsCategory);
        SpannableString s = new SpannableString(tools.getTitle());
        SpannableString s1 = new SpannableString(toolsCategory.getTitle());
        s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.TextAppearance44), 0, s.length(), 0);
        s1.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.TextAppearance44), 0, s1.length(), 0);
        tools.setTitle(s);
        toolsCategory.setTitle(s1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //slidemenu setup
                if (UserAccessSession.getInstance(MainActivity.this).getUserSession() != null) {
                    isLoggedCurrent = true;
                } else {
                    isLoggedCurrent = false;
                }
                checkLoginNavigation();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        MainActivityPermissionsDispatcher.showFragmentWithPermissionCheck(this, new MainFragment());
        checkFirstRun();

        //todo add this
        //showAds();
        //showIntersitial();

        mAppUnitId = "ca-app-pub-2373364756954365~5948197577";

        mAdView = findViewById(R.id.adView);

        initializeBannerAd(mAppUnitId);

        loadBannerAd();
        hideSoftKeyboard();
    }

    private void initializeBannerAd(String appUnitId) {

        MobileAds.initialize(this, appUnitId);

    }

    private void loadBannerAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    public void checkLoginNavigation() {
        navigationView.getMenu().clear();
        userSession = UserAccessSession.getInstance(MainActivity.this).getUserSession();
        RoundedImageView imgViewThumb = (RoundedImageView) navigationView.findViewById(R.id.imgViewThumb);
        TextView txtfullname = (TextView) navigationView.findViewById(R.id.txtName_header);
        TextView txtScoreHeader = (TextView) navigationView.findViewById(R.id.txtScoreHeader);
        CircularProgressBar circularProgressBar = (CircularProgressBar) navigationView.findViewById(R.id.circularProgressBar);


        //placing the user info into the header
        if (userSession != null && userSession.getFull_name() != null) {
            txtfullname.setText(userSession.getFull_name());
            txtScoreHeader.setText(userSession.getScore());
            circularProgressBar.setProgress(Integer.valueOf(userSession.getScore()));
            if (userSession.getImage() != null) {
                PokemonApplication.getImageLoaderInstance(MainActivity.this).displayImage(
                        userSession.getImage(),
                        imgViewThumb,
                        PokemonApplication.getDisplayImageOptionsThumbInstance());
            }
        }


        if (userSession != null) {
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);
        } else {
            navigationView.inflateMenu(R.menu.activity_main_drawer_not_logged);
        }
        //coloring the title for the both categories
        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(R.id.tools);
        MenuItem toolsCategory = menu.findItem(R.id.toolsCategory);
        SpannableString s = new SpannableString(tools.getTitle());
        SpannableString s1 = new SpannableString(toolsCategory.getTitle());
        s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.TextAppearance44), 0, s.length(), 0);
        s1.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.TextAppearance44), 0, s1.length(), 0);
        tools.setTitle(s);
        toolsCategory.setTitle(s1);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                System.exit(1);
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.tap_back_again_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main
                , menu);
        return true;
    }
    private void checkFirstRun() {
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
        } else if (currentVersionCode > savedVersionCode) {}

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_notification:
                openNotification();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openNotification() {
        Intent i = new Intent(this, NotificationHistory.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            MainActivityPermissionsDispatcher.showFragmentWithPermissionCheck(this, new MainFragment());
            setTitle(R.string.app_name);
        }

        if (id == R.id.nav_about_us) {
            Intent i = new Intent(this, AboutUsActivity.class);
            startActivity(i);
        }

        if (id == R.id.nav_terms_condition) {
            setTitle(R.string.app_name);
            Intent i = new Intent(this, TermsActivity.class);
            startActivity(i);
        }

        if (id == R.id.nav_event) {
            try {
                if(userSession.getId() == null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }else{
                    setTitle(R.string.app_name);
                    Intent i = new Intent(this, EventsActivity.class);
                    i.putExtra("User_id", String.valueOf(userSession.getId()));
                    startActivity(i);

                }
            }catch (Exception e){
                e.getLocalizedMessage();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }
        if (id == R.id.nav_login_register) {
            setTitle(R.string.app_name);
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("LauncherActivity", "MainActivity");
            startActivity(i);
        }

        if (id == R.id.nav_register) {
            setTitle(R.string.app_name);
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }

        if (id == R.id.nav_profile) {
            setTitle(R.string.app_name);
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }

        if (id == R.id.nav_my_sightings) {
            setTitle(R.string.app_name);

            Intent i = new Intent(this, MySightingsActivity.class);
            startActivity(i);
        }

        if (id == R.id.nav_my_collection) {
            setTitle(R.string.app_name);
            Intent i = new Intent(this, MyCollection.class);
            i.putExtra("User_id", String.valueOf(userSession.getId()));
            startActivity(i);
        }
        if (id == R.id.nav_trade) {

            setTitle(R.string.app_name);

            Intent i = new Intent(this, TradeActivity.class);
            startActivity(i);
        }
        if (id == R.id.nav_boarding) {

            setTitle(R.string.app_name);

            Intent i = new Intent(this, ActivityNeedHelp.class);
            startActivity(i);
        }
        if (id == R.id.nav_my_Intrest) {

            setTitle(R.string.app_name);

            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            final View promptView = layoutInflater.inflate(R.layout.my_intrest, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(promptView);

            alertDialog1 = builder.create();

            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            LLCheckBoxesIntresrt = (LinearLayout) promptView.findViewById(R.id.LLCheckBoxes);
            SubmitIntresrt = (Button) promptView.findViewById(R.id.btnSubmitIntrest);
            progressBarIntresrt = (ProgressBar) promptView.findViewById(R.id.PBMyIntrest);


            SubmitIntresrt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setInterest();
                }
            });
            getCheckBoxes();
            alertDialog1.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setInterest() {
        final TinyDB tinydb = new TinyDB(MainActivity.this);
        final ArrayList<String> notificationHis = new ArrayList<String>();


        MGAsyncTask task = new MGAsyncTask(MainActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;


            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) {
            }
            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(MainActivity.this, R.string.sending_interest));

            }
            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                alertDialog1.dismiss();

            }
            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", userSession.getId()));
                int interestIndex = 0;
                for (String cat : checkBoxesListIntresrtArray) {
                    params.add(new BasicNameValuePair("interest[" + interestIndex + "]", cat));
                    notificationHis.add(cat);
                    interestIndex++;
                    tinydb.putListString("Interest", notificationHis);
                }
                response = DataParser.getJSONFromUrlWithPostRequest(Config.SEND_INTEREST, params, getApplicationContext());
            }
        });
        task.execute();
    }

    private void getCheckBoxes() {
        MGAsyncTaskNoDialog task = new MGAsyncTaskNoDialog(MainActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", userSession.getId()));
                interest = DataParser.getJSONFromUrlWithPostCategorys(Config.GET_CATEGORY, params, getApplicationContext());
                if (interest == null)
                    return;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        //updateTextUser();
                        TinyDB tinydb = new TinyDB(MainActivity.this);
                        checkBoxesListIntresrtArray = tinydb.getListString("Interest");

                        for (final Category cat : interest.getCategory()) {

                            CheckBox checkBox = new CheckBox(MainActivity.this);
                            checkBox.setText(cat.getName());
                            checkBox.setTextColor(Color.WHITE);
                            // Apply right padding of Flex CheckBox
                            checkBox.setPadding(30, 0, 50, 0);
                            checkBox.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_selector));
                            if (checkBoxesListIntresrtArray.contains(cat.getId()))
                                checkBox.setChecked(true);

                            checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (checkBoxesListIntresrtArray.contains(cat.getId()))
                                        checkBoxesListIntresrtArray.remove(cat.getId());
                                    else
                                        checkBoxesListIntresrtArray.add(cat.getId());
                                }

                            });
                            LLCheckBoxesIntresrt.addView(checkBox);
                        }
                        SubmitIntresrt.setEnabled(true);
                    }
                });
            }
            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) {}
            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                progressBarIntresrt.setVisibility(View.GONE);
                LLCheckBoxesIntresrt.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) {}
        });
        task.execute();
        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void showFragment(Fragment fragment) {

        if (currFragment != null && fragment.getClass().equals(currFragment.getClass()))
            return;

        currFragment = fragment;
        if (fragment instanceof MainFragment) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, currFragment).commit();
                }
            }, Config.DELAY_MAP_SHOW_ANIMATION);
        } else {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            if (fragmentManager == null)
                return;

            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft == null)
                return;

            ft.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currFragment instanceof MainFragment) {
            currFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onDestroy() {
        active = false;
        super.onDestroy();

        if (timerInterstitial != null)
            timerInterstitial.cancel();
    }
    @Override
    protected void onResume() {
        active = true;
        super.onResume();
    }

    public void showAds() {
        try {

            String deviceId = MGUtilities.getDeviceID(getApplicationContext());
            Log.e("DEVICE ID", "------------------------------------------");
            Log.e("DEVICE ID", deviceId);
            Log.e("DEVICE ID", "------------------------------------------");

            FrameLayout frameAds = (FrameLayout) findViewById(R.id.frameAds);

            if (Config.WILL_SHOW_ADS) {
                frameAds.setVisibility(View.VISIBLE);
                if (adView == null) {
                    adView = new AdView(this);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId(Config.BANNER_AD_UNIT_ID);
                    adView.setMinimumHeight(100);

                    frameAds.addView(adView);


                    AdRequest.Builder builder = new AdRequest.Builder();
                    if (Config.TEST_ADS_USING_TESTING_DEVICE)
                        builder.addTestDevice(Config.TESTING_DEVICE_HASH);

                    AdRequest adRequest = builder.build();
                    // Start loading the ad in the background.
                    adView.loadAd(adRequest);
                }
            } else {
                frameAds.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    public void showIntersitial() {

        try {
            if (mInterstitialAd == null)
                mInterstitialAd = new InterstitialAd(this);

            if (Config.SHOW_INTERSTITIAL) {
                // set the ad unit ID
                mInterstitialAd.setAdUnitId(Config.INTERSTITIAL_AD_UNIT_ID);

                loadRequest();

                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        isShowingIntersitital = false;
                        loadRequest();
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
                    }
                });

                beginInterstial();
            }
        } catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    public void beginInterstial() {
        TimerTask timerTask = new InterstitialTimerTask();
        //running timer task as daemon thread
        timerInterstitial = new Timer(true);
        timerInterstitial.scheduleAtFixedRate(timerTask, 0, Config.INTERSTITIAL_DELAY_IN_SECONDS * 1000);
    }

    public class InterstitialTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //todo ad this
                    if (mInterstitialAd.isLoaded() && !isShowingIntersitital) {
                        mInterstitialAd.show();
                        isShowingIntersitital = true;
                    }
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

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
