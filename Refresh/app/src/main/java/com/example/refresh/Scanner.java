package com.example.refresh;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Camera;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.example.refresh.Delivery_Item.COMPLETE;
import static com.example.refresh.Delivery_Item.INCOMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class Scanner extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    String orders_status;
    DatabaseHelper myDb;
    String Barcode="";
    TextView results;
    EditText input;
    String details ="Nothing scanned yet";
    String id ="No id yet";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
        setContentView(R.layout.activity_scanner);
        results = findViewById(R.id.scan_results);
        setOrderInformation();
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
        TextView textView = findViewById(R.id.scan_results);
        AlertDialog.Builder builder = new AlertDialog.Builder(Scanner.this);
        builder.setCancelable(true);
        builder.setMessage(details);
        builder.setPositiveButton("Undo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unscanItem(id);
            }
        });
        builder.show();
    }

    public void setOrderInformation(){
        Cursor rawOrders = myDb.getAllData();
        if(rawOrders.getCount() == 0){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (rawOrders.moveToNext()) {
            int status = rawOrders.getInt(4);
            if(status == COMPLETE){
                buffer.append("Current Status: COMPLETED\n");
            }
            else if(status == SCANNED){
                buffer.append("Current Status: SCANNED\n");
            }
            else{
                buffer.append("Current Status: INCOMPLETE\n");
            }
            buffer.append("--------------------------------------------------------\n");
            buffer.append("Order number: " + rawOrders.getString(0)+"\n");
            buffer.append("Address: " + rawOrders.getString(1)+"\n");
            buffer.append("Recipient: " + rawOrders.getString(2)+"\n");
            buffer.append("Item: " + rawOrders.getString(3)+"\n");
            buffer.append("--------------------------------------------------------\n");
            buffer.append("\n");
            buffer.append("\n");
        }
        orders_status = buffer.toString();
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.scanner_done, null);

        Button exit = mView.findViewById(R.id.Exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertMessage("This will discard all saved scans. Do you still wish to continue?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goBack();
                    }
                });
            }
        });
//
        Button orders = mView.findViewById(R.id.orders);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAll();
            }
        });

        Button done = mView.findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScannedItems();
            }
        });
        builder.setView(mView);
        final AlertDialog dialog = builder.show();

        Button continueB = mView.findViewById(R.id.Continue);
        continueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void openScannedItems(){
        Intent intent = new Intent(this, scannedItems.class);
        startActivity(intent);
    }

    public void goBack(){
        Intent intent = new Intent(this, RecyclerView.class);
        Cursor rawOrders = myDb.getAllData();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String id = rawOrders.getString(0);
            int status = rawOrders.getInt(4);
            if(status == SCANNED){
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
            if(status == COMPLETE){
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
            if(status == COMPLETE){
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
        new AlertDialog.Builder(Scanner.this)
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
//        String scanResult = result.getText();
        int status = myDb.getStatus(scanResult);

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(status == COMPLETE || status == SCANNED || status == SELECTED){
            results.setTextColor(Color.parseColor("#fa5555"));
            results.setText("ALREADY SCANNED");
            id = scanResult;
            details = viewInstance(scanResult);

//            builder.setTitle("Error: ");
//            builder.setMessage("You have already scanned that item. \nWould you like to continue scanning?");
//            builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which){
//                    openScannedItems();
//                }
//            });
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which){
//                    dialog.dismiss();
//                }
//            });

        }
        else if(status == INCOMPLETE){
            myDb.updateStatus(scanResult, SCANNED);
            results.setTextColor(Color.parseColor("#1c9c2b"));
            results.setText("SUCCESS");
            id = scanResult;
            details = viewInstance(scanResult);
//            builder.setTitle("Order Number: " + scanResult);
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which){
//                    dialog.dismiss();
//                }
//            });
//            builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which){
//                    openScannedItems();
//                }
//            });
//            builder.setMessage("Order Completed! Continue Scanning?");
        }
        else{
            results.setTextColor(Color.parseColor("#fa5555"));
            results.setText("INVALID ITEM");
            id = "Not a valid ID";
            details = "There are no details for an invalid item.";
//            builder.setTitle("Error: ");
//            builder.setMessage("Item is not on the order list. \nPlease scan again.");
//            builder.setNeutralButton("Rescan", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which){
//                    dialog.cancel();
//                }
//            });

        }
//        AlertDialog alert = builder.create();
//        alert.show();
    }
}
