package com.joserv.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.PokemonApplication;
import com.config.Config;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.joserv.Akram.MainActivity;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.dataparser.DataParser;
import com.libraries.twitter.TwitterApp;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Status;
import com.models.User;
import com.joserv.Akram.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private TwitterApp mTwitter;
    private MGAsyncTask task;
    private CallbackManager mCallbackManager;
    String _imageURL;
    String _name;

    AppCompatButton btnFacebook;
    AppCompatButton btnTwitter;
    AppCompatButton btnGooglePlus;
    GoogleApiClient mGoogleApiClient;
    EditText txtUsername;
    EditText txtPassword;

    //forget password
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String launcherActivity = intent.getStringExtra("LauncherActivity");

        //to hide those if not splash activity launching this Activity
        LinearLayout btnReg = (LinearLayout) findViewById(R.id.btnRegester);
        TextView txtSkip = (TextView) findViewById(R.id.txtSkipLogin);
        TextView txtforget = (TextView) findViewById(R.id.txtForgetPassword);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtUsername.requestFocus();
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        if(!launcherActivity.equals("SplashActivity")){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtSkip.setVisibility(View.GONE);
        }


        mTwitter = new TwitterApp(this, twitterAppListener);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("LoginManager", "Login Success");
                        getUserProfile(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Log.d("LoginManager", "Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LoginManager", exception.getMessage());
                    }
                });


        btnFacebook = (AppCompatButton) findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(this);

        txtSkip.setOnClickListener(this);
        txtforget.setOnClickListener(this);

        Button btnLogin = (AppCompatButton) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnReg.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        PokemonApplication app = (PokemonApplication) getApplication();
        mGoogleApiClient = app.getGoogleApiClientInstance();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getUserProfile(LoginResult loginResult) {
        String accessToken = loginResult.getAccessToken().getToken();
        Log.i("accessToken", accessToken);
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        syncFacebookUser(object, response);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnFacebook:
                loginToFacebook();
                break;
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnRegester:
                regester();
                break;
            case R.id.txtSkipLogin:
                startmain();
                break;
            case R.id.txtForgetPassword :
                forgetPassword();
                
                break;
        }
    }

    private void forgetPassword() {

        LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.forget_password_dialog, null);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
        builder1.setView(promptView);
        final EditText txtEmail = (EditText) promptView.findViewById(R.id.txtEmailForgetDialog);
        Button btnSubmit = (Button) promptView.findViewById(R.id.btnsubmitSendforgetDialog);
        alertDialog = builder1.create();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(txtEmail.getText()) || !isValidEmail(txtEmail.getText())) {

                    txtEmail.setError(getResources().getString(R.string.empty_field_email));

                    return;
                }

                alertDialog.dismiss();
                sendForgetRequest(txtEmail.getText().toString());

            }
        });

        alertDialog.show();

    }

    private void sendForgetRequest(final String s) {
            task = new MGAsyncTask(LoginActivity.this);
            task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

                DataResponse response;

                @Override
                public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

                @Override
                public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                    asyncTask.dialog.setMessage(
                            MGUtilities.getStringFromResource(LoginActivity.this, R.string.reseting_password) );
                }

                @Override
                public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    Toast.makeText(LoginActivity.this,R.string.email_sent,Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) LoginActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = LoginActivity.this.getCurrentFocus();
                    if (view == null) {
                        view = new View(LoginActivity.this);
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }

                @Override
                public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("email",s ));
                    params.add(new BasicNameValuePair("api_key", Config.API_KEY ));
                    response = DataParser.getJSONFromUrlWithPostRequest(Config.FORGET_PASSWORD, params,getApplicationContext());
                }
            });
            task.execute();
    }

    private void startmain() {
        finish();
        startActivity(new Intent(LoginActivity.this,MainActivity.class));

    }

    private void regester() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        intent.putExtra("LauncherActivity","RegisterActivity");

        startActivity(intent);
    }

    public void loginToFacebook() {
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    LoginActivity.this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }

        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("email", "public_profile"));
    }

    public void updateLogin(DataResponse response, String imageURL, String name) {

        if(response == null) {
            MGUtilities.showAlertView(
                    LoginActivity.this,
                    R.string.login_error,
                    R.string.problems_encountered_login);
            return;
        }

        Status status = response.getStatus();
        if(response != null && status != null) {
            if(status.getStatus_code() == 1 && response.getUser() != null ) {
                User user = response.getUser();
                if(user != null) {
                    UserAccessSession session = UserAccessSession.getInstance(this);
                    UserSession userSession = new UserSession();

                    userSession.setId(String.valueOf(user.getId()));
                    userSession.setName(String.valueOf(user.getName()));
                    userSession.setFull_name(String.valueOf(user.getFull_name()));
                    userSession.setEmail(String.valueOf(user.getEmail()));
                    userSession.setGender(String.valueOf(user.getGender()));
                    userSession.setImage(String.valueOf(user.getImage()));
                    userSession.setScore(String.valueOf(user.getScore()));
                    userSession.setFacebook_profile(String.valueOf(user.getFacebook_profile()));
                    userSession.setInsta_profile(String.valueOf(user.getInsta_profile()));
                    userSession.setApikey(String.valueOf(user.getApikey()));
                    userSession.setPhone(String.valueOf(user.getPhone()));
                    userSession.setDob(String.valueOf(user.getDob()));
                    userSession.setCollect(String.valueOf(user.getCollect()));
                    userSession.setRedeem(String.valueOf(user.getRedeem()));
                    userSession.setPassword(String.valueOf(user.getPassword()));

                    session.storeUserSession(userSession);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            }
            else {
                MGUtilities.showAlertView(LoginActivity.this, R.string.login_error, status.getStatus_text());
            }
        }
    }


    public void syncTwitterUser(final AccessToken accessToken, final String screenName) {
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    LoginActivity.this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }
        task = new MGAsyncTask(LoginActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(LoginActivity.this, R.string.logging_in) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateLogin(response, _imageURL, _name);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                @SuppressWarnings("static-access")
                Twitter tw = TwitterApp.getTwitterInstance();
                twitter4j.User user = null;
                try {
                    user = tw.showUser(accessToken.getUserId());
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                if(user != null) {
                    String imageURL = user.getOriginalProfileImageURL();
                    params.add(new BasicNameValuePair("thumb_url", imageURL ));
                    Log.e("TWITTER IMAGE URL", imageURL);
                    _imageURL = imageURL;
                }

                _name = screenName;
                params.add(new BasicNameValuePair("twitter_id", String.valueOf(accessToken.getUserId()) ));
                params.add(new BasicNameValuePair("full_name", String.valueOf(screenName) ));
                params.add(new BasicNameValuePair("email", "" ));
                params.add(new BasicNameValuePair("api_key", Config.API_KEY ));
                response = DataParser.getJSONFromUrlWithPostRequest(Config.REGISTER_URL, params,getApplicationContext());
            }
        });
        task.execute();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void login() {


        String password = Html.toHtml(new SpannableString(txtPassword.getText().toString()) );
        String username = Html.toHtml(new SpannableString(txtUsername.getText().toString()) );
        password = MGUtilities.filterInvalidChars(password);
        username = MGUtilities.filterInvalidChars(username);

        final String password1 = password;
        final String username1 = username;

        if(username.isEmpty() || password.isEmpty()) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_field_username_passowrd);
            return;
        }

        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    LoginActivity.this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }
        task = new MGAsyncTask(LoginActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(LoginActivity.this, R.string.logging_in) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateLogin(response, _imageURL, _name);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", username1 ));
                params.add(new BasicNameValuePair("password", password1 ));
                response = DataParser.getJSONFromUrlWithPostRequest(Config.LOGIN_USER, params,getApplicationContext());
            }
        });
        task.execute();
    }

    // FACEBOOK
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()  {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Config.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    TwitterApp.TwitterAppListener twitterAppListener = new TwitterApp.TwitterAppListener() {

        @Override
        public void onError(String value)  {
            // TODO Auto-generated method stub
            Log.e("TWITTER ERROR**", value);
        }
        @Override
        public void onComplete(AccessToken accessToken) {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    syncTwitterUser(mTwitter.getAccessToken(), mTwitter.getScreenName());
                }
            });
        }
    };

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
    public void onDestroy()  {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    public void syncFacebookUser(final JSONObject object, final GraphResponse response) {

        Log.i("syncFacebookUser", response.toString());
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            String id = object.getString("id");
            String imageURL = "http://graph.facebook.com/" + id + "/picture?type=large";
            _imageURL = imageURL;
            try {
                URL profile_pic = new URL(imageURL);
                Log.i("profile_pic", profile_pic + "");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String name = object.getString("name");
            _name = name;

            String email = "";
            if(object.getString("email")!=null)
                email = object.getString("email");

            params.add(new BasicNameValuePair("facebook_id", id));
            params.add(new BasicNameValuePair("full_name", name));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("image", imageURL ));
            params.add(new BasicNameValuePair("email", email ));
            params.add(new BasicNameValuePair("gender",""));
            params.add(new BasicNameValuePair("dob",""));
            params.add(new BasicNameValuePair("phone",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        task = new MGAsyncTask(LoginActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;
            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(LoginActivity.this, R.string.logging_in) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateLogin(response, _imageURL, _name);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = DataParser.getJSONFromUrlWithPostRequest(Config.REGISTER_USER_FACEBOOK, params,getApplicationContext());
            }
        });
        task.execute();
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GOOGLEPLUS", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String email = acct.getEmail() != null ? acct.getEmail() : "";
            String thumbUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";

            final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("google_id", acct.getId()));
            params.add(new BasicNameValuePair("full_name", acct.getDisplayName()));
            params.add(new BasicNameValuePair("thumb_url", thumbUrl ));
            params.add(new BasicNameValuePair("email",  email));
            params.add(new BasicNameValuePair("api_key", Config.API_KEY ));

            task = new MGAsyncTask(LoginActivity.this);
            task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

                DataResponse response;

                @Override
                public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

                @Override
                public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                    asyncTask.dialog.setMessage(
                            MGUtilities.getStringFromResource(LoginActivity.this, R.string.logging_in) );
                }

                @Override
                public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    updateLogin(response, _imageURL, _name);
                }

                @Override
                public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    response = DataParser.getJSONFromUrlWithPostRequest(Config.REGISTER_URL, params,getApplicationContext());
                }
            });
            task.execute();
        }
    }

}
