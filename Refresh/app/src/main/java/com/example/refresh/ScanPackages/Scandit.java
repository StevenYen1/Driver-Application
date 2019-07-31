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
          Currently on a 30 day free trial, trial ends 08/18/19
     */
    private void setupScandit(){
        ScanditLicense.setAppKey("AYN9UzrQJ0caAEoW3BjRAx8G3/wkFdQURSqG2Otx0xfKXxKWBEfLLWdtSjPsW6DQ3FcVibJpcmxoNG/C7CNXD21sjZVAf5cPQgf7nk92/gwyY5lN4HzHaPN5OdjDA8PoVztHSu4+Hs2Bdky0Gz/ZIanahxxQErzpDZC9yiRZRkQ9GyFJ/K4lLwuJ11/45tanu/cXrgavxriglt8yAwyVDhQJ7q50Fo7iqBIj0oxR/HKec3NQsUt4gG/8Lebyu50MyKVPTFfm+eBmW6BRf9WzR+Nuyu3H7DdlFqIvSv/wK4PPJRuZm2itCaz4kKcY6rD2xr00yJDYRpsK6rsF29FoQ9C3GVLW07b/RcpwUWEsM1soa+c570RoqxtcXCXN6oLpJVXaYLOaRb4M0yC/mGBlMCYXCywQbJBNepOtHFNMRxUqQuS8dYbMWE2UN0mWk2IURjPZqwvUsBRZ1+ECtD3SdIhgJSgRIy3ZsjZy/Uj1KrNVT1Tu1ehLd/RulRjt1lt3ly66UB0L6mW2N/J2NhOBmQ1vy8G8f25C/tRpJtb6m3wuTbCUCJ8s5XRV3SFw0dwmNIRhHm34Myr+Oabcx1JVKvk9BOszF9DFvQ7jclMF/Bc8qcFeS03AWsJsVvPaBlnmpfJnciQOHDb3kj8XEZ8bU/W4ESD6lzLbIOAfuEgAVcYOkp2tarvkEIlLRkSqrPswgi04UhJ/qoAPkRKpoV4N6Y1GsDJGpZk2EAvYMagzvTJbB9c/ywA5fGpI+lUcaKBeql3YCVFJrjrnY3/oMHfNtZgio9gMXHahyath7K5z2U/oCY5el6mI");
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
