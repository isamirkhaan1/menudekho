package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import custom.ExceptionHandling;
import custom.PreferencesFile;
import datalayer.LocalDataManager;
import datalayer.PostRequestData;
import datalayer.UrlString;

public class RestaurantLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_login);
        //getSupportActionBar().hide();

        final int restId = getIntent().getIntExtra("restId", 0);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Allura-Regular.ttf");

        final TextView textViewForgotHeader = (TextView) findViewById(R.id.text_activity_restaurant_login_header);
        final EditText editTextUsername = (EditText) findViewById(R.id.editText_activity_restaurant_login_username);
        final EditText editTextPassword = (EditText) findViewById(R.id.editText_activity_restaurant_login_password);
        final TextView textViewForgotPassword = (TextView) findViewById(R.id.text_activity_restaurant_login_forgetpassword);
        final Button btnLogin = (Button) findViewById(R.id.btn_activity_restaurant_login_login);

        String query = "SELECT name FROM restaurant WHERE id = "+ restId;
        textViewForgotHeader.setText(new LocalDataManager(this).getSingleColumn(query).get(0));
        //textViewForgotHeader.setTypeface(custom_font);

        /* Login Button action Listener..*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                RestLoginService request = new RestLoginService(RestaurantLoginActivity.this,
                        restId, username, password);
                request.execute();
            }
        });

        /* ForgotPassword Text actionListener.. */
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantLoginActivity.this);
                builder.setTitle("Password Recovery");
                builder.setMessage("For security reasons the password updating/changing permission isn't granted to users. \n" +
                        "As instructed please inform The menuDekho team to recover your password. \n\n" +
                        "Thanks for collaboration");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu_with_search, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

class RestLoginService extends AsyncTask {

    private Context mContext;
    private ProgressDialog progressDialog;
    int mRestId;
    String mUsername, mPassword, mUserMessage;

    public RestLoginService(Context context, int restId, String username, String password) {

        this.mContext = context;

        this.mRestId = restId;
        this.mUsername = username;
        this.mPassword = password;
    }

    @Override
    protected void onPreExecute() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            public void run() {

                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Logging in..");
                progressDialog.show();
            }
        });
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = UrlString.RootUrl() + "rest_login.php";
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

            parameters.put("username", mUsername);
            parameters.put("password", mPassword);
            parameters.put("rest_id", mRestId + "");
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
                    case "success": {
                        mUserMessage = "You have been logged in, successfully"; PreferencesFile file = new PreferencesFile(mContext);
                        file.setRestId(mRestId);
                        file.setRestUsername(mUsername);
                        file.setRestPassword(mPassword);

                        Intent intent = new Intent(((Activity) mContext), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                    }
                        break;
                    case "error":
                        mUserMessage = "Error occured while logging you in..";
                        break;
                    case "incorrect":
                        mUserMessage = "Incorrect username or password";
                        break;
                }
            } else {
                throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
            }
            os.close();
            bw.close();


        } catch (MalformedURLException e) {
            mUserMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (IOException e) {
            mUserMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (JSONException e) {
            mUserMessage = "Json error occured while logging you in..";
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                    Toast.makeText(mContext, mUserMessage, Toast.LENGTH_SHORT).show();

                }
            });
        }
        return null;
    }
}