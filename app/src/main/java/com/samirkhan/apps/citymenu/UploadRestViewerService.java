package com.samirkhan.apps.citymenu;

import android.content.Context;
import android.os.AsyncTask;

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
import custom.Viewer;
import datalayer.PostRequestData;
import datalayer.UrlString;

/**
 * Created by Samir KHan on 9/27/2016.
 */
public class UploadRestViewerService extends AsyncTask {
    Context mContext;
    String mUserMessage;

    public UploadRestViewerService(Context context) {
        this.mContext = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String urlString = UrlString.RootUrl();

        URL url;
        OutputStream os = null;
        BufferedWriter bw = null;
        HttpURLConnection connection;

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);

            os = connection.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("data", new Viewer(mContext).uploadRestViewer());
            bw.write(PostRequestData.getData(parameters));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line, data = "";
                while ((line = br.readLine()) != null) {
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                JSONObject obj = jsonArray.getJSONObject(0);
                switch (obj.getString("response")) {
                    case "success":

                        Viewer v = new Viewer(mContext);
                        v.deleteRestViewers();
                        break;
                }
            } else {
                throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
