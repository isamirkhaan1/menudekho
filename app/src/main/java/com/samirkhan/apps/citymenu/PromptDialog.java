package com.samirkhan.apps.citymenu;


import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Samir KHan on 8/20/2016.
 */
public class PromptDialog extends DialogFragment {

    Context mContext;
    String mTitle;
    String mMessage;
    int btnTitle;
    public PromptDialog(Context context, String title, String message, int btnTitle){
        this.mContext = context;
        this.mTitle = title;
        this.mMessage = message;
        this.btnTitle = btnTitle;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mTitle);
        builder.setMessage(mMessage);
        builder.setNeutralButton(btnTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
