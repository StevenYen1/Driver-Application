package com.example.refresh;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(Scanner.this, "Permission is granted!", Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }
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

    public void openFeature1(){
        Intent intent = new Intent(this, Feature1.class);
        intent.putExtra("orderNumber", this.getIntent().getStringExtra("orderNumber"));
        intent.putExtra("completedOrders", this.getIntent().getStringArrayListExtra("completedOrders"));
        startActivity(intent);
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

    private String returnDate(){
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    @Override
    public void handleResult(final Result result) {
        final String scanResult = result.getText();
        String orderNumber = this.getIntent().getStringExtra("orderNumber");
        String recipient = this.getIntent().getStringExtra("recipient");
        String item = this.getIntent().getStringExtra("item");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Number: " + orderNumber);
        builder.setPositiveButton("Rescan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(Scanner.this);
            }
        });
        if(scanResult.equals(orderNumber)){
            builder.setNeutralButton("Proceed to Sign", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    openFeature1();
                }
            });
            String r = recipient;
            String i = item;
            String date = returnDate();
            builder.setMessage("Recipient: " + recipient + "\n" + "Item Purchased: " + item + "\n" + "Date Received: " + date);
        }
        else{
            builder.setMessage("This is not the correct item. \nPlease scan again.");
        }
        AlertDialog alert = builder.create();
        alert.show();
    }
}
