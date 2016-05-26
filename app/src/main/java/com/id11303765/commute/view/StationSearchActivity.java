package com.id11303765.commute.view;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;

public class StationSearchActivity extends AppCompatActivity {

    private ListView mListView;
    private Cursor mCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_search);
        setTitle(R.string.search_hint);
        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_search_menu, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchMenuItem = menu.findItem(R.id.activity_search_menu_search_item);
        searchMenuItem.expandActionView();
        SearchView search = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        /*search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(Constants.SEARCHTAG, "onQueryTextSubmit ");
                cursor = studentRepo.getStudentListByKeyword(s);
                if (cursor == null) {
                    Toast.makeText(MainActivity.this, "No records found!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, cursor.getCount() + " records found!", Toast.LENGTH_LONG).show();
                }
                customAdapter.swapCursor(cursor);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(Constants.SEARCHTAG, "onQueryTextChange ");
                cursor = studentRepo.getStudentListByKeyword(s);
                if (cursor != null) {
                    customAdapter.swapCursor(cursor);
                }
                return false;
            }

        });*/


        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
