package com.samirkhan.apps.citymenu;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import custom.PreferencesFile;
import datalayer.LocalDataManager;

public class ReviewsActivity extends AppCompatActivity {

    int mRestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setTitle(R.string.empty);

        mRestId = getIntent().getIntExtra("restId", 0);
        if (mRestId == 0) {
            Snackbar.make(findViewById(android.R.id.content), "No Restuarant Selected.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        ListView list = (ListView) findViewById(R.id.list_reviews);

        String keyword = handleSearch(getIntent());
        ArrayList<String[]> data;
        if (keyword == null) {
            data = new LocalDataManager(this).getReview("WHERE r.id = " + mRestId);
        } else if (keyword.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "No Result Found..", Snackbar.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            data = new LocalDataManager(this).getReview("WHERE rev.name like '%" + keyword + "%'");
        }

        ReviewListAdapter adapter = new ReviewListAdapter(this, data);
        if(data != null && data.size()>0)
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.reviews, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_reviews_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_reviews_add) {
            if (new PreferencesFile(this).restId() != 0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sorry");
                builder.setMessage("Restaurant administration cannot review on feeds.");
                builder.setPositiveButton(R.string.action_got_it, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
            Intent intent = new Intent(this, AddReviewActivity.class);
            intent.putExtra(PreferencesFile.REST_ID, mRestId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivity(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("restId", mRestId);
        }
        super.startActivity(intent);
    }

    public String handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String keyword = intent.getStringExtra(SearchManager.QUERY).trim();
            return keyword;
        }
        return null;
    }

    class ReviewListAdapter extends BaseAdapter {

        Context context;
        ArrayList<String[]> data;

        public ReviewListAdapter(Context context, ArrayList<String[]> data) {
            this.context = context;
            this.data = (ArrayList<String[]>) data.clone();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.indexOf(position);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                final LayoutInflater inflater = LayoutInflater.from(context);
                v = inflater.inflate(R.layout.item_review, parent, false);

                final TextView textViewName = (TextView) v.findViewById(R.id.text_item_review_username);
                final TextView textViewRemarks = (TextView) v.findViewById(R.id.text_item_review_remarks);
                final TextView textViewStars = (TextView) v.findViewById(R.id.text_item_review_stars);
                final TextView textViewDate = (TextView) v.findViewById(R.id.text_item_review_date);

                textViewName.setText(data.get(position)[1]);
                textViewRemarks.setText(data.get(position)[3]);
                textViewStars.setText(data.get(position)[4]);
                textViewDate.setText(data.get(position)[5]);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      /*  View viewDialog = inflater.inflate(R.layout.dialog_add_category, null);
                        TextView textViewNameDialog = (TextView) viewDialog.findViewById(R.id.text_dialog_review_name);
                        TextView textViewRemarksDialog = (TextView) viewDialog.findViewById(R.id.text_dialog_review_remarks);
                        TextView textViewStarsDialog = (TextView) viewDialog.findViewById(R.id.text_dialog_review_stars);
                        TextView textViewDateDialog = (TextView) viewDialog.findViewById(R.id.text_dialog_review_date);*/

                        /* textViewNameDialog.setText(textViewName.getText());
                        textViewRemarksDialog.setText(textViewRemarks.getText());
                        textViewStarsDialog.setText(textViewStars.getText());
                        textViewDateDialog.setText(textViewDate.getText());*/

                       /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(textViewName.getText().toString());
                        builder.setMessage(textViewRemarks.getText().toString());
                        builder.setPositiveButton(R.string.action_got_it, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();*/
                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setTitle(textViewName.getText().toString());
                        builder.setMessage(textViewRemarks.getText().toString());
                        builder.setPositiveButton(R.string.action_got_it, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }

            return v;
        }
    }
}
