package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.Delivery_Item.COMPLETE;
import static com.example.refresh.Delivery_Item.FAIL_SEND;
import static com.example.refresh.Delivery_Item.INCOMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class External_Scanner extends AppCompatActivity {

    private String orders_status;
    private DatabaseHelper myDb;
    private String Barcode="";
    private TextView results;
    private EditText input;
    private String details ="Nothing scanned yet";
    private String title="No title";
    private String id ="No id yet";
    private FancyButton sideMenu;
    private Handler mHandler = new Handler();
    private boolean canUndo = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
        setContentView(R.layout.activity_external_scanner);
        results = findViewById(R.id.scan_results);
        sideMenu = findViewById(R.id.external_sideMenu);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setInput();


    }

    public void setInput(){
        input = findViewById(R.id.editText);
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN && (keyCode==301 || keyCode == 302 || keyCode == 303)){
                    Barcode = input.getText().toString();
                }
                if(event.getAction()==KeyEvent.ACTION_UP && (keyCode==301 || keyCode == 302 || keyCode == 303)){
                    input.getText().clear();
                    handleResult(Barcode);
                }

                return false;
            }
        });
    }

    public void displayDetails(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(External_Scanner.this);
        View mView = getLayoutInflater().inflate(R.layout.order_details_layout, null);
        TextView title = mView.findViewById(R.id.details_title);
        title.setText(this.title);
        TextView body = mView.findViewById(R.id.details_body);
        body.setText(this.details);
        builder.setCancelable(true);
        if(canUndo){
            builder.setPositiveButton("Undo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unscanItem(id);
                }
            });
        }
        builder.setView(mView);
        builder.show();
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.scanner_done, null);

        FancyButton exit = mView.findViewById(R.id.Exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mUpdateTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        displayAlertMessage("This will discard all saved scans. Do you still wish to continue?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goBack();
                            }
                        });
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });

        FancyButton orders = mView.findViewById(R.id.orders);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mUpdateTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        viewAll();
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });

        FancyButton done = mView.findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScannedItems();
            }
        });
        builder.setView(mView);
        final AlertDialog dialog = builder.show();

        FancyButton continueB = mView.findViewById(R.id.Continue);
        continueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mUpdateTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });

    }



    public void openScannedItems(){
        Intent intent = new Intent(this, scannedItems.class);
        intent.putExtra("previousActivity", "e");
        startActivity(intent);
    }

    public void goBack(){
        Intent intent = new Intent(this, Menu.class);
        Cursor rawOrders = myDb.getAllData();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String id = rawOrders.getString(0);
            int status = rawOrders.getInt(4);
            if(status == SCANNED || status == SELECTED){
                myDb.updateStatus(id, INCOMPLETE);
            }
        }
        startActivity(intent);
    }

    public void viewAll(){
        Cursor rawOrders = myDb.getAllData();
        if(rawOrders.getCount() == 0){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (rawOrders.moveToNext()) {
            int status = rawOrders.getInt(4);
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
            buffer.append("Order number: " + rawOrders.getString(0)+"\n");
            buffer.append("Address: " + rawOrders.getString(1)+"\n");
            buffer.append("Recipient: " + rawOrders.getString(2)+"\n");
            buffer.append("Item: " + rawOrders.getString(3)+"\n");
            buffer.append("Quantity: " + rawOrders.getInt(7)+"\n");
            buffer.append("Carton Number: " + rawOrders.getString(8)+"\n");
            buffer.append("--------------------------------------------------------\n");
            buffer.append("\n");
            buffer.append("\n");
        }
        orders_status = buffer.toString();
        showMessage("Order Information", orders_status);
    }

    public String viewInstance(String id){
        Cursor rawOrders = myDb.getInstance(id);
        if(rawOrders.getCount() == 0){
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        while (rawOrders.moveToNext()) {
            int status = rawOrders.getInt(4);
            if(status == COMPLETE || status == FAIL_SEND){
                title = "Current Status: COMPLETED";
            }
            else if(status == SCANNED){
                title = "Current Status: SCANNED";
            }
            else if(status == SELECTED){
                title = "Current Status: SELECTED";
            }
            else{
                title = "Current Status: INCOMPLETE";
            }
            buffer.append("--------------------------------------------------------\n");
            buffer.append("Order number: " + rawOrders.getString(0)+"\n");
            buffer.append("Address: " + rawOrders.getString(1)+"\n");
            buffer.append("Recipient: " + rawOrders.getString(2)+"\n");
            buffer.append("Item: " + rawOrders.getString(3)+"\n");
            buffer.append("Quantity: " + rawOrders.getInt(7)+"\n");
            buffer.append("Carton Number: " + rawOrders.getString(8)+"\n");
            buffer.append("--------------------------------------------------------\n");
            buffer.append("\n");
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage(message);
        builder.show();
    }


    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(External_Scanner.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void unscanItem(String id){
        myDb.updateStatus(id, INCOMPLETE);
        results.setText("");
        details ="Nothing scanned yet";
        this.id ="No id yet";
    }


    public void handleResult(String scanResult) {
        int status = myDb.getStatus(scanResult);

        if(status == COMPLETE || status == FAIL_SEND){
            results.setBackgroundColor(Color.parseColor("#fa5555"));
            results.setText("COMPLETED ITEM");
            id = scanResult;
            details = viewInstance(scanResult);
            canUndo = false;
        }
        else if(status == SCANNED || status == SELECTED){
            results.setBackgroundColor(Color.parseColor("#fa5555"));
            results.setText("ALREADY SCANNED");
            id = scanResult;
            details = viewInstance(scanResult);
            canUndo = true;

        }
        else if(status == INCOMPLETE){
            myDb.updateStatus(scanResult, SCANNED);
            results.setBackgroundColor(Color.parseColor("#1c9c2b"));
            results.setText("SUCCESS");
            id = scanResult;
            details = viewInstance(scanResult);
            canUndo = true;
        }
        else{
            results.setBackgroundColor(Color.parseColor("#fa5555"));
            results.setText("INVALID ITEM");
            id = "Not a valid ID";
            details = "There are no details for an invalid item.";
            canUndo = false;
        }
    }
}
