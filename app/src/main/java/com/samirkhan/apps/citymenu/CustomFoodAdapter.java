package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import datalayer.LocalDataManager;

/**
 * Created by Samir KHan on 8/30/2016.
 */
class CustomFoodAdapter extends BaseAdapter {

    Activity mActivity;
    ArrayList<String[]> mData;
    int mRestId;
    boolean editable;

    public CustomFoodAdapter(Activity activity, ArrayList<String[]> mData, boolean editable) {
        this.mActivity = activity;
        if (mData.size() > 0)
            this.mData = (ArrayList<String[]>) mData.clone();
        else
            this.mData = mData;
        if (mData.size() > 0)
            this.mRestId = Integer.parseInt(mData.get(0)[8]);
        this.editable = editable;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.indexOf(position);
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
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            view = layoutInflater.inflate(R.layout.item_food, parent, false);

            final TextView textViewName = (TextView) view.findViewById(R.id.text_item_food_name);
            final TextView textViewPrice = (TextView) view.findViewById(R.id.text_item_food_price);
            final TextView textViewTime = (TextView) view.findViewById(R.id.text_item_food_time);
            final TextView textViewDescription = (TextView) view.findViewById(R.id.text_item_food_description);
            final TextView textViewFlavours = (TextView) view.findViewById(R.id.text_item_food_flavours);
            final ImageView imageViewLogo = (ImageView) view.findViewById(R.id.img_item_food_logo);
            final ImageView imageViewAvail = (ImageView) view.findViewById(R.id.img_item_food_available);

            textViewName.setText(mData.get(position)[1]);
            textViewPrice.setText(mData.get(position)[2]);
            textViewDescription.setText(mData.get(position)[3]);
            textViewFlavours.setText(mData.get(position)[4]);
            textViewTime.setText(mData.get(position)[5]);
            final String availImgName = ((mData.get(position)[6] != null) ? mData.get(position)[6] : "NO");
            final String logoImgName = ((mData.get(position)[7] != null) ? mData.get(position)[7] : "ic_default_food").toLowerCase();

            if (availImgName == "YES") {
                imageViewAvail.setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(),
                        (mActivity.getResources().getIdentifier("@android:drawable/presence_online", null, null))));
            } else {
                imageViewAvail.setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(),
                        (mActivity.getResources().getIdentifier("@android:drawable/presence_offline", null, null))));
                textViewName.setTextColor(Color.parseColor("#d9d9d9"));
                textViewPrice.setTextColor(Color.parseColor("#d9d9d9"));
                textViewDescription.setTextColor(Color.parseColor("#d9d9d9"));
                textViewFlavours.setTextColor(Color.parseColor("#d9d9d9"));
                textViewTime.setTextColor(Color.parseColor("#d9d9d9"));
                imageViewLogo.setAlpha((float) 0.3);
            }

            int idDrawableLogo = mActivity.getResources().getIdentifier("com.samirkhan.apps.citymenu:drawable/" + logoImgName, null, null);
            imageViewLogo.setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(), idDrawableLogo));

            // click event listener..
            if (editable)
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ArrayList<String[]> list = new LocalDataManager(mActivity)
                                .getFood(" WHERE f.rest_id = " + mRestId + " AND f.name = '" + textViewName.getText().toString() + "' ");

                        String[] data = new String[]{
                                textViewName.getText().toString(), textViewPrice.getText().toString(), textViewTime.getText().toString(),
                                textViewDescription.getText().toString(),
                                textViewFlavours.getText().toString(), availImgName, mData.get(position)[7]};
                        FoodEditableDialog dialog = new FoodEditableDialog(mActivity, data);
                        dialog.show(mActivity.getFragmentManager(), "update_food");
                    }
                });
        }
        return view;
    }

}
