package com.id11303765.commute.view;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.id11303765.commute.R;
import com.id11303765.commute.model.DatabaseHelper;
import com.id11303765.commute.utils.Constants;


public class WelcomeFragment extends Fragment implements View.OnClickListener {
    View mCustomView;

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
        Button button = (Button) getActivity().findViewById(R.id.fragment_welcome_setup_button);
        button.setOnClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        LaunchPopulateDB a = new LaunchPopulateDB(dbHelper);
        a.execute();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(),SettingsActivity.class));
    }


    private void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    /**
     * Populates the internal database with the GTFS data from Transport NSW
     */
    private class LaunchPopulateDB extends AsyncTask<Void, Void, Void> {

        DatabaseHelper mDB;

        public LaunchPopulateDB(DatabaseHelper db){
            mDB = db;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDB.populateDb();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showToast("Transport data successfully imported");
        }
    }

}
