package com.joserv.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.config.Config;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.asynctask.MGAsyncTask.OnMGAsyncTaskListener;
import com.libraries.dataparser.DataParser;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Sighting;
import com.joserv.Akram.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

	private DataResponse response;
	private MGAsyncTask task;
	private Sighting sighting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.view_activity_report);
		setTitle(R.string.flag_sighting);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sighting = (Sighting) this.getIntent().getSerializableExtra("sighting");

		final EditText txtReview = (EditText) findViewById(R.id.txtReview);
		final TextView tvMaxCharCount = (TextView) findViewById(R.id.tvMaxCharCount);
		String charsLeft = String.format("%d %s",
				Config.MAX_CHARS_FLAGGED,
				MGUtilities.getStringFromResource(ReportActivity.this, R.string.chars_left));

		tvMaxCharCount.setText(charsLeft);
		InputFilter filter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
									   Spanned dest, int dstart, int dend) {
				if (source.length() >= Config.MAX_CHARS_FLAGGED)
					return "";

				String charsLeft = String.format("%d %s",
						Config.MAX_CHARS_FLAGGED - txtReview.getText().toString().length(),
						MGUtilities.getStringFromResource(ReportActivity.this, R.string.chars_left));
				tvMaxCharCount.setText(charsLeft);
				return source;
			}
		};
		txtReview.setFilters(new InputFilter[]{filter});
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
	        case R.id.menuSend:
	        	report();
	            return true;
	        default:
	        	finish();	
	            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

	public void report() {
		if(!MGUtilities.hasConnection(this)) {
			MGUtilities.showAlertView(
					this,
					R.string.network_error, 
					R.string.no_network_connection);
			return;
		}
		
		EditText txtReview = (EditText) findViewById(R.id.txtReview);
		String reviewStr = txtReview.getText().toString().trim();
		if(reviewStr.length() == 0) {
			MGUtilities.showAlertView(
					this, 
					R.string.empty_error, 
					R.string.empty_error_flag_details);
			return;
		}

		if(task != null)
			task.cancel(true);

        task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new OnMGAsyncTaskListener() {
			
			@Override
			public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }
			
			@Override
			public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
				asyncTask.dialog.setMessage(MGUtilities.getStringFromResource(
						ReportActivity.this, R.string.reporting));
			}
			
			@Override
			public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub
				reloadData();
			}
			
			@Override
			public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub
				sync();
			}
		});
        task.execute();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void sync() {
		EditText txtReview = (EditText) findViewById(R.id.txtReview);
		String reviewStr = txtReview.getText().toString().trim();
		UserAccessSession userAccess = UserAccessSession.getInstance(this);
		UserSession userSession = userAccess.getUserSession();
		try {
			SpannedString span = new SpannedString(reviewStr);
			String reviewString = Html.escapeHtml(span);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("review", reviewString ));
			params.add(new BasicNameValuePair("sighting_id", String.valueOf(sighting.getSighting_id()) ));
			params.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getId()) ));
			params.add(new BasicNameValuePair("api_key", Config.API_KEY ));
	        response = DataParser.getJSONFromUrlWithPostRequest(Config.FLAG_URL, params,getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reloadData() {
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	@Override
    public void onDestroy()  {
        super.onDestroy();
        if(task != null)
        	task.cancel(true);
	}
}
