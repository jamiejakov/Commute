package com.id11303765.commute.view;

import android.app.FragmentManager;
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
import com.id11303765.commute.model.FareManager;
import com.id11303765.commute.model.JourneyManager;
import com.id11303765.commute.model.RouteManager;
import com.id11303765.commute.model.StopManager;
import com.id11303765.commute.model.TimetableManager;
import com.id11303765.commute.model.TripManager;
import com.id11303765.commute.view.journey.JourneyFragment;

/**
 * Main activity of the application
 * Handles the drawer and created the database helper
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpScreen();
        setUpPojoManagers();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager frag = getFragmentManager();

        if (id == R.id.nav_journey) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new JourneyFragment()).commit();
        } else if (id == R.id.nav_commute) {
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new CommuteFragment()).commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Chooses which fragment to display when opening the application based on the shared preferences.
     */
    private void selectLaunchScreen() {
        FragmentManager frag = getFragmentManager();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String launchScreenPref = sharedPreferences.getString(getString(R.string.key_launch_screen_preference), "0");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (launchScreenPref) {
            case "0":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new WelcomeFragment()).commit();
                editor.putString(getString(R.string.key_launch_screen_preference), "1");
                editor.apply();
                break;
            case "1":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new JourneyFragment()).commit();
                break;
            case "2":
                frag.beginTransaction().replace(R.id.activity_main_content_frame, new CommuteFragment()).commit();
                break;
        }
    }

    /**
     * Setup for all UI elements for the Activity
     */
    private void setUpScreen() {
        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
        setSupportActionBar(toolbar);
        //Setup drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        Menu navMenu = navigationView.getMenu();
        final MenuItem item = navMenu.findItem(R.id.nav_commute);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!item.isEnabled()) {
                    item.setEnabled(isCommuteEnabled());
                }
            }
        };
        assert mDrawer != null;
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Function checks whether the Commute fragemnt is enabled based on shared preferences
     * @return whether the Commute fragment is enabled
     */
    private boolean isCommuteEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String homeStop = sharedPreferences.getString(getString(R.string.home_preference), getString(R.string.default_preference_result));
        String workStop = sharedPreferences.getString(getString(R.string.work_preference), getString(R.string.default_preference_result));
        return !homeStop.equals(getString(R.string.default_preference_result)) && !workStop.equals(getString(R.string.default_preference_result));
    }

    /**
     * Sets up all the POJO manager singletons and gives them access to the database;
     */
    private void setUpPojoManagers() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        AgencyManager.setDatabaseHelper(dbHelper);
        RouteManager.setDatabaseHelper(dbHelper);
        RouteManager.setLineColors(getResources().getStringArray(R.array.lineColors));
        TripManager.setDatabaseHelper(dbHelper);
        StopManager.setDatabaseHelper(dbHelper);
        TimetableManager.setDatabaseHelper(dbHelper);
        CommuteManager.setDatabaseHelper(dbHelper);
        JourneyManager.setDatabaseHelper(dbHelper);
        FareManager.setDatabaseHelper(dbHelper);
    }

}
