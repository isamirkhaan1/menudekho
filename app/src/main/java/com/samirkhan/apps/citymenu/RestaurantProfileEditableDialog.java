package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import custom.ExceptionHandling;
import custom.PreferencesFile;
import datalayer.LocalDataManager;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

/**
 * Created by Samir KHan on 8/19/2016.
 */
public class RestaurantProfileEditableDialog extends DialogFragment {
    Context mContext;

    public RestaurantProfileEditableDialog(Context context) {
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_edit_restaurant_profile, null);

        final EditText editViewName = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_name);
        final EditText editViewSubtitle = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_subtitle);
        final EditText editViewStreetAddress =
                (EditText) view.findViewById(R.id.text_dialog_edit_restaurant_profile_street_address);
        final Spinner spinnerCity = (Spinner)
                view.findViewById(R.id.spin_dialog_edit_restaurant_profile);
        final EditText editViewPhone = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_phone);
        final EditText editViewMobile = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_mobile);
        final EditText editViewTimeFrom = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_time_from);
        final EditText editViewTimeTo = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_time_to);
        final EditText editTextWebsite = (EditText)
                view.findViewById(R.id.text_dialog_edit_restaurant_profile_website);
        final CheckBox checkBoxDelivery = (CheckBox)
                view.findViewById(R.id.check_dialog_edit_restaurant_profile_delivery);
        final CheckBox checkBoxDarewro = (CheckBox)
                view.findViewById(R.id.check_dialog_edit_restaurant_profile_darewro);
        final ImageView imgViewEditTimeFrom = (ImageView)
                view.findViewById(R.id.img_dialog_edit_restaurant_profile_time_from);
        final ImageView imgViewEditTimeTo = (ImageView)
                view.findViewById(R.id.img_dialog_edit_restaurant_profile_time_to);

        PreferencesFile preferencesFile = new PreferencesFile(mContext);
        final int restId = preferencesFile.restId();
        ArrayList<Object[]> data = new LocalDataManager(mContext).getRestaurant("WHERE r.id = " + restId);

        if (data == null || data.size() < 1) {
            // Toast.makeText(mContext, "Please login to the restaurant account.", Toast.LENGTH_SHORT).show();
            Snackbar.make(((Activity) mContext).findViewById(android.R.id.content),
                    "Please login to the restaurant account.", Snackbar.LENGTH_SHORT).show();
            return null;
        }

        final String query = "SELECT c.name FROM city AS c INNER JOIN state AS st ON c.state_id = st.id" +
                " INNER JOIN country count ON count.id = st.country_id  WHERE  country_id = 1";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,
                new LocalDataManager(mContext).getSingleColumn(query));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        editViewName.setText((String) data.get(0)[1]);
        editViewSubtitle.setText((String) data.get(0)[2]);
        editViewStreetAddress.setText(((String) data.get(0)[3]).split(",")[0]);
        spinnerCity.setSelection(adapter.getPosition(((String) data.get(0)[3]).split(",")[1]));
        editViewPhone.setText((String) data.get(0)[4]);
        editViewMobile.setText((String) data.get(0)[5]);
        editViewTimeFrom.setText((String) data.get(0)[9]);
        editViewTimeTo.setText((String) data.get(0)[10]);
        checkBoxDelivery.setChecked(((((String) data.get(0)[11]).equals("1")) ? true : false));
        checkBoxDarewro.setChecked(((((String) data.get(0)[12]).equals("1")) ? true : false));
        editTextWebsite.setText((String) data.get(0)[13]);
        String cityNameString = ((String) data.get(0)[3]).split(",")[1];
        int i = ((ArrayAdapter) spinnerCity.getAdapter())
                .getPosition(((String) data.get(0)[3]).split(",")[1].trim());
        spinnerCity.setSelection(i);

        imgViewEditTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editViewTimeFrom.setText(hourOfDay + ":" + minute + ":00");
                    }
                }, 9, 00, false);
                timePickerDialog.show();
            }
        });
        imgViewEditTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editViewTimeTo.setText(hourOfDay + ":" + minute + ":00");
                    }
                }, 9, 00, false);
                timePickerDialog.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.setNeutralButton(R.string.action_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = editViewName.getText().toString().trim();
                String subtitle = editViewSubtitle.getText().toString().trim();
                String streetAddress = editViewStreetAddress.getText().toString().trim();
                String cityName = spinnerCity.getSelectedItem().toString().trim();
                String phone = editViewPhone.getText().toString().trim();
                String mobile = editViewMobile.getText().toString().trim();
                String website = editTextWebsite.getText().toString().trim();
                String timeFrom = editViewTimeFrom.getText().toString().trim();
                String timeTo = editViewTimeTo.getText().toString().trim();
                String delivery = (checkBoxDelivery.isChecked() ? "1" : "0");
                String darewro = (checkBoxDarewro.isChecked() ? "1" : "0");

                String query = "SELECT id FROM city WHERE name = '" + cityName + "'";
                String cityId = new LocalDataManager(mContext).getSingleColumn(query).get(0);
                PromptDialog promptDialog;
                if (name.length() < 3) {
                    promptDialog = new PromptDialog(mContext, "Name", "Name must be larger than 3 characters.", R.string.action_got_it);
                    promptDialog.show(getFragmentManager(), "edit_rest");
                    return;
                }
                if (subtitle.length() < 3) {
                    promptDialog = new PromptDialog(mContext, "Subtitle", "Subtitle must be larger than 3 characters.", R.string.action_got_it);
                    promptDialog.show(getFragmentManager(), "edit_rest");
                    return;
                }
                if (streetAddress.length() < 5) {
                    promptDialog = new PromptDialog(mContext, "Street Address", "Street address must be larger than 5 characters.", R.string.action_got_it);
                    promptDialog.show(getFragmentManager(), "edit_rest");
                    return;
                }
                if (phone.length() < 7) {
                    if (phone.length() == 0) {
                        phone = "Not Provided";
                    } else {
                        promptDialog = new PromptDialog(mContext, "Phone No.", "Phone No. must be larger than 7 characters.", R.string.action_got_it);
                        promptDialog.show(getFragmentManager(), "edit_rest");
                        return;
                    }
                }
                if (mobile.length() < 10) {
                    if (mobile.length() == 0) {
                        mobile = "Not Provided";
                    } else {
                        promptDialog = new PromptDialog(mContext, "Mobile No.", "Mobile No. must be larger than 10 characters.", R.string.action_got_it);
                        promptDialog.show(getFragmentManager(), "edit_rest");
                        return;
                    }
                }
                if (website.length() < 7) {
                    if (website.length() < 1) {
                        website = "Not Provided";
                    } else {
                        promptDialog = new PromptDialog(mContext, "Website", "Website address must be larger than 7 characters.", R.string.action_got_it);
                        promptDialog.show(getFragmentManager(), "edit_rest");
                        return;
                    }
                }
                if (timeFrom.length() < 1) {
                    timeFrom = "Not Provided";

                }
                if (timeTo.length() < 1) {
                    timeTo = "Not Provided";

                }
                String[] data = new String[]{name, subtitle, streetAddress, cityId, phone, mobile,
                        timeFrom, timeTo, delivery, darewro, website, restId + ""};
                EditRestaurantService service = new EditRestaurantService(mContext, data);
                service.execute();
            }
        });
        return builder.create();
    }
}

class EditRestaurantService extends AsyncTask {
    Context context;
    String[] data;
    String userMessage;
    ProgressDialog progressDialog;

    public EditRestaurantService(Context context, String[] data) {
        this.context = context;
        this.data = data;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Updating profile..");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = UrlString.RootUrl() + "restaurant.php";
        HashMap<String, String> parameters = new HashMap<>();

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            parameters.put("client", "ANDROID");
            parameters.put("action", "UPDATE");
            parameters.put("name", data[0]);
            parameters.put("subtitle", data[1]);
            parameters.put("street_address", data[2]);
            parameters.put("city_id", data[3]);
            parameters.put("phone_no", data[4]);
            parameters.put("mobile_no", data[5]);
            parameters.put("op_time", data[6]);
            parameters.put("cl_time", data[7]);
            parameters.put("self_d", data[8]);
            parameters.put("d_partner", data[9]);
            parameters.put("website", data[10]);
            parameters.put("priority", "priority");
            parameters.put("id", data[11]);
            Log.d("self_d", data[8]);
            Log.d("darewro", data[9]);

            bw.write(PostRequestData.getData(parameters));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line, responseData = "";
                while ((line = br.readLine()) != null) {
                    responseData = responseData + line;
                }

                JSONArray jsonArray = new JSONArray(responseData);
                JSONObject obj = jsonArray.getJSONObject(0);
                switch (obj.getString("response")) {
                    case "success":
                        userMessage = "Profile has been updated successfully";
                        String query = "UPDATE restaurant SET name = '" + data[0] + "', sub_title = '" + data[1] + "', street_address = '" +
                                data[2] + "', city_id = " + data[3] + ", phone_no = '" + data[4] + "', mobile_no = '" +
                                data[5] + "', opening_time = '" + data[6] + "', closing_time = '" + data[7] +
                                "', self_delivery = " + data[8] + ", darewro_partner = " + data[9] + ", website = '" +
                                data[10] + "', priority = priority where id = " + data[11];
                        new LocalDataManager(context).updateRow(query);

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Updates updates = new Updates(context, false);
                                updates.execute();
                            }
                        });
                        break;
                    case "error":
                        userMessage = "Error occured while updating profile..";
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                }
            } else {
                throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
            }
            os.close();
            bw.close();
        } catch (MalformedURLException e) {
            userMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (IOException e) {
            userMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (JSONException e) {
            userMessage = "Json error occured while updating profile.";
            e.printStackTrace();
        } catch (Exception e) {
            userMessage = e.getMessage();
        } finally {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                    Toast.makeText(context, userMessage, Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    ((Activity) context).startActivity(((Activity) context).getIntent());

                }
            });
        }
        return null;
    }
}


