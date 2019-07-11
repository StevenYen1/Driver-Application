package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class VoidOrder extends AppCompatActivity {
    ListView listView;
    DatabaseHelper myDb;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_order);
        listView = findViewById(R.id.void_list_view);
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
                final String ordernum = display.get(position);
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
                reasoningButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDb.removeIndex(ordernum, position);
                        Toast.makeText(VoidOrder.this, ordernum+" HAS BEEN VOIDED", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VoidOrder.this, Menu.class);
                        startActivity(intent);
                    }
                });
                builder.setView(mView);
                builder.show();

            }
        });
    }
}
