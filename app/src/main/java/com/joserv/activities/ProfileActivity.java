package com.joserv.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.application.PokemonApplication;
import com.config.Config;
import com.config.UIConfig;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hbb20.CountryCodePicker;
import com.libraries.directories.Directory;
import com.libraries.imageview.RoundedImageView;
import com.libraries.twitter.TwitterApp;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.joserv.Akram.R;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import twitter4j.auth.AccessToken;

import static com.config.Config.UPDATE_PASSWORD;
import static com.config.Config.UPDATE_PROFILE;
import static com.libraries.bitmap.MGImageUtils.getRealPathFromURI;

public class ProfileActivity extends AppCompatActivity {

    EditText txtName, txtPassword, txtPasswordConfirm, txtUserName, txtEmail, txtNumber, txtDob;
    RoundedImageView imgViewThumb;
    private String thumbUriStr = null;
    private Uri thumbUri = null;
    int REQUEST_CAMERA_PIC_THUMB = 881;
    int REQUEST_LOAD_PIC_THUMB = 882;
    private Directory dir;
    private String path = "";
    private String imagePath = "";
    UserSession userSession;

    //akram 2.1
    Spinner countryCode, gender;
    private String countryCodeString = "";


    //akram 3.0
    TextView txtScore;
    CountryCodePicker ccp;
    private String txtGenderString = "";


    //change password
    Button btnchangePassword;
    android.support.v7.app.AlertDialog.Builder builder;
    android.support.v7.app.AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AndroidNetworking.initialize(getApplicationContext());

        userSession = UserAccessSession.getInstance(this).getUserSession();
        path = Environment.getExternalStorageDirectory() + "/" + Config.SD_CARD_PATH;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = new Directory(path);
            if (!dir.isExist()) {
                dir.createDir();
                dir.createSubDirCameraTaken();
                dir.createSubDirData();
            }
        }
        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        btnchangePassword = (Button) findViewById(R.id.btnChangePass);
        btnchangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.dialog_change_password);

                final EditText currentPassword = (EditText) dialog.findViewById(R.id.txtCurrentPassword);
                final android.support.design.widget.TextInputLayout txtCurrentPasswordLayout = (android.support.design.widget.TextInputLayout) dialog.findViewById(R.id.txtCurrentPasswordLayout);
                final EditText newPassword = (EditText) dialog.findViewById(R.id.txtNewPassword);
                final android.support.design.widget.TextInputLayout txtNewPasswordLayout = (android.support.design.widget.TextInputLayout) dialog.findViewById(R.id.txtNewPasswordLayout);
                final EditText repeatPasswrod = (EditText) dialog.findViewById(R.id.txtConfirmPassword);
                final android.support.design.widget.TextInputLayout txtConfirmPasswordLayout = (android.support.design.widget.TextInputLayout) dialog.findViewById(R.id.txtConfirmPasswordLayout);
                final Button btnChangePasswordDialog = (Button) dialog.findViewById(R.id.btnchangePasswordDialog) ;
                Button btncancelDialog = (Button) dialog.findViewById(R.id.btncancelDialog);
                btncancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (currentPassword.getText().length() != 0 && newPassword.getText().length() != 0 && repeatPasswrod.getText().length() != 0){
                    btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorPrimary));
                    btnChangePasswordDialog.setEnabled(true);
                } else {
                    btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorGreenLight));
                    btnChangePasswordDialog.setEnabled(false);
                }

                currentPassword.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                        if (currentPassword.getText().toString().length() > 0 && newPassword.getText().toString().length() > 0 && repeatPasswrod.getText().toString().length() > 0){
                            btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorPrimary));
                            btnChangePasswordDialog.setEnabled(true);
                        } else {
                            btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorGreenLight));
                            btnChangePasswordDialog.setEnabled(false);
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });
                newPassword.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {

                        if (currentPassword.getText().toString().length() > 0 && newPassword.getText().toString().length() > 0 && repeatPasswrod.getText().toString().length() > 0){
                            btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorPrimary));
                            btnChangePasswordDialog.setEnabled(true);
                        } else {
                            btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorGreenLight));
                            btnChangePasswordDialog.setEnabled(false);
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });
                repeatPasswrod.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                        if (currentPassword.getText().toString().length() > 0 && newPassword.getText().toString().length() > 0 && repeatPasswrod.getText().toString().length() > 0){
                            btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorPrimary));
                            btnChangePasswordDialog.setEnabled(true);
                        } else {
                            btnChangePasswordDialog.setTextColor(ProfileActivity.this.getResources().getColor(R.color.colorGreenLight));
                            btnChangePasswordDialog.setEnabled(false);
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });

                btnChangePasswordDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(currentPassword.getText())){
//                    currentPassword.setError(getResources().getString(R.string.field_empty_error));
//                    currentPassword.requestFocus();
                            txtCurrentPasswordLayout.setError(getResources().getString(R.string.field_empty_error));
                            return;
                        }
                        if(TextUtils.isEmpty(newPassword.getText())){
//                    newPassword.setError(getResources().getString(R.string.field_empty_error));
//                    newPassword.requestFocus();
                            txtNewPasswordLayout.setError(getResources().getString(R.string.field_empty_error));
                            return;
                        }
                        if(TextUtils.isEmpty(repeatPasswrod.getText())){
//                    repeatPasswrod.setError(getResources().getString(R.string.field_empty_error));
//                    repeatPasswrod.requestFocus();
                            txtConfirmPasswordLayout.setError(getResources().getString(R.string.field_empty_error));
                            return;
                        }
                        if(!currentPassword.getText().toString().equals(userSession.getPassword())){
//                    currentPassword.setError(getResources().getString(R.string.wrong_password));
//                    currentPassword.requestFocus();
                            txtCurrentPasswordLayout.setError(getResources().getString(R.string.wrong_password));
                            return;
                        }
                        if(!newPassword.getText().toString().equals(repeatPasswrod.getText().toString())){
//                    repeatPasswrod.setError(getResources().getString(R.string.password_error_details));
//                    repeatPasswrod.requestFocus();
                            txtConfirmPasswordLayout.setError(getResources().getString(R.string.field_empty_error));
                            return;
                        }
                        try {
                            updatePassword(currentPassword.getText().toString(), repeatPasswrod.getText().toString());
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                        }

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        imgViewThumb = (RoundedImageView) findViewById(R.id.imgViewThumb);
        imgViewThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 22) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPictureThumb();
                }
            }
        });

        txtName = (EditText) findViewById(R.id.txtName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPasswordConfirm = (EditText) findViewById(R.id.txtPasswordConfirm);
        txtUserName = (EditText) findViewById(R.id.txtUsername);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtNumber = (EditText) findViewById(R.id.txtNumber);
        txtDob = (EditText) findViewById(R.id.txtBirth);
        txtScore = (TextView) findViewById(R.id.txtScoreProfile);
        countryCode = (Spinner) findViewById(R.id.spinPhone);
        gender = (Spinner) findViewById(R.id.spinGender);

        txtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());
                datePickerDialog.show();
            }
        });
        //getData();
        updateView();
        hideSoftKeyboard();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(ProfileActivity.this, "Permission denied to access your location.", Toast.LENGTH_SHORT).show();
                } else {
                    getPictureThumb();
                }
            }
        }
    }
    public void updateView() {
        if (userSession.getFull_name() != null && !userSession.getFull_name().equals("None")) {
            txtName.setText(Html.fromHtml(userSession.getFull_name()));
        }
        if (userSession.getName() != null && !userSession.getName().equals("None")) {
            txtUserName.setText(Html.fromHtml(userSession.getName()));
        }
        if (userSession.getEmail() != null && !userSession.getEmail().equals("None")) {
            txtEmail.setText(Html.fromHtml(userSession.getEmail()));
        }
        if (userSession.getPhone() != null && !userSession.getPhone().equals("None")) {
            ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
                        ((TextView) v.findViewById(android.R.id.text1)).setText("");
                        ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }
                    return v;
                }
                @Override
                public int getCount() {
                    return super.getCount() - 1; // you dont display last item. It is used as hint.
                }
            };
            adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterCountry.add("+961");
            adapterCountry.add("+962");
            adapterCountry.add("+971");
            //check if the phone code is valid if not it fixes it
            if (!userSession.getPhone().startsWith("+")) {
                txtNumber.setText(userSession.getPhone());
                adapterCountry.add("+"); //This is the text that will be displayed as hint.
                countryCode.setAdapter(adapterCountry);
                countryCode.setSelection(adapterCountry.getCount()); //set the hint the default selection so it appears on launch.
                countryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        countryCodeString = adapterView.getItemAtPosition(i).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            } else {
                txtNumber.setText(userSession.getPhone().substring(4));
                countryCodeString = userSession.getPhone().substring(4);
                adapterCountry.add(userSession.getPhone().substring(0, 4)); //This is the text that will be displayed as hint.
                countryCode.setAdapter(adapterCountry);
                countryCode.setSelection(adapterCountry.getCount()); //set the hint the default selection so it appears on launch.
                countryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        countryCodeString = adapterView.getItemAtPosition(i).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            }
        }
        if (userSession.getDob() != null && !userSession.getDob().equals("None")) {
            txtDob.setText(Html.fromHtml(userSession.getDob()));
        }
        if (userSession.getName().equals("")) {
            txtUserName.setVisibility(View.GONE);
        }
        if (!userSession.getScore().equals("") || userSession.getScore() == null) {
            txtScore.setText(userSession.getScore());
            CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.cpb_points_profile);
            int animationDuration = 2500; // 2500ms = 2,5s
            circularProgressBar.setProgressWithAnimation(Integer.valueOf(userSession.getScore()), animationDuration);
            //circularProgressBar.setProgress(Integer.valueOf(userSession.getScore()));
        }
        if (userSession.getGender() == null || userSession.getGender().equals("")) {
            gender.setEnabled(true);
            intSpinnerGender(getResources().getString(R.string.gender));
        } else {
            gender.setEnabled(false);
            intSpinnerGender(userSession.getGender());
        }
        if (userSession.getDob().equals("")) {
            txtDob.setEnabled(true);
        }
        if (userSession.getPassword() != null && userSession.getPassword().equals(""))
            userSession.setPassword("empty");

        imgViewThumb.setImageResource(UIConfig.IMAGE_PLACEHOLDER_PROFILE_THUMB);
        imgViewThumb.setBorderWidth(UIConfig.BORDER_WIDTH);
        imgViewThumb.setBorderColor(getResources().getColor(R.color.colorAccent));

        if (userSession.getImage() != null) {
            PokemonApplication.getImageLoaderInstance(this).displayImage(userSession.getImage(), imgViewThumb,
                    PokemonApplication.getDisplayImageOptionsThumbInstance());
        }
    }

    public static boolean dobdateValidate(String date) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        try {
            Date parseddate = sdf.parse(date);
            Calendar c2 = Calendar.getInstance();
            c2.add(Calendar.YEAR, -10);
            Date dateObj2 = new Date(System.currentTimeMillis());
            if (parseddate.before(c2.getTime())) {
                result = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void intSpinnerGender(String firstString) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileActivity.this, R.layout.my_spinner_style) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }};
        adapter.setDropDownViewResource(R.layout.my_spinner_style);
        adapter.add("Male");
        adapter.add("Female");
        adapter.add(firstString);//This is the text that will be displayed as hint.

        gender.setAdapter(adapter);
        gender.setSelection(adapter.getCount()); //set the hint the default selection so it appears on launch.
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txtGenderString = adapterView.getItemAtPosition(i).toString();
                Log.e("Gender", txtGenderString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void update() {
        if (TextUtils.isEmpty(txtName.getText())) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_field_fullname);
            return;
        }
        if (TextUtils.isEmpty(txtEmail.getText()) || !isValidEmail(txtEmail.getText().toString())) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_field_email);
            return;
        }
        if (TextUtils.isEmpty(txtDob.getText())) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_dob);
            return;
        }
        if (!dobdateValidate(txtDob.getText().toString())) {
            MGUtilities.showAlertView(
                    this,
                    R.string.field_error,
                    R.string.valid_dob);
            return;
        }
        if (gender.getSelectedItem().toString().equals(getResources().getString(R.string.gender))) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_gender);
            return;
        }
        if (countryCode.getSelectedItem().toString().equals("+")) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_field_phone);
            return;
        }
        if (TextUtils.isEmpty(txtNumber.getText())) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_field_phone);
            return;
        }
        if (txtNumber.getText().toString().length() < 8 || txtNumber.getText().toString().length() > 12) {
            MGUtilities.showAlertView(
                    this,
                    R.string.wrong_fields_size,
                    R.string.phone_wrong_size);
            return;
        }
        if (!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }
        try {
            updateProfile(userSession.getPassword());
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_PIC_THUMB && resultCode == RESULT_OK) {
            String newPath = String.format("%s%s", dir.getPFCameraTakenPath(), imagePath);
            File f = new File(newPath);
            thumbUri = Uri.fromFile(f);
            thumbUriStr = newPath;
            showThumb();
        } else if (requestCode == REQUEST_LOAD_PIC_THUMB && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            thumbUri = selectedImage;
            thumbUriStr = getRealPathFromURI(selectedImage, ProfileActivity.this);
            showThumb();
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

    private void getPictureThumb() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_LOAD_PIC_THUMB);
    }

    private void showThumb() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), thumbUri);
            imgViewThumb.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void updateProfile(String password) {

        String s_phone = "";
        if (txtNumber.getText().toString().startsWith("0"))
            s_phone = countryCodeString + txtNumber.getText().toString().substring(1);
        else
            s_phone = countryCodeString + txtNumber.getText().toString();


        Map<String, File> files = new HashMap<String, File>();
        if (thumbUriStr != null)
            files.put("image", new File(thumbUriStr));

        String s_ApiKey = "";
        if (userSession != null)
            if (userSession.getId() != null)
                s_ApiKey = userSession.getApikey();

        Map<String, String> params = new HashMap<String, String>();
        params.put("x-api-key", s_ApiKey);
        AndroidNetworking.upload(UPDATE_PROFILE)
                .addMultipartParameter("user_id", String.valueOf(userSession.getId()))
                .addMultipartParameter("name", txtUserName.getText().toString())
                .addMultipartParameter("password", password)
                .addMultipartParameter("full_name", txtName.getText().toString())
                .addMultipartParameter("email", txtEmail.getText().toString())
                .addMultipartParameter("gender", gender.getSelectedItem().toString())
                .addMultipartParameter("dob", txtDob.getText().toString())
                .addMultipartParameter("phone", s_phone.toString())
                .addMultipartFile(files)
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

                                JSONObject value = response.getJSONObject("user");
                                String id = value.getString("id");
                                String name = value.getString("name");
                                String email = value.getString("email");
                                String password = value.getString("password");
                                String full_name = value.getString("full_name");
                                String gender = value.getString("gender");
                                String image = value.getString("image");
                                String score = value.getString("score");
                                String apikey = value.getString("apikey");
                                String facebook_profile = value.getString("facebook_profile");
                                String insta_profile = "";
                                String phone = value.getString("phone");
                                String collect = value.getString("collect");
                                String redeem = value.getString("redeem");
                                String dob = value.getString("dob");

                                UserAccessSession session = UserAccessSession.getInstance(ProfileActivity.this);
                                UserSession userSession = new UserSession();
                                userSession.setId(id);
                                userSession.setName(name);
                                userSession.setFull_name(full_name);
                                userSession.setEmail(email);
                                userSession.setGender(gender);
                                userSession.setImage(image);
                                userSession.setScore(score);
                                userSession.setFacebook_profile(facebook_profile);
                                userSession.setInsta_profile(insta_profile);
                                userSession.setApikey(apikey);
                                userSession.setPhone(phone);
                                userSession.setDob(dob);
                                userSession.setCollect(collect);
                                userSession.setRedeem(redeem);
                                userSession.setPassword(password);

                                session.storeUserSession(userSession);
                                String status_text = status.getString("status_text");


                                final Dialog dialog = new Dialog(ProfileActivity.this);
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
                                        finish();
                                    }
                                });
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Snackbar snackbar = Snackbar.make(imgViewThumb, "Error occurred", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }

    public void updatePassword(final String oldpassword, final String newpassword) {

        String s_ApiKey = "";
        if (userSession != null)
            if (userSession.getId() != null)
                s_ApiKey = userSession.getApikey();

        Map<String, String> params = new HashMap<String, String>();
        params.put("x-api-key", s_ApiKey);
        AndroidNetworking.post(UPDATE_PASSWORD)
                .addBodyParameter("user_id", String.valueOf(userSession.getId()))
                .addBodyParameter("oldpassword", oldpassword)
                .addBodyParameter("newpassword", newpassword)
                .addHeaders(params)
                .setContentType("multipart/form-data; charset=utf-8")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("newpassword", "11");
                            JSONObject status = response.getJSONObject("status");
                            Log.e("newpassword", "1");
                            if (status.getString("status_code").equals("1")) {

                                Log.e("newpassword", "2");

                                String id = userSession.getId();
                                String name = userSession.getName();
                                String email = userSession.getEmail();
                                String password = userSession.getPassword();
                                String full_name = userSession.getFull_name();
                                String gender = userSession.getGender();
                                String image = userSession.getImage();
                                String score = userSession.getScore();
                                String apikey = userSession.getApikey();
                                String facebook_profile = userSession.getFacebook_profile();
                                String insta_profile = "";
                                String phone = userSession.getPhone();
                                String collect = userSession.getCollect();
                                String redeem = userSession.getRedeem();
                                String dob = userSession.getDob();

                                UserAccessSession session = UserAccessSession.getInstance(ProfileActivity.this);
                                UserSession userSession = new UserSession();
                                userSession.setId(id);
                                userSession.setName(name);
                                userSession.setFull_name(full_name);
                                userSession.setEmail(email);
                                userSession.setGender(gender);
                                userSession.setImage(image);
                                userSession.setScore(score);
                                userSession.setFacebook_profile(facebook_profile);
                                userSession.setInsta_profile(insta_profile);
                                userSession.setApikey(apikey);
                                userSession.setPhone(phone);
                                userSession.setDob(dob);
                                userSession.setCollect(collect);
                                userSession.setRedeem(redeem);
                                userSession.setPassword(newpassword);

                                session.storeUserSession(userSession);

                                Log.e("newpassword", "3");
                                String status_text = status.getString("status_text");


                                final Dialog dialog = new Dialog(ProfileActivity.this);
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
                                        finish();
                                    }
                                });
                                dialog.show();
                                Log.e("newpassword", "3");
                            } else if (status.getString("status_code").equals("0")) {

                                final Dialog dialog = new Dialog(ProfileActivity.this);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.setCancelable(true);
                                dialog.setContentView(R.layout.custom_dialog);

                                final TextView txtmsg = (TextView) dialog.findViewById(R.id.txtmsg);
                                Button btnokDialog = (Button) dialog.findViewById(R.id.btnokDialog);
                                Button btncancelDialog = (Button) dialog.findViewById(R.id.btncancelDialog);
                                String status_text = status.getString("status_text");
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
                                        finish();
                                    }
                                });
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Enewpassword", e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Snackbar snackbar = Snackbar.make(imgViewThumb, "Error occurred", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
