package com.id11303765.commute.view;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
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
import com.id11303765.commute.utils.DividerItemDecoration;

import java.util.ArrayList;

public class StopSearchActivity extends AppCompatActivity {

    private RecyclerView mStopRecyclerView;
    private StopSearchAdapter mStopSearchAdapter;
    private ArrayList<Stop> mStopList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_search);
        setTitle(R.string.search_hint);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mStopList = new ArrayList<>();
        mStopRecyclerView = (RecyclerView) findViewById(R.id.activity_stop_search_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mStopRecyclerView.setLayoutManager(layoutManager);
        mStopSearchAdapter = new StopSearchAdapter(this, mStopList);
        mStopRecyclerView.setAdapter(mStopSearchAdapter);
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
                Log.d(Constants.SEARCHTAG, "mStopList Before:  " + mStopList.size());
                mStopList.clear();
                mStopList.addAll(StopManager.getStopsByName(s));
                mStopSearchAdapter.notifyDataSetChanged();
                return false;
            }

        });


        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
