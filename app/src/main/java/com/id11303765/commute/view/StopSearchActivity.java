package com.id11303765.commute.view;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.id11303765.commute.R;
import com.id11303765.commute.controller.StopSearchAdapter;
import com.id11303765.commute.model.Stop;
import com.id11303765.commute.model.StopManager;
import com.id11303765.commute.utils.Constants;

import java.util.ArrayList;

/**
 * Activity that allows the user to search through a list of stops and select the one they want.
 */
public class StopSearchActivity extends AppCompatActivity {
    private StopSearchAdapter mStopSearchAdapter;
    private ArrayList<Stop> mStopList;
    private ArrayList<String> mExcludedStopNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_search);
        setTitle(R.string.search_hint);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mExcludedStopNames = getIntent().getStringArrayListExtra(Constants.INTENT_SEARCH_EXCLUDE);
        mStopList = new ArrayList<>();

        //Setup RecyclerView
        RecyclerView stopRecyclerView = (RecyclerView) findViewById(R.id.activity_stop_search_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        assert stopRecyclerView != null;

        stopRecyclerView.setLayoutManager(layoutManager);
        int intentRequest = getIntent().getIntExtra(Constants.INTENT_REQUEST, 0);
        mStopSearchAdapter = new StopSearchAdapter(this, this, intentRequest, mStopList);
        stopRecyclerView.setAdapter(mStopSearchAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.activity_search_menu_search_item);
        searchMenuItem.expandActionView();
        SearchView search = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            /**
             * Search the list for matches while user is typing and display them
             * @param s - string to search
             * @return -
             */
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(Constants.TAG_STOP_SEARCH, s);
                mStopList.clear();
                ArrayList<Stop> list = StopManager.getStopsByName(s, false);
                for (Stop stop : list) {
                    if (containsStop(stop) == null) {
                        if (mExcludedStopNames == null || !excludedStop(stop)) {
                            mStopList.add(stop);
                        }
                    }
                }
                mStopSearchAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks whether the stopList contains the stop
     *
     * @param stopToCheck - stop to check
     * @return stop if it was found, null if not
     */
    private Stop containsStop(Stop stopToCheck) {
        for (Stop stop : mStopList) {
            if (stop.getShortName().toLowerCase().equals(stopToCheck.getShortName().toLowerCase()) &&
                    stop.getStopType() == stopToCheck.getStopType()) {
                return stop;
            }
        }
        return null;
    }

    /**
     * Check whether the stop is in the excluded list
     *
     * @param stop - stop to check
     * @return whether the stop should be excluded
     */
    private boolean excludedStop(Stop stop) {
        for (String s : mExcludedStopNames) {
            if (stop.getShortName().toLowerCase().contains(s.toLowerCase()) &&
                    !stop.getShortName().toLowerCase().contains(" " + s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
