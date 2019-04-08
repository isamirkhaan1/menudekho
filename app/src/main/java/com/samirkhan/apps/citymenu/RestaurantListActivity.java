package com.samirkhan.apps.citymenu;
//

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import custom.OptionMenuItemListner;
import custom.PreferencesFile;
import custom.RoundImage;
import datalayer.LocalDataManager;

public class RestaurantListActivity extends AppCompatActivity {

    boolean mIsForRestReviews, mIsForRestLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        getSupportActionBar().setTitle("Restaurants");

        ListView listRestaurants = (ListView) findViewById(R.id.list_activity_restaurant_list);
        TextView textViewStatus = (TextView) findViewById(R.id.text_activity_restaurant_list_status);

        int cityId = getIntent().getIntExtra("cityId", 0);
        if (cityId == 0) {
            cityId = new PreferencesFile(this).defaultCityId();
        }
        mIsForRestReviews = getIntent().getBooleanExtra("forReviews", false);
        mIsForRestLogin = getIntent().getBooleanExtra("isRestaurantLogin", false);

        CustomRestaurantAdapter adapter;
        ArrayList<Object[]> data;
        String keyword = handleSearch(getIntent());
        if (keyword == null) {
            data = new LocalDataManager(this).getRestaurant("WHERE r.city_id = " + cityId);
            if (data == null || data.size() < 1)
                return;
            adapter = new CustomRestaurantAdapter(this, data, mIsForRestReviews, mIsForRestLogin);
            listRestaurants.setAdapter(adapter);
        } else if (keyword.equals("")) {
            Toast.makeText(this, "No result found..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            data = new LocalDataManager(this).getRestaurant("WHERE r.name like '%" + keyword + "%'");
            if (data == null || data.size() < 1)
                return;
            adapter = new CustomRestaurantAdapter(this, data, mIsForRestReviews, mIsForRestLogin);
            listRestaurants.setAdapter(adapter);
        }

       /* if (isForRestReviews)
            textViewStatus.setText("Select a restaurant to review on their feed.");*/
        if (mIsForRestLogin) {
            textViewStatus.setText("Tap on a restaurant, you wanna login into..");
            textViewStatus.setTextSize(11);
            textViewStatus.setTextColor(Color.parseColor("#D33A34"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu_with_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.text_menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        OptionMenuItemListner optionMenuItemListner = new OptionMenuItemListner(this, id);
        optionMenuItemListner.performAction();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivity(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (mIsForRestLogin)
                intent.putExtra("isRestaurantLogin", mIsForRestLogin);
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
}


/*
* ADAPTER CLASS FOR RESTAURANT LIST..
* */

class CustomRestaurantAdapter extends BaseAdapter {


    Context context;
    ViewHolder holder;
    ArrayList<Object[]> data, list;
    Boolean isForRestReviews = false, isRestaurantLogin = false;

    public CustomRestaurantAdapter(Context context, ArrayList<Object[]> list, Boolean isForRestReviews, Boolean isRestaurantLogin) {

        this.context = context;
        this.list = list;
        this.data = (ArrayList<Object[]>) this.list.clone();
        this.isForRestReviews = isForRestReviews;
        this.isRestaurantLogin = isRestaurantLogin;
        holder = new ViewHolder();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_restaurant_list, parent, false);

            holder.textViewRestName = (TextView) view.findViewById(R.id.text_item_restaurant_list_name);
            holder.textViewSubtitle = (TextView) view.findViewById(R.id.text_item_restaurant_list_subtitle);
            holder.textViewAddress = (TextView) view.findViewById(R.id.text_item_restaurant_list_address);

            holder.textViewStaring = (TextView) view.findViewById(R.id.text_item_restaurant_list_staring);
            holder.textViewReviews = (TextView) view.findViewById(R.id.text_item_restaurant_list_reviews);
            holder.imgRest = (ImageView) view.findViewById(R.id.img_item_restaurant_list_img);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewRestName.setText(((String) data.get(position)[1] != null) ? (String) data.get(position)[1] : "");
        holder.textViewSubtitle.setText(((String) data.get(position)[2] != null) ? (String) data.get(position)[2] : "");
        holder.textViewAddress.setText(((String) data.get(position)[3] != null) ? (String) data.get(position)[3] : "");

        holder.textViewStaring.setText(((String) data.get(position)[6] != null) ? (String) data.get(position)[6] : "");
        holder.textViewReviews.setText(((String) data.get(position)[7] != null) ? (String) data.get(position)[7] : "");

        StringBuilder logo;
        logo = ((StringBuilder) data.get(position)[8]);

        byte[] byteArray = Base64.decode(logo.toString(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        holder.imgRest.setImageBitmap(RoundImage.getRoundedCornerBitmap(bitmap, 170));

        // onClickListener..
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textView = (TextView) v.findViewById(R.id.text_item_restaurant_list_name);
                int restId = -1;
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i)[1].equals(textView.getText().toString())) {
                        restId = Integer.parseInt((String) data.get(i)[0]);
                        break;
                    }
                }

                Intent intent;
                if (isForRestReviews)
                    intent = new Intent(context, AddReviewActivity.class);
                else if (isRestaurantLogin)
                    intent = new Intent(context, RestaurantLoginActivity.class);
                else
                    intent = new Intent(context, RestaurantHomeActivity.class);

                intent.putExtra("restId", restId);
                context.startActivity(intent);
            }
        });

        return view;
    }

    class ViewHolder {
        TextView textViewRestName, textViewSubtitle, textViewAddress,
                textViewPhoneNo, textViewMobileNo, textViewStaring, textViewReviews;
        ImageView imgRest;

    }
}