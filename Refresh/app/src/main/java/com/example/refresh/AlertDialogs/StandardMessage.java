package com.example.refresh.AlertDialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class StandardMessage {

    private Context context;

    public StandardMessage(Context context){
        this.context = context;
    }

    public AlertDialog.Builder buildStandardMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }
}
