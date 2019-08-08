package com.example.refresh.RetrieveSignatures;
/*
Description:
    Creates a list of possible signatures to retrieve.

Specific Features:
    Dropdown List that allows the user to select a specific order.
    Displays order information

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Menu;
import com.example.refresh.R;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;

public class SignatureInterface extends AppCompatActivity {

    /*
    private instance variables
     */
    private String orderSelected = "";
    private ArrayList<String> details = new ArrayList<>();
    private ArrayList<String> orderNumbers = new ArrayList<>();
    private FancyButton signatureSearch;


    /*
    Methods that are called when the Activity starts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_list);
        signatureSearch = findViewById(R.id.rest_search_btn);
        Cursor queryResult = queryAllData();
        setOrderInformation(queryResult);
        searchBtnSetup();
        createSpinner();
    }


    /*
    Grabs all order information from "order table" and stores them locally in the Activity.
     */
    private void setOrderInformation(Cursor queryResult){
        while(queryResult.moveToNext()){
            String orderNumber = queryResult.getString(COL_ORDERNUMBER);
            orderNumbers.add(orderNumber);

            String address = queryResult.getString(COL_ADDRESS);
            String recipient = queryResult.getString(COL_RECIPIENT);
            String item = queryResult.getString(COL_ITEM);
            String quantity = queryResult.getString(COL_QUANTITY);
            String cartionNumber = queryResult.getString(COL_CARTONNUMBER);

            details.add("Order Number: " + orderNumber
                    +"\nShipment Address: " + address
                    +"\nRecipient: " + recipient
                    +"\nItem: " + item
                    +"\nQuantity: " + quantity
                    +"\nCartonNumber: " + cartionNumber
            );
        }
    }


    /*
    Returns a Cursor object that can grab all data currently available in the "order table"
     */
    private Cursor queryAllData(){
        DatabaseHelper databaseHelper = new DatabaseHelper(SignatureInterface.this);
        return databaseHelper.queryAllOrders();
    }


    /*
    Creates the spinner and populates it with data.
     */
    private void createSpinner(){
        Spinner dropdown = findViewById(R.id.rest_dropdown);
        HintSpinner<String> spinnerUI = new HintSpinner<>(
                dropdown,
                new HintAdapter<>(this, "Select an order", orderNumbers),
                (position, itemAtPosition) -> {
                    orderSelected = orderNumbers.get(position);
                    TextView detailsView = findViewById(R.id.rest_list_details);
                    detailsView.setText(details.get(position));
                    detailsView.setGravity(Gravity.LEFT);
                    signatureSearch.setEnabled(true);
                });
        spinnerUI.init();
    }


    /*
    Setup for search button.
     */
    private void searchBtnSetup(){
        signatureSearch.setEnabled(false);
        signatureSearch.setOnClickListener(v -> {
            Intent intent = new Intent(SignatureInterface.this, SignatureCall.class);
            intent.putExtra("id", orderSelected);
            startActivity(intent);
        });
    }


    /*
    Forces return to Manage Route Menu
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignatureInterface.this, Menu.class);
        startActivity(intent);
    }
}
