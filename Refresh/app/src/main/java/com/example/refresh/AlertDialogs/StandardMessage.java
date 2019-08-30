package com.example.refresh.AlertDialogs;
/*
Description:
    Purpose of this class is to create a simple AlertDialog with a title and message.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Context;
import android.support.v7.app.AlertDialog;

public class StandardMessage {

    /*
    private instance variables
     */
    private Context context;

    /*
    constructor that takes in a context
     */
    public StandardMessage(Context context){
        this.context = context;
    }

    /*
    creates dialog given a title and message
     */
    public AlertDialog.Builder buildStandardMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }
}
