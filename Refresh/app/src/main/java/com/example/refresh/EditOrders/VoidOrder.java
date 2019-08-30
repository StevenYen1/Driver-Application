package com.example.refresh.EditOrders;
/*
Description:
    The purpose of this class was to 'Void' orders, which was equivalent to eliminating them from the list of current orders.
    This option was implemented in case of an error or invalid order.

Specific Features:
    Removing an order from the list of orders (deleting it from the 'order table')

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.autofill.DateTransformation;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.MainMenu.Menu;
import com.example.refresh.R;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;

public class VoidOrder extends AppCompatActivity {
    /*
    private instance variables
     */
    private ListView listView;
    private ArrayList<String> display = new ArrayList<>();
    private ArrayList<String> orderNums = new ArrayList<>();
    private ArrayList<String> details = new ArrayList<>();

    /*
    methods that occur when the activity starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_order);
        listView = findViewById(R.id.void_list_view);
        createList();
        setupAdapter();

    }

    /*
    instantiates the list of data and stores them in ArrayLists of type String
     */
    private void createList(){
        DatabaseHelper databaseHelper = new DatabaseHelper(VoidOrder.this);
        Cursor cursor = databaseHelper.queryAllOrders();
        while(cursor.moveToNext()){

            String details_string = "Order Number: "+cursor.getString(COL_ORDERNUMBER)
                    +"\nShipment Address: "+cursor.getString(COL_ADDRESS)
                    +"\nRecipient: "+cursor.getString(COL_RECIPIENT)
                    +"\nItem Name: "+cursor.getString(COL_ITEM)
                    +"\nQuantity: "+cursor.getInt(COL_QUANTITY)
                    +"\nCarton Number: "+cursor.getString(COL_CARTONNUMBER);

            display.add("Order Number: " + cursor.getString(COL_ORDERNUMBER));
            orderNums.add(cursor.getString(COL_ORDERNUMBER));
            details.add(details_string);
        }
    }

    /*
    Setup for the ArrayAdapter. Also sets each item OnClickListener
     */
    private void setupAdapter(){
        DatabaseHelper databaseHelper = new DatabaseHelper(VoidOrder.this);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            final String ordernum = orderNums.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(VoidOrder.this);
            View mView = getLayoutInflater().inflate(R.layout.reasoning_for_action_layout, null);
            TextView reasoningTitle = mView.findViewById(R.id.reasoning_title);
            TextView orderDetails = mView.findViewById(R.id.reasoning_orderinfo);
            EditText reasoningView = mView.findViewById(R.id.reasoning_reason);
            final FancyButton reasoningButton = mView.findViewById(R.id.reasoning_button);

            reasoningTitle.setText("WARNING! YOU ARE ABOUT TO VOID ORDER: " + ordernum);
            reasoningTitle.setTextColor(Color.WHITE);
            reasoningTitle.setBackgroundColor(getResources().getColor(R.color.red));

            orderDetails.setText(details.get(position));
            orderDetails.setTextColor(Color.WHITE);
            orderDetails.setBackgroundColor(getResources().getColor(R.color.light_red));

            reasoningView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    reasoningButton.setEnabled(true);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            reasoningButton.setText("VOID");
            reasoningButton.setEnabled(false);
            reasoningButton.setTextColor(Color.WHITE);
            reasoningButton.setBackgroundColor(getResources().getColor(R.color.red));
            reasoningButton.setFocusBackgroundColor(getResources().getColor(R.color.light_red));
            reasoningButton.setOnClickListener(v -> {
                databaseHelper.deleteOrder(ordernum);
                Toast.makeText(VoidOrder.this, ordernum+" HAS BEEN VOIDED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VoidOrder.this, Menu.class);
                startActivity(intent);
            });
            builder.setView(mView);
            builder.show();

        });
    }
}
