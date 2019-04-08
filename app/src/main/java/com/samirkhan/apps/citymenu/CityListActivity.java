package com.samirkhan.apps.citymenu;

import android.app.Activity;
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
import android.widget.TextView;

import java.util.ArrayList;

import custom.OptionMenuItemListner;
import custom.PreferencesFile;
import datalayer.LocalDataManager;

public class CityListActivity extends AppCompatActivity {

    private final String SET_DEFAULT_CITY = "setDefaultCity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        getSupportActionBar().setTitle("Cities");

        ListView list = (ListView) findViewById(R.id.list_activity_city_list);

        Boolean selectDefaultCity = getIntent().getBooleanExtra("setDefaultCity", false);
        CityListAdapter adapter;


        adapter = new CityListAdapter(this, new LocalDataManager(this).getCity(null, true), selectDefaultCity);
        list.setAdapter(adapter);
/*
        if (selectDefaultCity) {
            Snackbar.make(findViewById(android.R.id.content), "Select Default City", Snackbar.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu_without_search, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        OptionMenuItemListner optionMenuItemListner = new OptionMenuItemListner(this, id);
        optionMenuItemListner.performAction();
        return super.onOptionsItemSelected(item);
    }
}

/*
*  CITY CUSTOM ADAPTER..
* */
class CityListAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<Object[]> data;
    Boolean selectDefaultCity;

    public CityListAdapter(Context context, ArrayList<Object[]> list, Boolean selectDefaultCity) {
        this.context = context;
        this.data = (ArrayList<Object[]>) list.clone();
        this.selectDefaultCity = selectDefaultCity;
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
            view = layoutInflater.inflate(R.layout.item_city_list, parent, false);

            TextView textViewName = (TextView) view.findViewById(R.id.text_item_city_list_name);
            TextView textViewState = (TextView) view.findViewById(R.id.text_item_city_list_state);
            TextView textViewRestSize = (TextView) view.findViewById(R.id.text_item_city_list_rest_size);
            TextView textViewRestLabel = (TextView) view.findViewById(R.id.text_item_city_list_rest_label);
            ImageView imgView = (ImageView) view.findViewById(R.id.img_item_city_list_img);

            textViewName.setText(((String) data.get(position)[1] != null) ? (String) data.get(position)[1] : "City");
            textViewState.setText(((String) data.get(position)[2] != null) ? (String) data.get(position)[2] : "Address");
            textViewRestSize.setText((String) data.get(position)[4]);
            if(textViewRestSize.getText().toString().equals("0")){
                textViewRestLabel.setText("restaurant");
            } else {
                textViewRestLabel.setText("restaurants");
                textViewRestLabel.setTextColor(Color.parseColor("#D33A34"));
                textViewRestSize.setTextColor(Color.parseColor("#D33A34"));
            }
            StringBuilder logo;
            logo = ((StringBuilder) data.get(position)[3]);

            byte[] byteArray = Base64.decode(logo.toString(), Base64.DEFAULT);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
            imgView.setImageBitmap(bitmap);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // find city Id..
                TextView textView = (TextView) v.findViewById(R.id.text_item_city_list_name);

                int cityId = -1;
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i)[1].equals(textView.getText().toString())) {
                        cityId = Integer.parseInt((String) data.get(i)[0]);
                        break;
                    }
                }
                if (selectDefaultCity) {
                    PreferencesFile preferencesFile = new PreferencesFile(context);
                    preferencesFile.setDefaultCityId(cityId);

                   /* Toast.makeText(context, textView.getText().toString() + " has been Selected as Default City."
                            , Toast.LENGTH_SHORT).show();*/
                }

                Intent intent = new Intent(context, RestaurantListActivity.class);
                intent.putExtra("cityId", cityId);
                context.startActivity(intent);


                ((Activity) context).finish();
            }
        });
        return view;
    }
}
