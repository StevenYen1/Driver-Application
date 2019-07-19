package com.example.refresh;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.support.constraint.Constraints.TAG;
import static com.example.refresh.Delivery_Item.COMPLETE;
import static com.example.refresh.Delivery_Item.FAIL_SEND;
import static com.example.refresh.Delivery_Item.INCOMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class Scandit extends Activity implements OnScanListener {

    private BarcodePicker mPicker;
    private DatabaseHelper myDb;
    private String orders_status;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDb = new DatabaseHelper(this);

        ScanditLicense.setAppKey("AYN9UzrQJ0caAEoW3BjRAx8G3/wkFdQURSqG2Otx0xfKXxKWBEfLLWdtSjPsW6DQ3FcVibJpcmxoNG/C7CNXD21sjZVAf5cPQgf7nk92/gwyY5lN4HzHaPN5OdjDA8PoVztHSu4+Hs2Bdky0Gz/ZIanahxxQErzpDZC9yiRZRkQ9GyFJ/K4lLwuJ11/45tanu/cXrgavxriglt8yAwyVDhQJ7q50Fo7iqBIj0oxR/HKec3NQsUt4gG/8Lebyu50MyKVPTFfm+eBmW6BRf9WzR+Nuyu3H7DdlFqIvSv/wK4PPJRuZm2itCaz4kKcY6rD2xr00yJDYRpsK6rsF29FoQ9C3GVLW07b/RcpwUWEsM1soa+c570RoqxtcXCXN6oLpJVXaYLOaRb4M0yC/mGBlMCYXCywQbJBNepOtHFNMRxUqQuS8dYbMWE2UN0mWk2IURjPZqwvUsBRZ1+ECtD3SdIhgJSgRIy3ZsjZy/Uj1KrNVT1Tu1ehLd/RulRjt1lt3ly66UB0L6mW2N/J2NhOBmQ1vy8G8f25C/tRpJtb6m3wuTbCUCJ8s5XRV3SFw0dwmNIRhHm34Myr+Oabcx1JVKvk9BOszF9DFvQ7jclMF/Bc8qcFeS03AWsJsVvPaBlnmpfJnciQOHDb3kj8XEZ8bU/W4ESD6lzLbIOAfuEgAVcYOkp2tarvkEIlLRkSqrPswgi04UhJ/qoAPkRKpoV4N6Y1GsDJGpZk2EAvYMagzvTJbB9c/ywA5fGpI+lUcaKBeql3YCVFJrjrnY3/oMHfNtZgio9gMXHahyath7K5z2U/oCY5el6mI");
        ScanSettings settings = ScanSettings.create();
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN13, true);
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_UPCA, true);
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_CODE128, true);

        mPicker = new BarcodePicker(this, settings);
        mPicker.setOnScanListener(this);

        setContentView(mPicker);
        setOrderInformation();

    }

    @Override
    protected void onResume() {
        mPicker.startScanning();
        super.onResume();
    }
    @Override
    protected void onPause() {
        mPicker.stopScanning();
        super.onPause();
    }

    @Override
    public void didScan(ScanSession scanSession) {
        List<Barcode> list = scanSession.getNewlyRecognizedCodes();
        Log.d(TAG, "didScan: " + list.get(0).getData());
        if(myDb.getStatus(list.get(0).getData())==INCOMPLETE){
            myDb.updateStatus(list.get(0).getData(), SCANNED);
            Toast.makeText(this, "Scan Complete: " + list.get(0).getData(), Toast.LENGTH_SHORT).show();
        }

    }

    public void setOrderInformation(){
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
    }

    @Override
    public void onBackPressed() {
        mPicker.pauseScanning();
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
                        mPicker.resumeScanning();
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });
    }

    public void openScannedItems(){
        Intent intent = new Intent(this, scannedItems.class);
        intent.putExtra("previousActivity", "c");
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
        new AlertDialog.Builder(Scandit.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
