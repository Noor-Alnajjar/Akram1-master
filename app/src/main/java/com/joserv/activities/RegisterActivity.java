package com.joserv.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.config.Config;
import com.config.UIConfig;
import com.joserv.Akram.MainActivity;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.bitmap.MGImageUtils;
import com.libraries.dataparser.DataParser;
import com.libraries.directories.Directory;
import com.libraries.imageview.RoundedImageView;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Status;
import com.models.User;
import com.joserv.Akram.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private MGAsyncTask task;

    EditText txtName, txtPassword, txtPasswordConfirm, txtEmail, txtUsername, txtdob, txtGender, txtPhone;
    RoundedImageView imgViewThumb;
    Spinner countryCode, gender;

    private String thumbUriStr = null;
    private Uri thumbUri = null;

    int REQUEST_CAMERA_PIC_THUMB = 881;
    int REQUEST_LOAD_PIC_THUMB = 882;

    String pfTime = "";
    private Directory dir;
    private String path = "";
    private String imagePath = "";
    private String txtGenderString = "";
    private String countryCodeString = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        path = Environment.getExternalStorageDirectory() + "/" + Config.SD_CARD_PATH;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = new Directory(path);
            if(!dir.isExist()){
                dir.createDir();
                dir.createSubDirCameraTaken();
                dir.createSubDirData();
            }
        }

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        imgViewThumb = (RoundedImageView) findViewById(R.id.imgViewThumb);
        imgViewThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>22){
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                }else {
                    getPictureThumb();
                }
            }
        });
        imgViewThumb.setImageResource(UIConfig.IMAGE_PLACEHOLDER_PROFILE_THUMB);
        imgViewThumb.setBorderWidth(UIConfig.BORDER_WIDTH);
        imgViewThumb.setBorderColor(getResources().getColor(R.color.colorAccent));

        txtName = (EditText) findViewById(R.id.txtName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPasswordConfirm = (EditText) findViewById(R.id.txtPasswordConfirm);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtdob = (EditText) findViewById(R.id.txtBirth);
        txtPhone = (EditText) findViewById(R.id.txtNumber);
        gender = (Spinner) findViewById(R.id.spinGender);
        countryCode = (Spinner) findViewById(R.id.spinPhone);


        SpannableString ss = new SpannableString(getResources().getString(R.string.by_registering_you_are_accepting_the_terms_and_condition));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                setTitle(R.string.app_name);
                Intent i = new Intent(RegisterActivity.this, TermsActivity.class);
                startActivity(i);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        if(Locale.getDefault().getLanguage() =="en" ) {
            ss.setSpan(clickableSpan, 36, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if (Locale.getDefault().getLanguage() =="ar") {
            ss.setSpan(clickableSpan, 19, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView textView = (TextView) findViewById(R.id.txtTermsAndCondistion);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Male");
        adapter.add("Female");
        adapter.add(getResources().getString(R.string.gender)); //This is the text that will be displayed as hint.


        gender.setAdapter(adapter);
        gender.setSelection(adapter.getCount()); //set the hint the default selection so it appears on launch.
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txtGenderString = adapterView.getItemAtPosition(i).toString();
                Log.e("Gender",txtGenderString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCountry.add("+961");
        adapterCountry.add("+962");
        adapterCountry.add("+971");
        adapterCountry.add("+"); //This is the text that will be displayed as hint.


        countryCode.setAdapter(adapterCountry);
        countryCode.setSelection(adapterCountry.getCount()); //set the hint the default selection so it appears on launch.
        countryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countryCodeString = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        txtdob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                final Calendar c = Calendar.getInstance();


                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtdob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());
                datePickerDialog.show();
                }
            }
        });
        txtdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();


                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtdob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.setCanceledOnTouchOutside(false);
                datePickerDialog.show();

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(RegisterActivity.this, "Permission denied to access your location.", Toast.LENGTH_SHORT).show();
                }else {
                    getPictureThumb();
                }
            }
        }
    }

    public void update() {
        if(TextUtils.isEmpty(txtUsername.getText())) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_field_username);
            return;
        }
        if(txtUsername.getText().toString().length()<4) {
            MGUtilities.showAlertView(this, R.string.wrong_fields_size, "The username must be more than 4 characters");
            return;
        }

        if(TextUtils.isEmpty(txtName.getText())) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_field_fullname);
            return;
        }
        if(TextUtils.isEmpty(txtEmail.getText()) || !isValidEmail(txtEmail.getText().toString())) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_field_email);
            return;
        }
        if(TextUtils.isEmpty(txtdob.getText())) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_dob);
            return;
        }
        if(!dobdateValidate(txtdob.getText().toString())) {
            MGUtilities.showAlertView(this, R.string.field_error, R.string.valid_dob);
            return;
        }
        if(txtGenderString.equals("") || txtGenderString.equals(getResources().getString(R.string.gender))) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_gender);
            return;
        }
        if(countryCode.getSelectedItem().toString().equals("+")) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_field_phone);
            return;
        }
        if(TextUtils.isEmpty(txtPhone.getText())) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_field_phone);
            return;
        }
        if(txtPhone.getText().toString().length()<8 || txtPhone.getText().toString().length() >12){
            MGUtilities.showAlertView(this, R.string.wrong_fields_size, R.string.phone_wrong_size);
            return;
        }
        if(TextUtils.isEmpty(txtPassword.getText()) || TextUtils.isEmpty(txtPasswordConfirm.getText())) {
            MGUtilities.showAlertView(this, R.string.empty_fields, R.string.empty_field_password);
            return;
        }
        if(txtPassword.getText().toString().length() < 8) {
            MGUtilities.showAlertView(this, R.string.field_error, R.string.field_error_password_8_chars);
            return;
        }
        if(txtPassword.getText().toString().compareTo(txtPasswordConfirm.getText().toString()) != 0 ) {
            MGUtilities.showAlertView(this, R.string.field_error, R.string.field_error_password);
            return;
        }
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(this, R.string.network_error, R.string.no_network_connection);
            return;
        }
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(this, R.string.network_error, R.string.no_network_connection);
            return;
        }
        task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {
            DataResponse response;
            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(MGUtilities.getStringFromResource(RegisterActivity.this, R.string.registering_user));
            }
            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateRegistration(response);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = syncDataRegistration();
            }
        });
        task.execute();
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
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public DataResponse syncDataRegistration() {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("full_name", txtName.getText().toString()));
        params.add(new BasicNameValuePair("name", txtUsername.getText().toString() ));
        params.add(new BasicNameValuePair("password", txtPassword.getText().toString() ));
        params.add(new BasicNameValuePair("email", txtEmail.getText().toString() ));
        params.add(new BasicNameValuePair("gender",txtGenderString));
        params.add(new BasicNameValuePair("dob",txtdob.getText().toString()));

        if(txtPhone.getText().toString().startsWith("0"))
            params.add(new BasicNameValuePair("phone", countryCodeString+txtPhone.getText().toString().substring(1) ));
        else
            params.add(new BasicNameValuePair("phone", countryCodeString+txtPhone.getText().toString()));

        Map<String, File> files = new HashMap<String, File>();
        if(thumbUriStr != null)
            files.put("image", new File(thumbUriStr));

        DataResponse response = DataParser.uploadFileWithParams(Config.REGESTER_USER, params, files,getApplicationContext());
        return response;
    }

    public void updateRegistration(DataResponse response) {

        if(response == null) {
            MGUtilities.showAlertView(RegisterActivity.this, R.string.login_error, R.string.problems_encountered_login);
            return;
        }
        Status status = response.getStatus();
        if(response != null && status != null) {
            if(status.getStatus_code() == 1 && response.getUser() != null) {
                User user = response.getUser();
                UserAccessSession session = UserAccessSession.getInstance(RegisterActivity.this);
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
                startActivity(intent);
                finishAffinity();
            }
            else {
                MGUtilities.showAlertView(RegisterActivity.this, R.string.network_error, status.getStatus_text());
            }
        }
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
        if (requestCode == REQUEST_CAMERA_PIC_THUMB && resultCode == RESULT_OK) {
            String newPath = String.format("%s%s", dir.getPFCameraTakenPath(), imagePath);
            File f = new File(newPath);
            thumbUri = Uri.fromFile(f);
            thumbUriStr = newPath;
            showThumb();
        }
        else if (requestCode == REQUEST_LOAD_PIC_THUMB && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            thumbUri = selectedImage;
            thumbUriStr = MGImageUtils.getRealPathFromURI(selectedImage, this);
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

    @Override
    public void onDestroy()  {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    private void captureCameraThumb() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pfTime = String.valueOf (System.currentTimeMillis());
        imagePath = String.format("%s.jpg", pfTime);
        //Grant permission to the camera activity to write the photo.
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //saving if there is EXTRA_OUTPUT
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File (dir.getPFCameraTakenPath(), imagePath)));
        startActivityForResult(cameraIntent, REQUEST_CAMERA_PIC_THUMB);
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
}
