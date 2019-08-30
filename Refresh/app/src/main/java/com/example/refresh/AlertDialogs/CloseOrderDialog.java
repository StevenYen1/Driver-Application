package com.example.refresh.AlertDialogs;
/*
Description:
    The purpose of this class is to create an AlertDialog for the process of closing orders.

Specific Features:
    Creates CloseOrderDialog
    Prompts for user Reasoning as to why they are closing the order.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.MainMenu.Menu;
import com.example.refresh.R;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;

public class CloseOrderDialog {

    /*
    private instance variables
     */
    private Context context;
    private ArrayList<String> list;

    /*
    constructor that takes in a context and ArrayList of String
     */
    public CloseOrderDialog(Context context, ArrayList<String> list){
        this.context = context;
        this.list = list;
    }

    /*
    Creates the actual dialog
     */
    public void createReasoningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.reasoning_for_action_layout, null);

        setupDialogTitle(view);
        FancyButton reasoningButton = createSubmitButton(view);
        createTextField(view, reasoningButton);

        builder.setView(view)
                .create()
                .show();
    }

    /*
    Sets dialog title
     */
    private void setupDialogTitle(View view){
        TextView reasoningTitle = view.findViewById(R.id.reasoning_title);
        reasoningTitle.setText("You are about to close an order(s): ");
        reasoningTitle.setTextColor(Color.WHITE);
        reasoningTitle.setBackgroundColor(context.getResources().getColor(R.color.blue));
    }

    /*
    Creates a textField that listens for user input.
     */
    private void createTextField(View view, FancyButton reasoningButton){
        EditText reasoningView = view.findViewById(R.id.reasoning_reason);
        reasoningView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reasoningButton.setEnabled(true);
                if(s.toString().equals("")){
                    reasoningButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /*
    Initializes button that allows user to submit their reasoning for closing the order.
     */
    private FancyButton createSubmitButton(View view){
        FancyButton reasoningButton = view.findViewById(R.id.reasoning_button);
        reasoningButton.setText("Close");
        reasoningButton.setEnabled(false);
        reasoningButton.setTextColor(Color.WHITE);
        reasoningButton.setBackgroundColor(context.getResources().getColor(R.color.blue));
        reasoningButton.setFocusBackgroundColor(context.getResources().getColor(R.color.skyblue));
        reasoningButton.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            ArrayList<String> orderNumbers = queryOrders(databaseHelper);
            for(String x : list){
                databaseHelper.closeOrder(x, orderNumbers.indexOf(x));
            }
            Intent intent = new Intent(context, Menu.class);
            context.startActivity(intent);
        });
        return reasoningButton;
    }

    /*
    Queries the database and returns the orders in the "order table"
     */
    private ArrayList<String> queryOrders(DatabaseHelper databaseHelper){
        ArrayList<String> orders = new ArrayList<>();
        Cursor queryResult  = databaseHelper.queryAllOrders();
        while(queryResult.moveToNext()){
            orders.add(queryResult.getString(COL_ORDERNUMBER));
        }
        return orders;
    }

}
