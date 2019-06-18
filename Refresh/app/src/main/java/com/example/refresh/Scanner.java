package com.example.refresh;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private ArrayList<String> incompleteOrders = new ArrayList<>();
    private ArrayList<String> completedOrders = new ArrayList<>();
    private ArrayList<String> scannedOrders = new ArrayList<>();
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        incompleteOrders = getIntent().getStringArrayListExtra("remainingOrders");
        completedOrders = getIntent().getStringArrayListExtra("completedOrders");
        if(getIntent().getStringArrayListExtra("scannedOrders")!=null){
            scannedOrders = getIntent().getStringArrayListExtra("scannedOrders");
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(Scanner.this, "Permission is granted!", Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }
    }

    @Override
    public void onBackPressed(){
        scannerView.stopCamera();
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
                onResume();
            }
        });

    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]){
        switch (requestCode) {
            case REQUEST_CAMERA:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(Scanner.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(Scanner.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                displayAlertMessage("You need to allow access for both positions.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    //checks if the scanner view is null or not. if so, creates a new one.
    public void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else{
                requestPermission();
            }
        }
    }

    public void openScannedItems(){
        Intent intent = new Intent(this, scannedItems.class);
        intent.putExtra("completedOrders", completedOrders);
        intent.putExtra("remainingOrders", incompleteOrders);
        intent.putExtra("scannedOrders", scannedOrders);
        if(getIntent().getStringArrayListExtra("selectedOrders")!=null){
            intent.putExtra("selectedOrders", getIntent().getStringArrayListExtra("selectedOrders"));
        }
        startActivity(intent);
    }

    public void goBack(){
        Intent intent = new Intent(this, Address.class);
        intent.putExtra("completedOrders", completedOrders);
        intent.putExtra("remainingOrders", incompleteOrders);
        startActivity(intent);
    }

    public void viewAll(){
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0){
            showMessage("Error", "No Data Found");
            return;
        }

        StringBuffer buffer = new StringBuffer();

        while (res.moveToNext()) {
            if(completedOrders!=null && completedOrders.contains(res.getString(0))){
                buffer.append("Current Status: COMPLETED\n");
            }
            else if(scannedOrders.contains(res.getString(0))){
                buffer.append("Current Status: SCANNED\n");
            }
            else{
                buffer.append("Current Status: INCOMPLETE\n");
            }
            buffer.append("--------------------------------------------------------\n");
            buffer.append("Order number: " + res.getString(0)+"\n");
            buffer.append("Address: " + res.getString(1)+"\n");
            buffer.append("Recipient: " + res.getString(2)+"\n");
            buffer.append("Item: " + res.getString(3)+"\n");
            buffer.append("--------------------------------------------------------\n");
            buffer.append("\n");
            buffer.append("\n");
        }

        showMessage("Order Information", buffer.toString());

    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.setMessage(message);
        builder.show();
    }


    @Override
    //release the camera using stop camera method
    public void onDestroy(){
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(Scanner.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(final Result result) {
        final String scanResult = result.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(completedOrders.contains(scanResult) || scannedOrders.contains(scanResult)){
            builder.setTitle("Error: ");
            builder.setMessage("You have already scanned that item. \nWould you like to continue scanning?");
            builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    openScannedItems();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    scannerView.resumeCameraPreview(Scanner.this);
                }
            });
        }
        else if(incompleteOrders.contains(scanResult)){
            builder.setTitle("Order Number: " + scanResult);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    scannedOrders.add(scanResult);
                    scannerView.resumeCameraPreview(Scanner.this);
                }
            });
            builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    scannedOrders.add(scanResult);
                    openScannedItems();
                }
            });
            builder.setMessage("Order Completed! Continue Scanning?");
        }
        else{
            builder.setTitle("Error: ");
            builder.setMessage("Item is not on the order list. \nPlease scan again.");
            builder.setNeutralButton("Rescan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    scannerView.resumeCameraPreview(Scanner.this);
                }
            });

        }
        AlertDialog alert = builder.create();
        alert.show();
    }
}
