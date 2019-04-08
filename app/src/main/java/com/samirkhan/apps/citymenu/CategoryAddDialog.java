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
import android.widget.EditText;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import custom.ExceptionHandling;
import custom.PreferencesFile;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

/**
 * Created by Samir KHan on 8/25/2016.
 */
public class CategoryAddDialog extends DialogFragment {

    Context context;

    public CategoryAddDialog(Context context) {
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_add_category, null);
        final TextView editTextName = (EditText) view.findViewById(R.id.text_dialog_add_category_name);
        final EditText editTextDesc = (EditText) view.findViewById(R.id.text_dialog_add_category_desc);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String restId = new PreferencesFile(context).restId() + "";
                AddCategoryService addCategoryService = new AddCategoryService(context,
                        restId, editTextName.getText().toString().trim(),
                        editTextDesc.getText().toString().trim());
                addCategoryService.execute();
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

class AddCategoryService extends AsyncTask {

    Context context;
    String restId, catName, catDesc, userMessage;
    ProgressDialog progressDialog;

    public AddCategoryService(Context context, String restId, String catName, String catDesc) {
        this.context = context;
        this.restId = restId;
        this.catName = catName;
        this.catDesc = catDesc;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Adding category..");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String urlString = UrlString.RootUrl() + "category.php";
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
            parameters.put("rest_id", restId);
            parameters.put("name", catName);
            parameters.put("desc", catDesc);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(PostRequestData.getData(parameters));
            writer.flush();

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
                        userMessage = "Category has been added successfully";
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
                        userMessage = "Error occured while adding category..";
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
            userMessage = "Json error occured while adding category.";
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

    private String getParameters(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
