package com.example.refresh.ScanPackages;
/*
Description:
    This Activity handles the external scanner.
    It reads the data the scanner inputs and checks to see if the data exists in the database.
    It also displays the data through certain AlertDialogs.

Specific Features:
    Scan OrderNumbers.
    Check validity of scan result.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refresh.AlertDialogs.OrderDetails;
import com.example.refresh.AlertDialogs.ScanResult;
import com.example.refresh.AlertDialogs.ScannerMenu;
import com.example.refresh.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class ExternalScanner extends AppCompatActivity {

    /*
    private instance variables
     */
    private String recentId = "";
    private String Barcode="";
    private EditText input;

    /*
    Methods that are executed when this Activity is opened.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutSetup();
        setInput();
    }

    /*
    Layout setup for visuals of Activity.
     */
    private void layoutSetup(){
        setContentView(R.layout.activity_external_scanner);

        FancyButton sideMenu = findViewById(R.id.external_sideMenu);
        sideMenu.setOnClickListener(v -> onBackPressed());

        TextView mostRecent = findViewById(R.id.recent_id);
        mostRecent.setOnClickListener(v -> viewRecentInformation());
    }

    /*
    Checks to see if a recentScan has occurred. If so, creates orderDetails AlertDialog.
     */
    public void viewRecentInformation(){
        if(recentId.equals("")){
            Toast.makeText(this, "Please scan a package first.", Toast.LENGTH_SHORT).show();
        }
        else {
            OrderDetails orderDetails = new OrderDetails(this, recentId);
            orderDetails.formatOrderDetails();
        }
    }

    /*
    Checks to see if the scanner button is pressed. If true, reads the input.
     */
    public void setInput(){
        input = findViewById(R.id.editText);
        input.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction()==KeyEvent.ACTION_DOWN && (keyCode==301 || keyCode == 302 || keyCode == 303)){
                Barcode = input.getText().toString();
            }
            if(event.getAction()==KeyEvent.ACTION_UP && (keyCode==301 || keyCode == 302 || keyCode == 303)){
                input.getText().clear();
                handleResult(Barcode);
            }
            return false;
        });
    }

    /*
    Opens ScannerMenu if back is pressed.
     */
    @Override
    public void onBackPressed(){
        ScannerMenu scannerMenu = new ScannerMenu(ExternalScanner.this, "e");
        scannerMenu.createDialog();
    }

    /*
    Checks the input to see if it is a valid OrderNumber.
     */
    public void handleResult(String orderId) {
        ScanResult newScan = new ScanResult(ExternalScanner.this, orderId);
        newScan.checkScan();
        recentId = orderId;
    }
}
