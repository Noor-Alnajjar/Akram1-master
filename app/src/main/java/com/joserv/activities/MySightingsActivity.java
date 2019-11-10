package com.joserv.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.application.PokemonApplication;
import com.config.Config;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.location.MGLocationManagerUtils;
import com.libraries.usersession.UserAccessSession;
import com.libraries.utilities.MGUtilities;
import com.melnykov.fab.FloatingActionButton;
import com.models.DataResponse;

import com.models.Sighting;
import com.joserv.Akram.R;

import java.util.ArrayList;

/**
 * Created by mg on 19/07/16.
 */
public class MySightingsActivity extends AppCompatActivity implements PokemonApplication.OnLocationListener{

    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    FloatingActionButton fabAdd;

    private RecyclerView.LayoutManager mLayoutManager;
    private MGAsyncTaskNoDialog task;
    private ArrayList<Sighting> sightings;
    private Queries q;
    MGRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.fragment_list_swipe_my_sightings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        q = PokemonApplication.getQueriesInstance(this);
        sightings = new ArrayList<Sighting>();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddButton();
            }
        });
        showRefresh(false);
        parsing();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void parsing() {

        if(!MGUtilities.isLocationEnabled(this) && PokemonApplication.currentLocation == null) {
            MGLocationManagerUtils utils = new MGLocationManagerUtils();
            utils.setOnAlertListener(new MGLocationManagerUtils.OnAlertListener() {
                @Override
                public void onPositiveTapped() {
                    startActivityForResult(
                            new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            Config.PERMISSION_REQUEST_LOCATION_SETTINGS);
                }

                @Override
                public void onNegativeTapped() {
                    showRefresh(false);
                }
            });
            utils.showAlertView(
                    this,
                    R.string.location_error,
                    R.string.gps_not_on,
                    R.string.go_to_settings,
                    R.string.cancel);
        }
        else {
            showRefresh(true);
            if(PokemonApplication.currentLocation != null) {
                getData();
            }
            else {
                refetch();
            }
        }
    }

    public void refetch() {
        showRefresh(true);
        PokemonApplication app = (PokemonApplication) getApplication();
        app.setOnLocationListener(this, this);
    }

    @Override
    public void onLocationChanged(Location prevLoc, Location currentLoc) {
        PokemonApplication app = (PokemonApplication) getApplication();
        app.setOnLocationListener(null, this);
        beginFetching();
    }

    @Override
    public void onLocationRequestDenied() {
        showRefresh(false);
        MGUtilities.showAlertView(this, R.string.permission_error, R.string.permission_error_details_location);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.PERMISSION_REQUEST_LOCATION_SETTINGS) {
            if(MGUtilities.isLocationEnabled(this))
                refetch();
            else {
                showRefresh(false);
                Toast.makeText(this, R.string.location_error_not_turned_on, Toast.LENGTH_LONG).show();
            }
        }

        if(resultCode == Activity.RESULT_OK && requestCode == Config.REQUEST_CODE_ADD_SIGHTING) {
            sightings.clear();
            adapter.removeAll();
            mRecyclerView.getAdapter().notifyDataSetChanged();
            parsing();
        }
        if(resultCode == Activity.RESULT_OK && requestCode == Config.REQUEST_CODE_EDIT_SIGHTING) {
            sightings.clear();
            adapter.removeAll();
            mRecyclerView.getAdapter().notifyDataSetChanged();
            parsing();
        }
    }

    private void beginFetching() {

        sightings.clear();
        if(adapter != null) {
            adapter.removeAll();
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if(task != null)
            task.cancel(true);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, Config.DELAY_SHOW_ANIMATION);
    }

    private void getData() {

        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(this,  R.string.network_error, R.string.no_network_connection);
            return;
        }

        if(task != null)
            task.cancel(true);

        showRefresh(true);
        task = new MGAsyncTaskNoDialog(this);
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) {

            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                showList();
                if(sightings.size() == 0 ) {
                    Toast.makeText(MySightingsActivity.this, R.string.no_results_found, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(PokemonApplication.currentLocation != null && MGUtilities.hasConnection(MySightingsActivity.this)) {
                    try {
                        UserAccessSession accessSession = UserAccessSession.getInstance(MySightingsActivity.this);
                        String strUrl = String.format("%s?api_key=%s&user_id=%d",
                                Config.GET_MY_SIGHTINGS_URL,
                                Config.API_KEY,
                                accessSession.getUserSession().getId());

                        DataParser parser = new DataParser();
                        DataResponse data = parser.getData(strUrl,getApplicationContext());
                        if (data == null)
                            return;

                        if (data.getSightings() != null && data.getSightings().size() > 0) {
                            for (Sighting obj : data.getSightings()) {
                                q.deleteSighting(obj.getSighting_id());
                                q.insertSighting(obj);
//
//                                if(obj.getRating() != 0) {
//                                    q.deleteRating(obj.getRating().getRating_id());
//                                    q.insertRating(obj.getRating());
//                                }

                                if(obj.getUser() != null) {
                                    q.deleteUser(obj.getUser().getId());
                                    q.insertUser(obj.getUser());
                                }

                                sightings.add(obj);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        task.execute();
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    private void showList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)

        adapter = new MGRecyclerAdapter(sightings.size(), R.layout.view_sightings_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {

                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                final Sighting obj = sightings.get(position);
                String str = String.format("%s (%s)", obj.getName(), obj.getType());
                tvTitle.setText(Html.fromHtml(str));

                v.view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    }
                });

            }
        });

        mRecyclerView.setAdapter(adapter);
        showRefresh(false);
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

    public void onClickAddButton() {
        Intent i = new Intent(this, TypeActivity.class);
        startActivityForResult(i, Config.REQUEST_CODE_ADD_SIGHTING);
    }
}
