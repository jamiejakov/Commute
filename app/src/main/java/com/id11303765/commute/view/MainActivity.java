package com.id11303765.commute.view;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.id11303765.commute.R;
import com.id11303765.commute.model.AgencyManager;
import com.id11303765.commute.model.CommuteManager;
import com.id11303765.commute.model.DatabaseHelper;
import com.id11303765.commute.model.RouteManager;
import com.id11303765.commute.model.StopManager;
import com.id11303765.commute.model.TimetableManager;
import com.id11303765.commute.model.TripManager;
import com.id11303765.commute.view.journey.JourneyFragment;
import com.id11303765.commute.view.timetables.TimetablesFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpDrawer();
        setUpDatabaseHelpers();
        selectLaunchScreen();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager frag = getFragmentManager();

        if (id == R.id.nav_journey) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new JourneyFragment()).commit();
        } else if (id == R.id.nav_commute) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new CommuteFragment()).commit();
        } else if (id == R.id.nav_timetable) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new TimetablesFragment()).commit();
        } else if (id == R.id.nav_saved) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new SavedRoutesFragment()).commit();
        } else if (id == R.id.nav_alerts) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new AlertsFragment()).commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        final MenuItem item = nav_Menu.findItem(R.id.nav_commute);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!item.isEnabled()){
                    item.setEnabled(isCommuteEnabled());
                }
            }
        };


        assert mDrawer != null;
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean isCommuteEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String homeStop = sharedPreferences.getString(getString(R.string.home_preference), "non");
        String workStop = sharedPreferences.getString(getString(R.string.work_preference), "non");
        return !homeStop.equals("non") && !workStop.equals("non");
    }


    private void setUpDatabaseHelpers() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        AgencyManager.setDatabaseHelper(dbHelper);
        RouteManager.setDatabaseHelperAndContext(dbHelper, this);
        TripManager.setDatabaseHelper(dbHelper);
        StopManager.setDatabaseHelper(dbHelper);
        TimetableManager.setDatabaseHelper(dbHelper);
        CommuteManager.setDatabaseHelper(dbHelper);
    }

    private void selectLaunchScreen() {
        FragmentManager frag = getFragmentManager();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String launchScreenPref = sharedPreferences.getString(getString(R.string.launch_screen_preference_key), "0");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (launchScreenPref) {
            case "0":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new WelcomeFragment()).commit();
                editor.putString(getString(R.string.launch_screen_preference_key), "1");
                editor.apply();
                break;
            case "1":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new JourneyFragment()).commit();
                break;
            case "2":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new CommuteFragment()).commit();
                break;
            case "3":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new TimetablesFragment()).commit();
                break;
            case "4":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new SavedRoutesFragment()).commit();
                break;
            case "5":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new AlertsFragment()).commit();
                break;
        }
    }

}
