package com.id11303765.commute.view.journey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.id11303765.commute.R;
import com.id11303765.commute.model.Journey;

public class JourneyRouteActivity extends AppCompatActivity {
    private Journey mJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpScreen();
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
        setContentView(R.layout.activity_journey_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_journey_route_toolbar);
        setSupportActionBar(toolbar);

        //SetUp Top Info

        //Set Up RecyclerView all rows in loop
        // if first, add the Start box and make the line above it gone.
        // if last, add the End box and make the line below it gone.


    }

}
