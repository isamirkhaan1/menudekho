package com.samirkhan.apps.citymenu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import java.util.ArrayList;
import java.util.HashMap;

import custom.ExceptionHandling;
import datalayer.LocalDataManager;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

public class AddReviewActivity extends AppCompatActivity {
    int mRestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        //getSupportActionBar().hide();

        mRestId = getIntent().getIntExtra("restId", 0);
        ArrayList<Object[]> data = new LocalDataManager(this).getRestaurant("WHERE r.id = "+mRestId);
        if(data == null || data.size()<1){
            Snackbar.make(findViewById(android.R.id.content), "No RestID", Snackbar.LENGTH_SHORT).show();
            return;
        }
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar_activity_add_review);
        final TextView textViewRestName = (TextView) findViewById(R.id.text_activity_add_review_rest_name);
        final TextView textViewRestAddress = (TextView) findViewById(R.id.text_activity_add_review_rest_address);
        final EditText editTextContact = (EditText) findViewById(R.id.editText_activity_add_review_contact);
        final EditText editTextRemarks = (EditText) findViewById(R.id.editText_activity_add_review_review);
        final EditText editTextName = (EditText) findViewById(R.id.editText_activity_add_review_name);
        final Button btnSubmit = (Button) findViewById(R.id.btn_restaurantreview_submit);

        textViewRestName.setText((String)data.get(0)[1]);
        textViewRestAddress.setText((String)data.get(0)[3]);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stars = ((int) ratingBar.getRating()) + "";
                String remarks = editTextRemarks.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String contact = editTextContact.getText().toString().trim();

                String userMessage = null;
                if(remarks.length()<8){
                    userMessage = "Remarks must be greater than 8 characters.";
                }
                else if(name.length()<3){
                    userMessage = "Please enter a valid name";
                } else if(contact.length()<7){
                    userMessage = "Please enter a valid phone no.";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddReviewActivity.this);
                builder.setTitle("Error");
                if(userMessage != null){
                    builder.setMessage(userMessage);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                    return;
                }

                AddReviewService addReview;
                addReview = new AddReviewService(AddReviewActivity.this, stars, remarks, name,
                        contact, mRestId);
                addReview.execute();
                finish();
            }
        });

    }
}

class AddReviewService extends AsyncTask {

    Context mContext;
    ProgressDialog progressDialog;

    int mRestId;
    String mStars, mReviews, mName, mContactNo, mUserMessage;

    public AddReviewService(Context context, String stars, String reviews, String name, String contactNo, int restId) {
        this.mContext = context;
        progressDialog = new ProgressDialog(context);

        this.mStars = stars;
        this.mReviews = reviews;
        this.mName = name;
        this.mContactNo = contactNo;
        this.mRestId = restId;
    }

    @Override
    protected void onPreExecute() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Adding review..");
                progressDialog.show();
            }
        });
        super.onPreExecute();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Object doInBackground(Object[] params) {

        URL url;
        HttpURLConnection connection;
        String urlString = UrlString.RootUrl() + "review.php";
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
            parameters.put("name", mName);
            parameters.put("contact", mContactNo);
            parameters.put("remarks", mReviews);
            parameters.put("stars", mStars);
            parameters.put("r_id", mRestId + "");
            bw.write(PostRequestData.getData(parameters));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line, data = "";
                while ((line = br.readLine()) != null) {
                    data = data + line;
                }
               // JSONArray array = new JSONArray(br);
                JSONArray jsonArray = new JSONArray(data);
                JSONObject obj = jsonArray.getJSONObject(0);
                switch (obj.getString("response")) {
                    case "success":
                        mUserMessage = "Review has been added successfully";
                       /* Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                "Your review has been added..", Snackbar.LENGTH_SHORT).show();*/

                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Updates updates = new Updates(mContext, false);
                                updates.execute();
                            }
                        });
                        break;
                    default:

                        mUserMessage = "Error occured while adding review..";
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                }
            } else {
                throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
            }
            os.close();
            bw.close();
        } catch (MalformedURLException e) {
            mUserMessage = "Please check the connectivity..";
        } catch (IOException e) {
            mUserMessage = "Please check the connectivity..";
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            mUserMessage = e.getMessage();
        }
        finally {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                    if(mUserMessage != null || mUserMessage.length()>0)
                    Toast.makeText(mContext, mUserMessage, Toast.LENGTH_SHORT).show();
                    ((Activity) mContext).finish();
                   // ((Activity) mContext).startActivity(((Activity) mContext).getIntent());

                }
            });
        }
        return null;
    }
}