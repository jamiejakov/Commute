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

public class StopSearchActivity extends AppCompatActivity {

    private StopSearchAdapter mStopSearchAdapter;
    private ArrayList<Stop> mStopList;
    private String mExcludeSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_search);
        setTitle(R.string.search_hint);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        int intentRequest = getIntent().getIntExtra(Constants.INTENT_REQUEST, 0);
        mExcludeSearch = getIntent().getStringExtra(Constants.INTENT_SEARCH_EXCLUDE);
        mStopList = new ArrayList<>();
        RecyclerView stopRecyclerView = (RecyclerView) findViewById(R.id.activity_stop_search_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        stopRecyclerView.setLayoutManager(layoutManager);
        mStopSearchAdapter = new StopSearchAdapter(this,this, intentRequest, mStopList);
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
                Log.d(Constants.SEARCHTAG, "onQueryTextSubmit ");


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(Constants.SEARCHTAG, "onQueryTextChange ");
                mStopList.clear();
                ArrayList<Stop> list = StopManager.getStopsByName(s);
                for (Stop stop : list){
                    if (containsStop(stop) == null){
                        if (mExcludeSearch == null || !stop.getShortName().toLowerCase().equals(mExcludeSearch.toLowerCase())){
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

    private Stop containsStop(Stop stopToCheck){
        for (Stop stop: mStopList) {
            if (stop.getShortName().toLowerCase().equals(stopToCheck.getShortName().toLowerCase()) &&
                    stop.getStopType() == stopToCheck.getStopType()) {
                return stop;
            }
        }
        return null;
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
}
