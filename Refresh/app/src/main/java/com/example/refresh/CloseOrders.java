package com.example.refresh;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.Delivery_Item.COMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;
import static java.lang.Integer.parseInt;

public class CloseOrders extends AppCompatActivity {

    private static final String TAG = "CloseOrders";
    ListView listView;
    TextView detail_display;
    FancyButton accept;
    FancyButton cancel;
    DatabaseHelper myDb;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> orderNums = new ArrayList<>();
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
            if(cursor.getInt(4)!=COMPLETE){
                String details_string = "";
                details_string += "Order Number: "+cursor.getString(0);
                details_string += "\nShipment Address: "+cursor.getString(1);
                details_string += "\nRecipient: "+cursor.getString(2);
                details_string += "\nItem Name: "+cursor.getString(3);
                details_string += "\nQuantity: "+cursor.getInt(7);
                details_string += "\nCarton Number: "+cursor.getString(8);
                orderNums.add(cursor.getString(0));
                display.add("OrderNumber: " + cursor.getString(0));
                details.add(details_string);
            }
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.check_listview, display);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = orderNums.get(position);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CloseOrders.this);
                View mView = getLayoutInflater().inflate(R.layout.reasoning_for_action_layout, null);
                TextView reasoningTitle = mView.findViewById(R.id.reasoning_title);
                EditText reasoningView = mView.findViewById(R.id.reasoning_reason);
                final FancyButton reasoningButton = mView.findViewById(R.id.reasoning_button);

                reasoningTitle.setText("You are about to close an order(s): ");
                reasoningTitle.setTextColor(Color.WHITE);
                reasoningTitle.setBackgroundColor(getResources().getColor(R.color.blue));

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

                reasoningButton.setText("Close");
                reasoningButton.setEnabled(false);
                reasoningButton.setTextColor(Color.WHITE);
                reasoningButton.setBackgroundColor(getResources().getColor(R.color.blue));
                reasoningButton.setFocusBackgroundColor(getResources().getColor(R.color.skyblue));
                reasoningButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(String x : selectedItems){
                            myDb.close_order(x, orderNums.indexOf(x));
                        }
                        Intent intent = new Intent(CloseOrders.this, Menu.class);
                        startActivity(intent);
                    }
                });
                builder.setView(mView);
                builder.show();

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
