package com.example.refresh.SignPackages;
/*
Description:
    The class that handles post-scanned items. Requires the user to select an order to sign.

Specific Features:
    Displays List of Scanned / Selected Items
    Allows user to select items to scan.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.refresh.AlertDialogs.StandardMessage;
import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Menu;
import com.example.refresh.R;
import com.example.refresh.ScanPackages.External_Scanner;
import com.example.refresh.ScanPackages.Scandit;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.ItemModel.PackageModel.SCANNED;
import static com.example.refresh.ItemModel.PackageModel.SELECTED;

public class ScannedItems extends AppCompatActivity {

    /*
    private instance variables
     */
    private ArrayList<String> orders = new ArrayList<>();
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> display = new ArrayList<>();

    /*
    Methods that occur when the activity begins.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_items);
        setOrderInformation();
        setupSignatureBtn();
    }

    /*
    Checks if there are any scanned orders.
     */
    private boolean ordersExist(){
        if(orders.size()>0){ return true; }
        return false;
    }

    /*
    Initializes signature button and sets OnClickListener
     */
    private void setupSignatureBtn(){
        FancyButton sign = findViewById(R.id.goto_sign);
        if(selectedItems.isEmpty()){
            sign.setEnabled(false);
        }
        else{
            sign.setEnabled(true);
        }

        sign.setOnClickListener(view -> openSign());
    }

    /*
    Looks at the database to see which orders are scanned/selected. Places those orders in lists.
     */
    private void setOrderInformation(){
        DatabaseHelper databaseHelper = new DatabaseHelper(ScannedItems.this);
        Cursor queryResults = databaseHelper.queryAllOrders();
        while(queryResults.moveToNext()){
            String orderNumber = queryResults.getString(0);
            int status = queryResults.getInt(4);
            if(status == SCANNED || status == SELECTED){
                orders.add(orderNumber);
                display.add("OrderNumber: " + orderNumber);
                if(status == SELECTED){
                    selectedItems.add(orderNumber);
                }
            }
        }
        if(ordersExist()){
            setupList();
        }
        else{
            createNoOrdersAlert();
        }
    }

    /*
    Creates an alert to confirm if the user would like to return to scanning.
     */
    @Override
    public void onBackPressed(){
        StandardMessage standardMessage = new StandardMessage(ScannedItems.this);
        standardMessage.buildStandardMessage("Move To: Scanner", "Would you like to scan another item?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> openScan())
                .setNeutralButton("No", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    /*
    Sets up the ListView that contains all of the scanned or selected orders.
     */
    private void setupList(){
        DatabaseHelper databaseHelper = new DatabaseHelper(ScannedItems.this);
        FancyButton sign =  findViewById(R.id.goto_sign);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.checklist, display);

        ListView listContainer = findViewById(R.id.list);
        listContainer.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listContainer.setAdapter(adapter);

        for (String x : selectedItems){
            int index = orders.indexOf(x);
            listContainer.setItemChecked(index, true);
        }

        listContainer.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = orders.get(position);
            if(selectedItems.contains(selectedItem)){
                selectedItems.remove(selectedItem);
                databaseHelper.updateStatus(selectedItem, SCANNED);
                if(selectedItems.isEmpty()){
                    sign.setEnabled(false);
                }
            }
            else{
                selectedItems.add(selectedItem);
                databaseHelper.updateStatus(selectedItem, SELECTED);
                if(!sign.isEnabled()){
                    sign.setEnabled(true);
                }
            }
        });
    }

    /*
    Opens Signature activity
     */
    private void openSign(){
        Intent intent = new Intent(this, Signature.class);
        intent.putExtra("previousActivity", getIntent().getStringExtra("previousActivity"));
        startActivity(intent);
    }

    /*
    Opens a scanner activity.
     */
    private void openScan(){
        Intent intent = new Intent(this, chooseScanner());
        startActivity(intent);
    }

    /*
    Opens Menu Activity
     */
    private void openMenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    /*
    Returns the class of most recent scanner activity used.
     */
    private java.lang.Class chooseScanner(){
        if(getIntent().getStringExtra("previousActivity").equals("e")){return External_Scanner.class;}
        return Scandit.class;
    }

    /*
    Creates an alert dialogue that notifies the user that no scanned orders currently exist.
     */
    private void createNoOrdersAlert(){
        StandardMessage standardMessage = new StandardMessage(ScannedItems.this);
        standardMessage.buildStandardMessage("Error:", "There are no orders available")
                .setNeutralButton("Menu", (dialog, which) -> openMenu())
                .setPositiveButton("Scan", (dialog, which) -> openScan())
                .setCancelable(false)
                .create()
                .show();
    }
}
