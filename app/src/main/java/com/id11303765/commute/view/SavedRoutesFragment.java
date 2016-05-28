package com.id11303765.commute.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.id11303765.commute.R;
import com.id11303765.commute.model.DatabaseHelper;
import com.id11303765.commute.utils.Constants;


public class SavedRoutesFragment extends Fragment implements View.OnClickListener {
    View mCustomView;

    public SavedRoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_saved_routes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.saved_routes);

        FloatingActionButton button = (FloatingActionButton) getActivity().findViewById(R.id.fragment_saved_routes_add_fab);
        button.setOnClickListener(this);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onClick(View v) {
    }

    private void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
