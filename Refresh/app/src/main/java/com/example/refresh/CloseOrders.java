package com.example.refresh;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;
import static java.lang.Integer.parseInt;

public class CloseOrders extends AppCompatActivity {

    //NOT WORKING PROPERLY RN
    private static final String TAG = "CloseOrders";
    ListView listView;
    TextView detail_display;
    Button accept;
    Button cancel;
    DatabaseHelper myDb;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();
    ArrayList<String> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_orders);

        listView = findViewById(R.id.list_view);
        detail_display = findViewById(R.id.display_details_close);
        accept = findViewById(R.id.accept_close);
        cancel = findViewById(R.id.cancel_close);

        myDb = new DatabaseHelper(this);

        Cursor cursor = myDb.getAllData();
        while(cursor.moveToNext()){
            String details_string = "";
            details_string += "Order Number: "+cursor.getString(0);
            details_string += "\nShipment Address: "+cursor.getString(1);
            details_string += "\nRecipient: "+cursor.getString(2);
            details_string += "\nItem Name: "+cursor.getString(3);
            display.add(cursor.getString(0));
            details.add(details_string);
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem =((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                    view.setBackgroundColor(Color.WHITE);
                    detail_display.setText(details.get(position));

                }
                else{
                    selectedItems.add(selectedItem);
                    view.setBackgroundColor(getResources().getColor(R.color.skyblue));
                    detail_display.setText(details.get(position));

                }

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: -------------------------------------------------------SIZE OF SELECTED ITEMS LIST: " + selectedItems.size());
                for(String x : selectedItems){
                    myDb.close_order(x, display.indexOf(x));
                    Log.d(TAG, "onClick: -------------------------------------------------- Order number: " + x + " | indexOf: "+display.indexOf(x));
                    display.remove(x);
                }
                Intent intent = new Intent(CloseOrders.this, Menu.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
