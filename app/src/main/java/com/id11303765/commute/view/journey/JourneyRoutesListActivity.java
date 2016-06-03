package com.id11303765.commute.view.journey;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.id11303765.commute.R;
import com.id11303765.commute.controller.JourneyRoutesListAdapter;
import com.id11303765.commute.model.CommuteManager;
import com.id11303765.commute.model.Journey;
import com.id11303765.commute.model.JourneyManager;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.CommuteFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class JourneyRoutesListActivity extends AppCompatActivity {
    private ArrayList<Journey> mJourneys;
    private JourneyRoutesListAdapter mJourneyRoutesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_routes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mJourneys = new ArrayList<>();


        setUpScreen();
        setUpData();
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

    private void setUpScreen(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.app_bar_journey_route_list_tab_layout);
        assert tabLayout != null;
        TabLayout.Tab speedTab = tabLayout.newTab().setText(R.string.speed);
        speedTab.setIcon(R.drawable.ic_directions_run_black_24dp);
        speedTab.getIcon().setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        tabLayout.addTab(speedTab);
        TabLayout.Tab priceTab = tabLayout.newTab().setText(R.string.price);
        priceTab.setIcon(R.drawable.ic_attach_money_black_24dp);
        priceTab.getIcon().setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        priceTab.getIcon().setAlpha(Constants.DESELECTED);
        tabLayout.addTab(priceTab);
        TabLayout.Tab convenienceTab = tabLayout.newTab().setText(R.string.convenience);
        convenienceTab.setIcon(R.drawable.ic_event_seat_black_24dp);
        convenienceTab.getIcon().setColorFilter(ContextCompat.getColor(JourneyRoutesListActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        convenienceTab.getIcon().setAlpha(Constants.DESELECTED);
        tabLayout.addTab(convenienceTab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(Constants.OPAQUE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(Constants.DESELECTED);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setUpData(){
        mJourneys.clear();
        String startStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_START_STOP);
        String endStopShortName = getIntent().getStringExtra(Constants.INTENT_SEARCH_JOURNEY_END_STOP);
        setTitle("Routes To " + endStopShortName);
        ArrayList<String> stops = new ArrayList<>();
        stops.add(startStopShortName);
        stops.add(endStopShortName);

        JourneyRoutesListActivity.LoadJourneysAsync loadCommute = new JourneyRoutesListActivity.LoadJourneysAsync(stops, this);
        loadCommute.execute();

    }

    private void continueSetup() {
        RecyclerView stopRecyclerView = (RecyclerView) findViewById(R.id.activity_journey_routes_list_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        assert stopRecyclerView != null;
        stopRecyclerView.setLayoutManager(layoutManager);
        mJourneyRoutesListAdapter = new JourneyRoutesListAdapter(this, this, mJourneys);
        stopRecyclerView.setAdapter(mJourneyRoutesListAdapter);
        mJourneyRoutesListAdapter.notifyDataSetChanged();
    }

    /**
     * Loads the list of Timetables connecting the 2 stops for the daily commute.
     */
    private class LoadJourneysAsync extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mDialog;
        private ArrayList<String> mStops;
        private Context mContext;

        LoadJourneysAsync(ArrayList<String> stops, Context context) {
            mStops = stops;
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(mContext);
            mDialog.show();
            mDialog.setMessage(getString(R.string.loading_routes));
            mDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mJourneys.add(JourneyManager.getJourney(mStops, true, Common.getNow()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            continueSetup();
        }
    }

}
