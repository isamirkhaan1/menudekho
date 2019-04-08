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
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashMap;

import custom.ExceptionHandling;
import custom.FoodLogo;
import custom.PreferencesFile;
import datalayer.LocalDataManager;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

/**
 * Created by Samir KHan on 9/8/2016.
 */
public class FoodEditableDialog extends DialogFragment {
    Context mContext;
    String mQuery, mName, mPrice, mTime, mDescription, mFlavours, mAvail;
    String[] mData;
    int mRestId;

    public FoodEditableDialog(Context context, String data[]) {
        this.mContext = context;
        this.mData = data;
        mRestId = new PreferencesFile(context).restId();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_edit_food, null);

        final TextView textViewDialogName = (TextView) dialogView.findViewById(R.id.text_dialog_edit_food_name);
        final TextView textViewDialogPrice = (TextView) dialogView.findViewById(R.id.text_dialog_edit_food_price);
        final Spinner spinnerImageName = (Spinner) dialogView.findViewById(R.id.spin_dialog_edit_food_image);
        final TextView textViewDialogTime = (TextView) dialogView.findViewById(R.id.text_dialog_edit_food_time);
        final TextView textViewDialogDescription = (TextView) dialogView.findViewById(R.id.text_dialog_edit_food_description);
        final TextView textViewDialogFlavours = (TextView) dialogView.findViewById(R.id.text_dialog_edit_food_flavours);
        final CheckBox checkBoxDialogAvail = (CheckBox) dialogView.findViewById(R.id.check_dialog_edit_food_avail);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(((Activity) mContext), android.R.layout.simple_spinner_item,
                FoodLogo.foodArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImageName.setAdapter(adapter);
        textViewDialogName.setText(mData[0]);
        textViewDialogPrice.setText(mData[1]);
        textViewDialogTime.setText(mData[2]);
        textViewDialogDescription.setText(mData[3]);
        textViewDialogFlavours.setText(mData[4]);
        if (mData[5] == "YES") {
            checkBoxDialogAvail.setChecked(true);
        } else {
            checkBoxDialogAvail.setChecked(false);
        }
        int i = ((ArrayAdapter)spinnerImageName.getAdapter())
                .getPosition(mData[6]);
        spinnerImageName.setSelection(i);
        final String oldName = textViewDialogName.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        builder.setNeutralButton(R.string.action_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

      /*  builder.setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(mContext);
                alertDelete.setTitle(R.string.title_delete);
                alertDelete.setMessage("\n Do you want to delete the food item? \n \n");
                alertDelete.setNeutralButton(R.string.action_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogDelete, int which) {
                        dialogDelete.dismiss();
                    }
                });
                alertDelete.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogDelete, int which) {
                        mQuery = "DELETE FROM food WHERE name = '" + textViewDialogName.getText().toString().trim() +
                                "' AND restaurant_id = " + mRestId;
                        Boolean isUpdated = LocalDataManager.updateRow(mContext, mQuery);
                        if (isUpdated) {
                            Toast.makeText(mContext, "The food item has been deleted.", Toast.LENGTH_SHORT).show();
                            ((Activity) mContext).finish();
                            ((Activity) mContext).startActivity(((Activity) mContext).getIntent());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(mContext, "Sorry, couldn't Delete the food item.", Toast.LENGTH_SHORT).show();
                        }
                        dialogDelete.dismiss();
                    }
                });
                alertDelete.show();
            }
        });*/

        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = textViewDialogName.getText().toString().trim();
                        String price = textViewDialogPrice.getText().toString().trim();
                        String imageName = spinnerImageName.getSelectedItem().toString();
                        Boolean avail = checkBoxDialogAvail.isChecked();
                        String time = textViewDialogTime.getText().toString().trim();
                        String description = textViewDialogDescription.getText().toString().trim();
                        String flavours = textViewDialogFlavours.getText().toString().trim();

                        PromptDialog promptDialog;
                        if (name.length() < 3) {
                            promptDialog = new PromptDialog(mContext, "Food Name", "Food Name must be larger than 3 characters.", R.string.action_got_it);
                            promptDialog.show(((Activity) mContext).getFragmentManager(), "eidt_food");
                            return;
                        }
                        if (price.length() < 1) {
                            promptDialog = new PromptDialog(mContext, "Food Price", "Food Name cannot be empty", R.string.action_got_it);
                            promptDialog.show(((Activity) mContext).getFragmentManager(), "edit_food");
                            return;
                        }
                        if (time.length() < 1) {
                            time = "00";
                        }
                        if (description.length() == 0) {
                            time = "Not Provided";
                        }
                        if (flavours.length() == 0) {
                            time = "Not Provided";
                        }
                      String  query = "SELECT id FROM food where name = '"+ oldName
                              +"' and rest_id = "+mRestId;
                        String id = new LocalDataManager(mContext).getSingleColumn(query).get(0);

                        String[] params = new String[]{id, name, price, description, flavours, time,
                                ((avail) ? "1" : "0"), imageName};
                        UpdateFoodService service = new UpdateFoodService(mContext, params);
                        service.execute();
                    }
                }

        );
        return builder.create();
    }

}

class UpdateFoodService extends AsyncTask {
    Context mContext;
    String[] mData;
    String userMessage;
    ProgressDialog progressDialog;

    public UpdateFoodService(Context context, String[] data) {
        this.mContext = context;
        this.mData = data;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Updating food..");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = UrlString.RootUrl() + "food.php";
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

            parameters.put("action", "UPDATE");
            parameters.put("id", mData[0]);
            parameters.put("name", mData[1]);
            parameters.put("price", mData[2]);
            parameters.put("desc", mData[3]);
            parameters.put("flavours", mData[4]);
            parameters.put("min_time", mData[5]);
            parameters.put("avail", mData[6]);
            parameters.put("img", mData[7]);


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
                        userMessage = "Food has been updated successfully";
                        LocalDataManager manager = new LocalDataManager(mContext);
                        String query = "UPDATE food SET name = '" + mData[1] + "', price = " + mData[2] + ", descrip = '" + mData[3] +
                                "', flavours = '" + mData[4] + "', min_time = " + mData[5] + ", available = " +
                                mData[6] + ", img = '" + mData[7] + "' WHERE id = "+mData[0];
                        new LocalDataManager(mContext).updateRow(query);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Updates updates = new Updates(mContext, false);
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
            userMessage = "Json error occured while updating food.";
            e.printStackTrace();
        } catch (Exception e) {
            userMessage = e.getMessage();
        } finally {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressDialog.hide();
                    Toast.makeText(mContext, userMessage, Toast.LENGTH_SHORT).show();
                    ((Activity) mContext).finish();
                    ((Activity) mContext).startActivity(((Activity) mContext).getIntent());

                }
            });
        }
        return null;
    }
}