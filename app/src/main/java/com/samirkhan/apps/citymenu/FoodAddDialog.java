package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import custom.FoodLogo;
import custom.PreferencesFile;
import datalayer.LocalDataManager;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

/**
 * Created by Samir KHan on 8/25/2016.
 */
public class FoodAddDialog extends DialogFragment {

    Context context;
    String query;

    public FoodAddDialog(Context context) {
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_add_food, null);

        final EditText editTextName = (EditText) v.findViewById(R.id.text_dialog_add_food_name);
        final EditText editTextPrice = (EditText) v.findViewById(R.id.text_dialog_add_food_price);
        final EditText editTextDesc = (EditText) v.findViewById(R.id.text_dialog_add_food_description);
        final EditText editTextFlav = (EditText) v.findViewById(R.id.text_dialog_add_food_flavours);
        final EditText editTextTime = (EditText) v.findViewById(R.id.text_dialog_add_food_time);
        final Spinner spinnerPic = (Spinner) v.findViewById(R.id.spin_dialog_add_food_image);
        final Spinner spinnerCat = (Spinner) v.findViewById(R.id.spin_dialog_add_food_cat);
        final CheckBox checkBoxIsAvail = (CheckBox) v.findViewById(R.id.check_dialog_add_food_avail);

        final int restId = (new PreferencesFile(context).restId());
        query = "SELECT name FROM category WHERE rest_id = " + restId;
        ArrayList<String> data = new LocalDataManager(context).getSingleColumn(query);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, FoodLogo.foodArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPic.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v);
        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                query = "SELECT id FROM category WHERE rest_id = " + restId + " AND name = '" +
                        spinnerCat.getSelectedItem().toString().trim() + "'";
                String catId = new LocalDataManager(context).getSingleColumn(query).get(0);

                String[] data = new String[]{editTextName.getText().toString().trim(),
                        editTextPrice.getText().toString().trim(), editTextDesc.getText().toString().trim(),
                        editTextFlav.getText().toString().trim(), editTextTime.getText().toString().trim(),
                        checkBoxIsAvail.isChecked() ? "1" : "0", spinnerPic.getSelectedItem().toString().trim(),
                        catId, restId + ""
                };

                AddFoodService service = new AddFoodService(context, data);
                service.execute();
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.action_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}

class AddFoodService extends AsyncTask {

    Context context;
    String[] mData;
    String userMessage;
    ProgressDialog progressDialog;

    public AddFoodService(Context context, String[] data) {
        this.context = context;
        this.mData = data;
        this.progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Adding food..");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String urlString = UrlString.RootUrl() + "food.php";
        URL url;
        HttpURLConnection connection;
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

            parameters.put("action", "INSERT");
            parameters.put("name", mData[0]);
            parameters.put("price", mData[1]);
            parameters.put("desc", mData[2]);
            parameters.put("flavours", mData[3]);
            parameters.put("min_time", mData[4]);
            parameters.put("avail", mData[5]);
            parameters.put("img", mData[6]);
            parameters.put("cat_id", mData[7]);
            parameters.put("rest_id", mData[8]);

          /*  String[] data = new String[]{editTextName.getText().toString().trim(),
                    editTextPrice.getText().toString().trim(), editTextDesc.getText().toString().trim(),
                    editTextFlav.getText().toString().trim(), editTextTime.getText().toString().trim(),
                    checkBoxIsAvail.isChecked()?"1":"0", spinnerPic.getSelectedItem().toString().trim(),
                    catId, restId+""
            };*/

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
                        userMessage = "Food has been added successfully";
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Updates updates = new Updates(context, false);
                                updates.execute();
                                try {
                                    Thread.sleep(3 * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    case "error":
                        userMessage = "Error occured while adding food..";
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
            userMessage = "Json error occured while adding food.";
            e.printStackTrace();
        } catch (Exception e) {
            userMessage = e.getMessage();
        } finally {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                    if (userMessage != null || userMessage.length() < 0)
                        Toast.makeText(context, userMessage, Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    ((Activity) context).startActivity(((Activity) context).getIntent());

                }
            });
        }
        return null;
    }

}
