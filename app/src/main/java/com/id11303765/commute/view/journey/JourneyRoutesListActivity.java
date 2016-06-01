package com.id11303765.commute.view.journey;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.id11303765.commute.R;
import com.id11303765.commute.controller.JourneyRoutesListAdapter;
import com.id11303765.commute.controller.StopSearchAdapter;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyManager;
import com.id11303765.commute.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class JourneyRoutesListActivity extends AppCompatActivity {
    private ArrayList<Journey> mJourneys;
    private JourneyRoutesListAdapter mJourneyRoutesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJourneys = new ArrayList<>();

        setContentView(R.layout.activity_journey_routes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("TO TEMP LOCATION");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.app_bar_journey_route_list_tab_layout);
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText(R.string.speed));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.price));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.convenience));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        RecyclerView stopRecyclerView = (RecyclerView) findViewById(R.id.activity_journey_routes_list_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        assert stopRecyclerView != null;
        stopRecyclerView.setLayoutManager(layoutManager);
        mJourneyRoutesListAdapter = new JourneyRoutesListAdapter(this,this, mJourneys);
        stopRecyclerView.setAdapter(mJourneyRoutesListAdapter);

        setUpData();
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

    private void setUpData(){
        String startStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_START_STOP);
        String endStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_END_STOP);
        Calendar now = Calendar.getInstance();
        now.set(1970, 0, 1);
        mJourneys.add(JourneyManager.getJoureney(startStopShortName, endStopShortName, now.getTime()));

    }

}
