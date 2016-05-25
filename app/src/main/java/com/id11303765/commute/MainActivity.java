package com.id11303765.commute;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert mDrawer != null;
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager frag = getFragmentManager();
        frag.beginTransaction().replace(R.id.activity_main_content_frame, new WelcomeFragment()).commit();
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

    @SuppressWarnings("StatementWithEmptyBody")
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
            //startActivity(new Intent(this,SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this,AboutActivity.class));
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
