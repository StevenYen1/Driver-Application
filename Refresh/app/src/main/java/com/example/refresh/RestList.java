package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mashape.unirest.http.Unirest;

import java.lang.reflect.Array;
import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import mehdi.sakout.fancybuttons.FancyButton;

public class RestList extends AppCompatActivity {

    DatabaseHelper myDb;
    String inputString = "";
    ArrayList<String> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_list);
        myDb = new DatabaseHelper(this);
        Spinner dropdown = findViewById(R.id.rest_dropdown);
        final FancyButton btn = findViewById(R.id.rest_search_btn);
        btn.setEnabled(false);

        Cursor cursor = myDb.getAllData();
        final ArrayList<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            int status = cursor.getInt(4);
//            if(status == Delivery_Item.COMPLETE){
                String orderNumber = cursor.getString(0);
                list.add(orderNumber);

                String address = cursor.getString(1);
                String recipient = cursor.getString(2);
                String item = cursor.getString(3);
                String quantity = cursor.getString(7);
                String cartionNumber = cursor.getString(8);

                details.add("Order Number: " + orderNumber
                        +"\nShipment Address: " + address
                        +"\nRecipient: " + recipient
                        +"\nItem: " + item
                        +"\nQuantity: " + quantity
                        +"\nCartonNumber: " + cartionNumber
                );
            }
//        }

        if(list.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error:");
            builder.setMessage("There is no signature to retrieve.");
            builder.show();
        }

        HintSpinner<String> spinnerUI = new HintSpinner<>(
                dropdown,
                new HintAdapter<String>(this, "Select an order", list),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        inputString = list.get(position);
                        TextView detailsView = findViewById(R.id.rest_list_details);
                        detailsView.setText(details.get(position));
                        detailsView.setGravity(Gravity.LEFT);
                        btn.setEnabled(true);
                    }
                });
        spinnerUI.init();


        //right now search bar trumps item select. Maybe make "No id" option for dropdown, then get input text if that is selected
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestList.this, RestCalls.class);
                intent.putExtra("id", inputString);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RestList.this, Menu.class);
        startActivity(intent);
    }
}
