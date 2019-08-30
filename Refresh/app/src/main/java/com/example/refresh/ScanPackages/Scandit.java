package com.example.refresh.ScanPackages;
/*
Description:
    This class handles the camera scanner. Currently utilizes Scandit's BarcodeScanner package.
    It reads the data the scanner inputs and checks to see if the data exists in the database.

Specific Functions:
    Barcode Scanner
    Check validity of scanned result.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.refresh.AlertDialogs.ScanResult;
import com.example.refresh.AlertDialogs.ScannerMenu;
import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import java.util.List;

public class Scandit extends Activity implements OnScanListener {

    /*
    private instance variables
     */
    private static BarcodePicker mPicker;

    /*
    Getter for barcode scanner object
     */
    public static BarcodePicker getScanner(){
        return mPicker;
    }

    /*
    Methods that occur when the Activity Starts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupScandit();
    }

    /*
    Setup required for creating a Scandit BarcodePicker.
    NOTE: Scandit does not function unless you place the license key inside "setAppKey"
     */
    private void setupScandit(){
        ScanditLicense.setAppKey("Scandit license key");
        ScanSettings settings = ScanSettings.create();
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN13, true);
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_UPCA, true);
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_CODE128, true);
        mPicker = new BarcodePicker(Scandit.this, settings);
        mPicker.setOnScanListener(Scandit.this);
        setContentView(mPicker);
    }

    /*
    Required method for resuming scanning.
     */
    @Override
    protected void onResume() {
        mPicker.startScanning();
        super.onResume();
    }

    /*
    Required method for pausing scanning.
     */
    @Override
    protected void onPause() {
        mPicker.stopScanning();
        super.onPause();
    }

    /*
    This method checks the scan result to see if it matches an order number in the "order table"
     */
    @Override
    public void didScan(ScanSession scanSession) {
        List<Barcode> list = scanSession.getNewlyRecognizedCodes();
        String orderNumber = list.get(0).getData();
        ScanResult scanResult = new ScanResult(Scandit.this, orderNumber);
        scanResult.checkScan();
    }

    /*
    Opens the scanner menu when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        mPicker.pauseScanning();
        ScannerMenu scannerMenu = new ScannerMenu(Scandit.this, "c");
        scannerMenu.createDialog();
    }
}
