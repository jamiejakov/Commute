package com.id11303765.commute.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.id11303765.commute.R;
import com.id11303765.commute.model.DatabaseHelper;
import com.id11303765.commute.utils.Constants;
import com.id11303765.commute.view.journey.JourneyFragment;


public class WelcomeFragment extends Fragment implements View.OnClickListener {
    private Button mButton;
    private TextView mText;
    private ProgressBar mProgressBar;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.commute);
        mText = (TextView) getActivity().findViewById(R.id.fragment_welcome_start_text_view);
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.fragment_welcome_progress_bar);
        mButton = (Button) getActivity().findViewById(R.id.fragment_welcome_setup_button);
        mButton.setOnClickListener(this);
        mButton.setEnabled(false);
    }

    @Override
    public void onStart(){
        super.onStart();
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        LaunchPopulateDbAsync a = new LaunchPopulateDbAsync(dbHelper);
        a.execute();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(getActivity(), SettingsActivity.class),
                Constants.WELCOME_TO_SETTINGS_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.WELCOME_TO_SETTINGS_REQUEST:
            FragmentManager frag = getActivity().getFragmentManager();
            frag.beginTransaction().replace(R.id.activity_main_content_frame, new JourneyFragment()).commit();
        }
    }

    private void enableButton() {
        mText.setText(getString(R.string.getting_started));
        mButton.setEnabled(true);
    }

    /**
     * Populates the internal database with the GTFS data from Transport NSW
     */
    private class LaunchPopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private DatabaseHelper mDB;


        public LaunchPopulateDbAsync(DatabaseHelper db){
            mDB = db;
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDB.populateMainTables();
            publishProgress();
            mDB.populateStopTimesTable();
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            mProgressBar.setVisibility(View.GONE);
            enableButton();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

}
