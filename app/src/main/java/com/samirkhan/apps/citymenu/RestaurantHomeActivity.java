package com.samirkhan.apps.citymenu;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import custom.OptionMenuItemListner;
import custom.Viewer;
import datalayer.LocalDataManager;

public class RestaurantHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int mRestId;
    ArrayList<String[]> mFoodCategories;
    ArrayList<Object[]> mRestData;
    private boolean mIsDarewroPartner, mIsDelivery;

    private TextView mTextViewName, mTextViewSubtitle, mTextViewAddress, mTextViewPhone, mTextViewMobile, mTextViewModifiedDate;
    private TextView mTextViewWebsite, mTextViewTimeTo, mTextViewStartingTime, mTextViewStars, mTextViewReviews;
    private ImageView mImageViewLogo, mImageViewDarewro, mImageViewDelivery;
    private ListView mListViewFoods;

    private ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_home);

        mRestId = getIntent().getIntExtra("restId", 0);
        mRestData = new LocalDataManager(this).getRestaurant("WHERE r.id = " + mRestId);
        if (mRestData == null || mRestData.size() < 1) {
            // Snackbar.make(findViewById(android.R.id.content), "No Record found.", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(this, "No Record found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        // add new viewer
        Viewer viewer = new Viewer(this);
        viewer.addRestViewer(mRestId);

        mListViewFoods = (ListView) findViewById(R.id.list_restaurant_home_foods);
        mTextViewName = (TextView) findViewById(R.id.text_partial_restaurant_profile_name);
        mTextViewSubtitle = (TextView) findViewById(R.id.text_partial_restaurant_profile_subtitle);
        mTextViewAddress = (TextView) findViewById(R.id.text_partial_restaurant_profile_address);
        mTextViewPhone = (TextView) findViewById(R.id.text_partial_restaurant_profile_phone);
        mTextViewMobile = (TextView) findViewById(R.id.text_partial_restaurant_profile_mobile);
        mTextViewStars = (TextView) findViewById(R.id.text_partial_restaurant_profile_staring);
        mTextViewReviews = (TextView) findViewById(R.id.text_partial_restaurant_profile_reviews);
        mTextViewStartingTime = (TextView) findViewById(R.id.text_partial_restaurant_profile_time_from);
        mTextViewTimeTo = (TextView) findViewById(R.id.text_partial_restaurant_profile_time_to);
        mTextViewWebsite = (TextView) findViewById(R.id.text_partial_restaurant_profile_website);
        mTextViewModifiedDate = (TextView) findViewById(R.id.text_partial_restaurant_profile_modified_date);

        mImageViewLogo = (ImageView) findViewById(R.id.img_partial_restaurant_profile_mainlogo);
        mImageViewDarewro = (ImageView) findViewById(R.id.img_partial_restaurant_profile_darewro);
        mImageViewDelivery = (ImageView) findViewById(R.id.img_partial_restaurant_profile_delivery);
        layout = (LinearLayout) findViewById(R.id.layout_partial_restaurant_profile_staring);

        // add review layout clickable...
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReviewsActivity.class);
                intent.putExtra("restId", mRestId);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setRestData(mRestData);
        mFoodCategories = (ArrayList<String[]>) new LocalDataManager(this)
                .getCategory("WHERE c.rest_id = " + mRestId).clone();

        for (int i = 0; i < mFoodCategories.size(); i++) {
            menu.add(mFoodCategories.get(i)[1]);
        }

        String keyword = handleSearch(getIntent());
        if (keyword != null) {
            CustomFoodAdapter customFoodAdapter = getFoodAdapter(mRestId, keyword);
            if (customFoodAdapter != null)
                mListViewFoods.setAdapter(customFoodAdapter);
        } else if (mFoodCategories.size() > 0) {
            onNavigationItemSelected(menu.getItem(0));
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int categoryId = getFoodCategoryId(item.getTitle().toString());
        CustomFoodAdapter customFoodAdapter = getFoodAdapter(categoryId);
        if (customFoodAdapter != null)
            mListViewFoods.setAdapter(customFoodAdapter);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public int getFoodCategoryId(String categoryName) {
        for (int i = 0; i < mFoodCategories.size(); i++) {
            if (categoryName == mFoodCategories.get(i)[1]) {
                return Integer.parseInt(mFoodCategories.get(i)[0]);
            }
        }
        return -1;
    }

    public CustomFoodAdapter getFoodAdapter(int categoryId) {

        ArrayList<String[]> foods = new ArrayList<>();
        foods = new LocalDataManager(this).getFood("WHERE c.id = " + categoryId + " AND r.id = " + mRestId);
        if (foods == null || foods.size() < 1)
            return null;
        CustomFoodAdapter adapter = new CustomFoodAdapter(this, foods, false);
        return adapter;
    }

    public CustomFoodAdapter getFoodAdapter(int restId, String keyword) {
        ArrayList<String[]> foods = new ArrayList<>();
        foods = new LocalDataManager(this).getFood("WHERE r.id =" + restId + " AND f.name like '%" + keyword + "%'");
        if (foods == null || foods.size() < 1)
            return null;
        CustomFoodAdapter adapter = new CustomFoodAdapter(this, foods, false);
        return adapter;
    }

    // set data to retaurantprofile
    private void setRestData(ArrayList<Object[]> data) {
        mTextViewName.setText((String) data.get(0)[1]);
        mTextViewSubtitle.setText((String) data.get(0)[2]);
        mTextViewAddress.setText((String) data.get(0)[3]);
        mTextViewPhone.setText((String) data.get(0)[4]);
        mTextViewMobile.setText((String) data.get(0)[5]);
        mTextViewStars.setText((String) data.get(0)[6]);
        mTextViewReviews.setText((String) data.get(0)[7]);
        mTextViewStartingTime.setText((String) data.get(0)[9]);
        mTextViewTimeTo.setText((String) data.get(0)[10]);
        /// just set values..
        mIsDarewroPartner = (Integer.parseInt((String) data.get(0)[11]) == 1) ? true : false;
        mIsDelivery = (Integer.parseInt((String) data.get(0)[12]) == 1) ? true : false;
        mTextViewWebsite.setText((String) data.get(0)[13]);
        mTextViewModifiedDate.setText(((String) data.get(0)[14]).substring(0, 10));

        StringBuilder logo;
        logo = ((StringBuilder) data.get(0)[8]);

        byte[] byteArray = Base64.decode(logo.toString(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        mImageViewLogo.setImageBitmap(bitmap);
        if (mIsDarewroPartner) {
            mImageViewDarewro.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_tick));
        } else {
            mImageViewDarewro.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
        }
        if (mIsDelivery) {
            mImageViewDelivery.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_tick));

        } else {
            mImageViewDelivery.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
        }
    }
}