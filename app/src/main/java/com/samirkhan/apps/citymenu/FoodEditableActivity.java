package com.samirkhan.apps.citymenu;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import custom.PreferencesFile;
import datalayer.LocalDataManager;

public class FoodEditableActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_editable);
        getSupportActionBar().setTitle(R.string.title_food_activity);

        // get rest_Id of logged-in account..
        PreferencesFile preferencesFile = new PreferencesFile(this);
        int restId = preferencesFile.restId();
        if (restId == preferencesFile.DEFAULT_REST_ID) {
           /* Snackbar.make(findViewById(android.R.id.content), "Please Login to the Restaurant Account.",
                    Snackbar.LENGTH_SHORT).show();*/
            Toast.makeText(this, "Please Login to the Restaurant Account.", Toast.LENGTH_SHORT).show();

            return;
        }
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)
                findViewById(R.id.swipe_food_editable);
        swipeRefreshLayout.setOnRefreshListener(this);


        ListView list = (ListView) findViewById(R.id.list_food_editable);
        String keyword = handleSearch(getIntent());
        ArrayList<String[]> data;

        if (keyword == null) {
            data = new LocalDataManager(getBaseContext()).getFood("WHERE f.rest_id = " + restId + " ORDER BY f.name ");
        } else if (keyword.equals("")) {
            //  Snackbar.make(findViewById(android.R.id.content), "No result found..", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(this, "No result found..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            data = new LocalDataManager(getBaseContext()).getFood("WHERE f.rest_id = " + restId + " AND f.name like '%" + keyword + "%'"
                    + " ORDER BY f.name ");
        }

        if (data == null || data.size() < 1) {
            /*Snackbar.make(findViewById(android.R.id.content), "Restuarant Data Do Not Exist.\n Please Re-Login.. ",
                    Snackbar.LENGTH_SHORT).show();*/
            Toast.makeText(this, "Restuarant data do not exist.\n" +
                    " Please login again.. ", Toast.LENGTH_SHORT).show();
            return;
        }
        CustomFoodAdapter adapter = new CustomFoodAdapter(this, data, true);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.food_editable, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_food_editable_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_food_editable_add) {
            FoodAddDialog dialog = new FoodAddDialog(this);
            dialog.show(getFragmentManager(), "add_food");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        this.finish();
        (this).startActivity(this.getIntent());
    }


    public String handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String keyword = intent.getStringExtra(SearchManager.QUERY).trim();
            return keyword;
        }
        return null;
    }
}

