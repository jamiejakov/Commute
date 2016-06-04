package com.id11303765.commute.view.journey;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JourneyTimeSelectionActivity extends AppCompatActivity implements View.OnClickListener {
    private TabLayout mTabLayout;
    private Button mDateButton;
    private ImageView mResetTime;
    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_time_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getString(R.string.time_options));

        mTabLayout = (TabLayout) findViewById(R.id.activity_journey_time_selection_tab_layout);
        TabLayout.Tab departAtTab = mTabLayout.newTab().setText(R.string.depart_at);
        mTabLayout.addTab(departAtTab);
        TabLayout.Tab arriveByTab = mTabLayout.newTab().setText(R.string.arrive_by);
        mTabLayout.addTab(arriveByTab);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mDateButton = (Button) findViewById(R.id.activity_journey_time_selection_date_button);
        mDateButton.setOnClickListener(this);

        mResetTime = (ImageView) findViewById(R.id.activity_journey_time_selection_reset_time);
        mResetTime.setOnClickListener(this);

        mTimePicker = (TimePicker) findViewById(R.id.activity_journey_time_selection_time_picker);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_journey_time_selection_date_button:
                showDatePicker();
                break;
            case R.id.activity_journey_time_selection_reset_time:
                resetPickers();
                break;
        }
    }

    private void showDatePicker() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("EE, dd MMM yyyy", Locale.US);
        Date selected;
        Calendar newCalendar = Calendar.getInstance();
        try {
            if (!mDateButton.getText().toString().equals(getString(R.string.today))) {
                selected = dateFormatter.parse(mDateButton.getText().toString());
                newCalendar.setTime(selected);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        DatePickerDialog toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (DateUtils.isToday(newDate.getTimeInMillis())) {
                    mDateButton.setText(getString(R.string.today));
                } else {
                    mDateButton.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog.show();
    }

    private void resetPickers() {
        TimePicker timepicker = (TimePicker) findViewById(R.id.activity_journey_time_selection_time_picker);
        mDateButton.setText(getString(R.string.today));

        Calendar cal = Calendar.getInstance();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timepicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timepicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        } else {
            timepicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
            timepicker.setMinute(cal.get(Calendar.MINUTE));
        }
    }

}
