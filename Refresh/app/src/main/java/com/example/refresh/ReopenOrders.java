package com.example.refresh;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class ReopenOrders extends AppCompatActivity {

    DatabaseHelper myDb;
    ListView listView;
    TextView detail_display;
    FancyButton accept;
    FancyButton cancel;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();
    ArrayList<String> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reopen_orders);
        listView = findViewById(R.id.list_view_reopen);
        detail_display = findViewById(R.id.display_details_reopen);
        accept = findViewById(R.id.accept_reopen);
        cancel = findViewById(R.id.cancel_reopen);

        myDb = new DatabaseHelper(this);

        Cursor cursor = myDb.get_closed_all();
        while(cursor.moveToNext()){
            String details_string = "";
            details_string += "Order Number: "+cursor.getString(0);
            details_string += "\nShipment Address: "+cursor.getString(1);
            details_string += "\nRecipient: "+cursor.getString(2);
            details_string += "\nItem Name: "+cursor.getString(3);
            details_string += "\nQuantity: "+cursor.getInt(7);
            details_string += "\nCarton Number: "+cursor.getString(8);
            display.add(cursor.getString(0));
            details.add(details_string);
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.check_listview, display);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem =((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                    detail_display.setText(details.get(position));

                }
                else{
                    selectedItems.add(selectedItem);
                    detail_display.setText(details.get(position));

                }

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String x : selectedItems){
                    myDb.reopen_order(x, display.indexOf(x));
                    display.remove(x);
                }
                Intent intent = new Intent(ReopenOrders.this, Menu.class);
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
