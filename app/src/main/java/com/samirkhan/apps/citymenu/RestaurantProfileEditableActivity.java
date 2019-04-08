package com.samirkhan.apps.citymenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import custom.PreferencesFile;
import datalayer.LocalDataManager;

/* WHAT THE FUCK I WAS THINKING..   */
public class RestaurantProfileEditableActivity extends AppCompatActivity {

    TextView mTextViewName, mTextViewSubtitle, mTextViewAddress, mTextViewPhone, mTextViewMobile,
            mTextViewWebsite, mTextViewTimeFrom, mTextViewTimeTo, mTextViewStars, mTextViewReviews;
    ImageView mImageViewLogo, mImageViewDelivery, mImageViewDarewro;
    LinearLayout mLayout;

    int mRestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile_editable);
        getSupportActionBar().setTitle(R.string.title_restaurant_profile);

        final PreferencesFile preferencesFile = new PreferencesFile(this);
        this.mRestId = preferencesFile.restId();

        mTextViewName = (TextView) findViewById(R.id.text_restaurant_profile_editable_name);
        mTextViewSubtitle = (TextView) findViewById(R.id.text_restaurant_profile_editable_subtitle);
        mTextViewAddress = (TextView) findViewById(R.id.text_restaurant_profile_editable_address);
        mTextViewPhone = (TextView) findViewById(R.id.text_restaurant_profile_editable_phone);
        mTextViewMobile = (TextView) findViewById(R.id.text_restaurant_profile_editable_mobile);
        mTextViewStars = (TextView) findViewById(R.id.text_restaurant_profile_editable_staring);
        mTextViewReviews = (TextView) findViewById(R.id.text_restaurant_profile_editable_reviews);
        mTextViewTimeFrom = (TextView) findViewById(R.id.text_restaurant_profile_editable_time_from);
        mTextViewTimeTo = (TextView) findViewById(R.id.text_restaurant_profile_editable_time_to);
        mTextViewWebsite = (TextView) findViewById(R.id.text_restaurant_profile_editable_website);
        mImageViewLogo = (ImageView) findViewById(R.id.img_restaurant_profile_editable_mainlogo);
        mImageViewDarewro = (ImageView) findViewById(R.id.img_restaurant_profile_editable_darewro);
        mImageViewDelivery = (ImageView) findViewById(R.id.img_restaurant_profile_editable_delivery);
        mLayout = (LinearLayout) findViewById(R.id.layout_restaurant_profile_editable_staring);

        ArrayList<Object[]> data = new LocalDataManager(this).getRestaurant("WHERE r.id = " + mRestId);
        setRestaurantData(data);

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReviewsActivity.class);
                intent.putExtra(PreferencesFile.REST_ID, mRestId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.restaurant_profile_editable, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        if (id == R.id.menu_restaurant_profile_editable_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setNegativeButton(R.string.action_back, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setMessage("Log me out..");
            builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferencesFile preferencesFile = new PreferencesFile(getBaseContext());
                    preferencesFile.removeRestId();
                    preferencesFile.removeRestUsername();
                    preferencesFile.removeRestPassword();

                    Intent intent = new Intent(RestaurantProfileEditableActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                    Snackbar.make(findViewById(android.R.id.content), "Successfully Logged out..", Snackbar.LENGTH_SHORT).show();

                }
            }).show();

        } else if (id == R.id.menu_restaurant_profile_editable_categories) {
            intent = new Intent(this, CategoryEditableActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_restaurant_profile_editable_foods) {
            intent = new Intent(this, FoodEditableActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_restaurant_profile_editable_edit) {
            RestaurantProfileEditableDialog restaurantProfileDialog = new RestaurantProfileEditableDialog(this);
            restaurantProfileDialog.show(getFragmentManager(), "edit_restaurant");
        } else if (id == R.id.text_menu_contact) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        // last updated date..
        else if (id == R.id.text_menu_last_updated) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            PreferencesFile preferencesFile = new PreferencesFile(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_sync, null);
            TextView textView = (TextView) view.findViewById(R.id.text_dialog_sync);
            textView.setText("Last time, City Menu has been synced on " + preferencesFile.lastUpdates());
            builder.setView(view);
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setRestaurantData(ArrayList<Object[]> data) {
        String isDarewroPartner, isDelivery;

        mTextViewName.setText((String) data.get(0)[1]);
        mTextViewSubtitle.setText((String) data.get(0)[2]);
        mTextViewAddress.setText((String) data.get(0)[3]);
        mTextViewPhone.setText((String) data.get(0)[4]);
        mTextViewMobile.setText((String) data.get(0)[5]);
        mTextViewStars.setText((String) data.get(0)[6]);
        mTextViewReviews.setText((String) data.get(0)[7]);
        //String imageName = ((data.get(0)[8] != null) ? data.get(0)[8] : "default_restaurant").toLowerCase();
        mTextViewTimeFrom.setText((String) data.get(0)[9]);
        mTextViewTimeTo.setText((String) data.get(0)[10]);
        isDelivery = (String) data.get(0)[11];
        isDarewroPartner = (String) data.get(0)[12];
        mTextViewWebsite.setText((String) data.get(0)[13]);

        StringBuilder logo;
        logo = ((StringBuilder) data.get(0)[8]);

        byte[] byteArray = Base64.decode(logo.toString(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        mImageViewLogo.setImageBitmap(bitmap);

        /*int drawableId = this.getResources().getIdentifier("com.samirkhan.apps.citymenu:drawable/" + imageName, null, null);
        if (drawableId > 0) {
            Bitmap image = BitmapFactory.decodeResource(this.getResources(), drawableId);
            mImageViewLogo.setImageBitmap(RoundImage.getRoundedCornerBitmap(image, 170));
        }*/
        //
        BitmapDrawable.createFromPath("@drawable/ic_cross");
        // set darewro partner light
        if (Integer.parseInt(isDarewroPartner) > 0) {
         /*   mImageViewDarewro.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                    (this.getResources().getIdentifier("@drawable/ic_red_tick", null, null))));*/

            mImageViewDarewro.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_tick));

          /*  mImageViewDarewro.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                    (this.getResources().getIdentifier("@drawable/ic_cross", null, null))));*/
        } else
            mImageViewDarewro.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
        // set darewro partner light
        if (Integer.parseInt(isDelivery) > 0) {
     /*       mImageViewDelivery.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                    (this.getResources().getIdentifier("@drawable/ic_red_tick", null, null))));*/
            mImageViewDelivery.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_tick));
        } else {
          /*  mImageViewDelivery.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                    (this.getResources().getIdentifier("@drawable/ic_cross", null, null))));*/
            mImageViewDelivery.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));

        }
    }


}
