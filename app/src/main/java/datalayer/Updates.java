package datalayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.samirkhan.apps.citymenu.CityListActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import custom.ExceptionHandling;
import custom.PreferencesFile;

/**
 * Created by Samir KHan on 8/29/2016.
 */
public class Updates extends AsyncTask {

    Context context;
    ProgressDialog progressDialog;
    boolean firstTime;

    HashMap<String, String> parameters;

    public Updates(Context context, boolean firstTime) {
        this.context = context;
        this.firstTime = firstTime;
        parameters = new HashMap<>();
    }

    @Override
    protected void onPreExecute() {
        if (firstTime) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Loading data..");
                    progressDialog.show();
                }
            });
        }
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        final HttpURLConnection connection;

        try {
            String dataUrl = UrlString.RootUrl() + "get_data.php";
            url = new URL(dataUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);

            country(dataUrl);
            state(dataUrl);
            city(dataUrl);
            restaurant(dataUrl);
            category(dataUrl);
            food(dataUrl);
            review(dataUrl);

            PreferencesFile preferencesFile = new PreferencesFile(context);
            preferencesFile.setFirstTime(false);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            preferencesFile.setLastUpdates(format.format(calendar.getTime()));

            /* show toast.. */
            if (firstTime) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Database has been updated..", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, CityListActivity.class);
                        intent.putExtra("setDefaultCity", true);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).finish();
                    }
                });
            }

        } catch (MalformedURLException e) {
            Log.d("Updates", "MalformedURLException :" + e.getMessage());
            if (firstTime)
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Please check connectivity..", Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (IOException e) {
            Log.d("Updates", "IOException :" + e.getMessage());
            if (firstTime)
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Server error, please login again", Toast.LENGTH_SHORT).show();
                    }
                });

        } catch (Exception e) {
            Log.d("Updates", e.getMessage());
            if (firstTime)
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Server not responding, please try again..", Toast.LENGTH_SHORT).show();

                    }
                });
        } finally {
            if (firstTime && progressDialog.isShowing())
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  progressDialog.cancel();
                    }
                });
        }
        return null;
    }

    public void country(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;
        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


        parameters.put("table", "country");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("country", "modified_date");
            parameters.put("where", " WHERE modified_date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.country(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }

        os.close();
        bw.close();
    }

    public void state(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;
        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        parameters.put("table", "state");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("state", "modified_date");
            parameters.put("where", " WHERE modified_date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.state(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }
        os.close();
        bw.close();
    }

    public void city(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;

        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        parameters.put("table", "city");
        parameters.put("select", "*");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("city", "modified_date");
            parameters.put("where", " WHERE modified_date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.city(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }
        os.close();
        bw.close();
    }

    public void restaurant(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;
        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        parameters.put("table", "restaurant");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("restaurant", "modified_date");
            parameters.put("where", " WHERE modified_date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.restaurant(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }
        os.close();
        bw.close();
    }

    public void category(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;
        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        parameters.put("table", "category");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("category", "modified_date");
            parameters.put("where", " WHERE modified_date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.category(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }
        os.close();
        bw.close();
    }

    public void food(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;
        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        parameters.put("table", "food");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("food", "modified_date");
            parameters.put("where", " WHERE modified_date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.food(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }
        os.close();
        bw.close();
    }

    public void review(String dataUrl) throws IOException, Exception {
        URL url;
        HttpURLConnection connection;
        url = new URL(dataUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        parameters.put("table", "review");
        parameters.put("select", "*");
        if (!firstTime) {
            String modifiedDate = new LocalDataManager(context).modifiedDate("review", "date");
            parameters.put("where", " WHERE date > '" + modifiedDate + "'");
        }
        bw.write(PostRequestData.getData(parameters));
        bw.flush();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            UpdatesManager updatesManager = new UpdatesManager(context);
            updatesManager.review(br);
        } else {
            throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
        }
        os.close();
        bw.close();
    }
}
