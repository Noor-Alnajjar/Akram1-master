package com.joserv.activities;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.PokemonApplication;
import com.config.Config;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Pokemon;
import com.joserv.Akram.R;

import java.util.ArrayList;


/**
 * Created by mg on 19/07/16.
 */
public class PokemonActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Pokemon> pokemons;
    private Queries q;
    private MGAsyncTaskNoDialog task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_list_swipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        q = PokemonApplication.getQueriesInstance(this);
        pokemons = new ArrayList<Pokemon>();

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
                if(pokemons.size() == 0 ) {
                    Toast.makeText(PokemonActivity.this, R.string.no_results_found, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(MGUtilities.hasConnection(PokemonActivity.this)) {
                    try {
                        String strUrl = String.format("%s?api_key=%s&get_pokemons=1",
                                Config.GET_SIGHTING_URL,
                                Config.API_KEY);

                        DataParser parser = new DataParser();
                        DataResponse data = parser.getData(strUrl,getApplicationContext());
                        if (data == null)
                            return;

                        if (data.getPokemons() != null && data.getPokemons().size() > 0) {
                            for (Pokemon obj : data.getPokemons()) {
                                q.deletePokemon(obj.getPokemon_id());
                                q.insertPokemon(obj);

                                pokemons.add(obj);
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
        MGRecyclerAdapter adapter = new MGRecyclerAdapter(pokemons.size(), R.layout.view_pokemon_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final Pokemon obj = pokemons.get(position);
                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                ImageView imgViewThumb = (ImageView) v.view.findViewById(R.id.imgViewThumb);
                tvTitle.setText(Html.fromHtml(obj.getName()));

                PokemonApplication.getImageLoaderInstance(PokemonActivity.this).
                        displayImage(
                                obj.getImage(),
                                imgViewThumb,
                                PokemonApplication.getDisplayImageOptionsInstance());

                v.view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        i.putExtra("pokemon", obj);
                        setResult(RESULT_OK, i);
                        finish();
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
}
