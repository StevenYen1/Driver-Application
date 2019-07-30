package com.example.refresh.AlertDialogs;
/*
Description:
    Class designed to create a four function custom AlertDialog.

Specific Features:
    Done Scanning -> move to scannedItems.class
    Continue Scanning -> stay on the current Activity
    View Orders -> Create a popup that shows all orders and their current status
    Exit -> Go back to Manage Route Menu and discard all unsigned scans.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.refresh.DatabaseHelper;
import com.example.refresh.Menu;
import com.example.refresh.R;
import com.example.refresh.scannedItems;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.COL_STATUS;
import static com.example.refresh.Delivery_Item.INCOMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class ScannerMenu extends Application {

    /*
    private instance variables
     */
    private Context context;
    private String scannerType;

    /*
    Constructor
     */
    public ScannerMenu(Context context, String scannerType){
        this.context = context;
        this.scannerType = scannerType;
    }

    /*
    Public method that actually builds the AlertDialog
     */
    public void createDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.scanner_done, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        createButtons(view, dialog);
        dialog.show();
    }

    /*
    Creates and assigns values to all four buttons of the layout.
     */
    private void createButtons(View view, AlertDialog alert){
        Handler mHandler = new Handler();

        //Exit Button - End Scanning Session
        FancyButton exit = view.findViewById(R.id.Exit);
        exit.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> discardScansWarning("This will discard all saved scans. Do you still wish to continue?",
                    (dialog, which) -> openMenu());
            mHandler.postDelayed(mUpdateTimeTask, 250);
        });

        //Orders Button - View all open orders
        FancyButton orders = view.findViewById(R.id.orders);
        orders.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> {
                RouteStatus routeStatus = new RouteStatus(context);
                routeStatus.viewRouteStatus();
            };
            mHandler.postDelayed(mUpdateTimeTask, 250);
        });

        //Done Button - Moves on to scannedItems
        FancyButton done = view.findViewById(R.id.Done);
        done.setOnClickListener(v -> openScannedItems());

        //Continue Button - Continue Scanning
        FancyButton continueB = view.findViewById(R.id.Continue);
        continueB.setOnClickListener(v -> alert.dismiss());
    }

    /*
    Alert Message to warn the user that his scans will be invalidated
     */
    private void discardScansWarning(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /*
    Discards all current scans (marks them all incomplete).
     */
    private void discardScans(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor queryResult = databaseHelper.queryAllOrders();
        if(queryResult.getCount() == 0){
            return;
        }
        while(queryResult.moveToNext()){
            String id = queryResult.getString(COL_ORDERNUMBER);
            int status = queryResult.getInt(COL_STATUS);
            if(status == SCANNED || status == SELECTED){
                databaseHelper.updateStatus(id, INCOMPLETE);
            }
        }
    }

    /*
    Opens Menu Page.
     */
    private void openMenu(){
        Intent intent = new Intent(context, Menu.class);
        discardScans();
        context.startActivity(intent);
    }
    /*
    Opens scannedItems Page.
     */
    private void openScannedItems(){
        Intent intent = new Intent(context, scannedItems.class);
        intent.putExtra("previousActivity", scannerType);
        context.startActivity(intent);
    }
}
