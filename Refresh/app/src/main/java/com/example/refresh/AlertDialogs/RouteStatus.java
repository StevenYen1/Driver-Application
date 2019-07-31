package com.example.refresh.AlertDialogs;
/*
Description:
    Class designed to display data of all current orders in a basic AlertDialog.

Specific Features:
    Creates RouteStatus object.
    Grabs entry data from the database and appends all data to a single String.
    Displays single String as body of the AlertDialog.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;

import com.example.refresh.DatabaseHelper.DatabaseHelper;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_STATUS;
import static com.example.refresh.ItemModel.PackageModel.COMPLETE;
import static com.example.refresh.ItemModel.PackageModel.FAIL_SEND;
import static com.example.refresh.ItemModel.PackageModel.SCANNED;
import static com.example.refresh.ItemModel.PackageModel.SELECTED;

public class RouteStatus {

    /*
    private instance variables
     */
    private Context context;

    /*
    Constructor for this class
     */
    public RouteStatus(Context context){
        this.context = context;
    }

    /*
    Method that can be publicly called to execute the crreation of a Route Status Alert Dialog.
     */
    public void viewRouteStatus(){
        Cursor queryRequest = queryAll();
        String body = getBody(queryRequest);
        createStatusDialog(body);
    }

    /*
    Creates an AlertDialog that displays a body String.
     */
    private void createStatusDialog(String body){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Order Status Information:");
        alertBuilder.setNeutralButton("Close", (dialog, which) -> dialog.dismiss());
        alertBuilder.setMessage(body);
        alertBuilder.show();
    }

    /*
    Unpacks all of the data from the "order table" using the Cursor object.
    Compiles the data all into a single String and returns that String.
     */
    private String getBody(Cursor queryRequest){
        StringBuffer buffer = new StringBuffer();

        while (queryRequest.moveToNext()) {
            int status = queryRequest.getInt(COL_STATUS);
            if(status == COMPLETE || status == FAIL_SEND){
                buffer.append("Current Status: COMPLETED\n");
            }
            else if(status == SCANNED){
                buffer.append("Current Status: SCANNED\n");
            }
            else if(status == SELECTED){
                buffer.append("Current Status: SELECTED\n");
            }
            else{
                buffer.append("Current Status: INCOMPLETE\n");
            }
            buffer.append("--------------------------------------------------------\n");
            buffer.append("Order number: ").append(queryRequest.getString(COL_ORDERNUMBER)).append("\n");
            buffer.append("Address: ").append(queryRequest.getString(COL_ADDRESS)).append("\n");
            buffer.append("Recipient: ").append(queryRequest.getString(COL_RECIPIENT)).append("\n");
            buffer.append("Item: ").append(queryRequest.getString(COL_ITEM)).append("\n");
            buffer.append("Quantity: ").append(queryRequest.getInt(COL_QUANTITY)).append("\n");
            buffer.append("Carton Number: ").append(queryRequest.getString(COL_CARTONNUMBER)).append("\n");
            buffer.append("--------------------------------------------------------\n");
            buffer.append("\n\n");
        }
        return buffer.toString();
    }

    /*
    Returns a Cursor object that points to all entries of the "order table".
     */
    private Cursor queryAll(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.queryAllOrders();
    }
}
