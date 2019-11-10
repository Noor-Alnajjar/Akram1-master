package com.joserv.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.application.PokemonApplication;
import com.config.Config;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.utilities.MGUtilities;
import com.models.Entity;
import com.joserv.Akram.R;
import java.util.ArrayList;

public class TypeActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Entity> entites;
    private Queries q;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_list_swipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        q = PokemonApplication.getQueriesInstance(this);
        entites = new ArrayList<Entity>();

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
        showRefresh(false);
        beginFetching();
    }

    private void beginFetching() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, Config.DELAY_SHOW_ANIMATION);
    }

    private void getData() {
        showRefresh(true);
        entites.add(new Entity(Config.ENTITY_ID_GYM, MGUtilities.getStringFromResource(this, R.string.gym)));
        entites.add(new Entity(Config.ENTITY_ID_POKEMON, MGUtilities.getStringFromResource(this, R.string.pokemon)));
        entites.add(new Entity(Config.ENTITY_ID_POKESTOP, MGUtilities.getStringFromResource(this, R.string.pokestop)));
        showList();
        showRefresh(false);
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
        MGRecyclerAdapter adapter = new MGRecyclerAdapter(entites.size(), R.layout.view_sightings_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final Entity entity = entites.get(position);
                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                tvTitle.setText(Html.fromHtml(entity.getEntity_name()));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode && requestCode == Config.REQUEST_CODE_ADD_GYM) {
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        if(RESULT_OK == resultCode && requestCode == Config.REQUEST_CODE_ADD_POKEMON) {
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        if(RESULT_OK == resultCode && requestCode == Config.REQUEST_CODE_ADD_POKESTOP) {
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();
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
    }
}
