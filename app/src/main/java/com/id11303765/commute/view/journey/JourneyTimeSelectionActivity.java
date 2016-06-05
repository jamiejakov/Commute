package com.id11303765.commute.view.journey;

import android.app.DatePickerDialog;
import android.content.Intent;
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
    private Button mLeaveNowButton;
    private Button mDoneButton;
    private Button mCancelButton;

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
        mLeaveNowButton = (Button) findViewById(R.id.activity_journey_time_selection_leave_now_button);
        mLeaveNowButton.setOnClickListener(this);
        mCancelButton = (Button) findViewById(R.id.activity_journey_time_selection_cancel_button);
        mCancelButton.setOnClickListener(this);
        mDoneButton = (Button) findViewById(R.id.activity_journey_time_selection_done_button);
        mDoneButton.setOnClickListener(this);

        mTimePicker = (TimePicker) findViewById(R.id.activity_journey_time_selection_time_picker);

        setTime();
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
            case R.id.activity_journey_time_selection_leave_now_button:
                leaveNow();
                break;
            case R.id.activity_journey_time_selection_done_button:
                setResult(Constants.JOURNEY_TIME_OPTIONS_TO_ACTIVITY_REQUEST, prepareIntent());
                finish();
                break;
            case R.id.activity_journey_time_selection_cancel_button:
                this.onBackPressed();
                break;
        }
    }

    private Intent prepareIntent(){
        Intent intent = new Intent();
        String tabString = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).getText().toString() + " ";
        String timeHour = String.valueOf(mTimePicker.getCurrentHour());
        String timeMinute = String.valueOf(mTimePicker.getCurrentMinute());
        boolean departAt = tabString.trim().equals(getString(R.string.depart_at));

        if (mTimePicker.getCurrentMinute() < 10) {
            timeMinute = "0" + mTimePicker.getCurrentMinute();
        }
        if (mTimePicker.getCurrentHour() >= 12) {
            if (mTimePicker.getCurrentHour() > 12) {
                timeHour = String.valueOf(mTimePicker.getCurrentHour() - 12);
            }
            timeMinute += "pm";
        } else {
            timeMinute += "am";
        }
        String timeString = timeHour + ":" + timeMinute + ", ";
        intent.putExtra(Constants.INTENT_TIME_OPTION, tabString + timeString + mDateButton.getText());
        intent.putExtra(Constants.INTENT_TIME_DEPART_AT_BOOL, departAt);
        return intent;
    }


    private void leaveNow(){
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_TIME_OPTION, getString(R.string.leave_now));
        intent.putExtra(Constants.INTENT_TIME_DEPART_AT_BOOL, true);
        setResult(Constants.JOURNEY_TIME_OPTIONS_TO_ACTIVITY_REQUEST, intent);
        finish();
    }

    private void setTime() {
        String fullText = getIntent().getStringExtra(Constants.INTENT_TIME_OPTION);
        if (!fullText.equals(getString(R.string.leave_now))) {
            String[] columns = fullText.split(",");

            String timeString;
            if (columns[0].contains(mTabLayout.getTabAt(0).getText())) {
                timeString = columns[0].trim().substring(Math.min(columns[0].length(), mTabLayout.getTabAt(0).getText().length()));
                mTabLayout.getTabAt(0).select();
            } else {
                timeString = columns[0].trim().substring(Math.min(columns[0].length(), mTabLayout.getTabAt(1).getText().length()));
                mTabLayout.getTabAt(1).select();
            }
            setTimeFromString(timeString);
            mDateButton.setText(columns[1].trim());
        }
    }

    private void setTimeFromString(String string) {
        Calendar c = Common.parseStringToCal(string, "hh:mma");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mTimePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        }else {
            mTimePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(c.get(Calendar.MINUTE));
        }

    }

    private void showDatePicker() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy (EE)", Locale.US);
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
        mDateButton.setText(getString(R.string.today));

        Calendar cal = Calendar.getInstance();
        String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        setTimeFromString(time);
    }

}
