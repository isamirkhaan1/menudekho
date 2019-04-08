package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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

import custom.UserInformation;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

/**
 * Created by Samir KHan on 9/25/2016.
 */
public class ReferenceDialog extends DialogFragment implements View.OnClickListener {

    Context mContext;
    TextView textViewInstruction;
    EditText editTextEmail;
    RadioButton radioButtonFacebook, radioButtonFriend, radioButtonOther;

    public ReferenceDialog(Context context) {
        this.mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_ref_1, null);

        textViewInstruction = (TextView) v.findViewById(R.id.dialog_ref_text_inst);
        editTextEmail = (EditText) v.findViewById(R.id.dialog_ref_text_email);
        radioButtonFacebook = (RadioButton) v.findViewById(R.id.dialog_ref_radio_facebook);
        radioButtonFriend = (RadioButton) v.findViewById(R.id.dialog_ref_radio_friend);
        radioButtonOther = (RadioButton) v.findViewById(R.id.dialog_ref_radio_other);

        radioButtonFacebook.setOnClickListener(this);
        radioButtonFriend.setOnClickListener(this);
        radioButtonOther.setOnClickListener(this);


        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ref = null;
                String learnt_through = null;

                if (radioButtonFacebook.isChecked()) {
                    learnt_through = "facebook";
                } else if (radioButtonFriend.isChecked()) {
                    learnt_through = "friend";
                    if (editTextEmail.getText().toString().trim().length() > 0) {
                        ref = editTextEmail.getText().toString().trim();

                        // check if user is entering his own email-id as a ref..
                        if (ref.equals(new UserInformation(mContext).emailAdd())) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                            builder1.setTitle("Error");
                            builder1.setMessage("Sorry, You cannot refrence your own.");
                            builder1.show();
                            builder1.setPositiveButton(R.string.action_got_it, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }

                    } else
                        ref = "nill";

                } else if (radioButtonOther.isChecked()) {
                    learnt_through = "other";
                }

                AddReferenceInfoService service = new
                        AddReferenceInfoService(mContext, ref, learnt_through);
                service.execute();

            }
        });
        builder.setView(v);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ref_radio_friend:
                editTextEmail.setVisibility(View.VISIBLE);
                textViewInstruction.setVisibility(View.VISIBLE);
                break;
            default:
                editTextEmail.setVisibility(View.INVISIBLE);
                textViewInstruction.setVisibility(View.INVISIBLE);
                break;
        }
    }
}


class AddReferenceInfoService extends AsyncTask {

    Context mContext;
    ProgressDialog progressDialog;
    String mMacAdd, mRefBy, mLearntThrough, mUserMessage;

    public AddReferenceInfoService(Context context, String refBy, String learntThrough) {
        this.mContext = context;
        this.mRefBy = refBy;
        this.mLearntThrough = learntThrough;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("saving response..");
                progressDialog.show();
            }
        });
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {

        String urlString = UrlString.RootUrl() + "add_user.php";
        URL url;
        HttpURLConnection connection;

        try {

            this.mMacAdd = new UserInformation(mContext).macAddress();
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("mac_address", mMacAdd);
            parameters.put("learnt_through", mLearntThrough);
            parameters.put("ref_by", mRefBy);

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
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                switch (jsonObject.getString("response")) {
                    case "success":
                        mUserMessage = "Thanks for downloading The menuDekho.";
                        break;
                    case "re-install":
                        mUserMessage = "It seems like you have already installed The menuDekho, so, we are not counting you as a" +
                                " favour for your friend.";
                        break;
                    default:
                        mUserMessage = jsonObject.getString("response");
                        break;
                }

                os.close();
                bw.close();
            }

        } catch (MalformedURLException e) {
            mUserMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (IOException e) {
            mUserMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (JSONException e) {
            mUserMessage = "Json error occured while updating category.";
            e.printStackTrace();
        } catch (Exception e) {
            mUserMessage = e.getMessage();
        } finally {

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.hide();
                    Toast.makeText(mContext, mUserMessage, Toast.LENGTH_LONG).show();

                    Updates updates = new Updates(mContext, true);
                    updates.execute();
                }
            });
        }
        return null;
    }
}
