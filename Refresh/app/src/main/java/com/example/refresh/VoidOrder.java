package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class VoidOrder extends AppCompatActivity {
    ListView listView;
    TextView detail_display;
    DatabaseHelper myDb;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_order);
        listView = findViewById(R.id.void_list_view);
        detail_display = findViewById(R.id.display_details_void);
        myDb = new DatabaseHelper(this);

        Cursor cursor = myDb.getAllData();
        while(cursor.moveToNext()){
            String details_string = "";
            details_string += "Order Number: "+cursor.getString(0);
            details_string += "\nShipment Address: "+cursor.getString(1);
            details_string += "\nRecipient: "+cursor.getString(2);
            details_string += "\nItem Name: "+cursor.getString(3);
            details_string += "\nQuantity: "+cursor.getInt(7);
            details_string += "\nCarton Number: "+cursor.getInt(8);
            display.add(cursor.getString(0));
            details.add(details_string);
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                detail_display.setText(details.get(position));
                final String ordernum = display.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(VoidOrder.this);
                builder.setTitle("About to void: " + ordernum);
                builder.setMessage(details.get(position));
                builder.setPositiveButton("VOID", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDb.removeIndex(ordernum, position);
                        Toast.makeText(VoidOrder.this, ordernum+" HAS BEEN VOIDED", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VoidOrder.this, Menu.class);
                        startActivity(intent);
                    }
                });
                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
    }
}
