package com.example.refresh;
/*
Description:
    The purpose of this class is to close orders (put them in an inactive state)

Specific Features:
    Creates a ListView of current orders
    Allows the user to close any any number of orders.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.refresh.AlertDialogs.CloseOrderDialog;
import com.example.refresh.DatabaseHelper.DatabaseHelper;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_STATUS;
import static com.example.refresh.ItemModel.PackageModel.COMPLETE;

public class CloseOrders extends AppCompatActivity {

    /*
    private instance variables
     */
    private ArrayList<String> display = new ArrayList<>();
    private ArrayList<String> orderNums = new ArrayList<>();
    private ArrayList<String> details = new ArrayList<>();
    private ArrayList<String> selectedItems = new ArrayList<>();


    /*
    Methods that are called when the activity starts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_orders);
        setupOrderInformation();
        setupListLayout();
        createButtons();
    }


    /*
    This method queries the database and stores the certain data in ArrayLists.
     */
    private void setupOrderInformation(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.queryAllOrders();
        while(cursor.moveToNext()){
            if(cursor.getInt(COL_STATUS)!=COMPLETE){

                String details_string = "Order Number: "+cursor.getString(COL_ORDERNUMBER)
                        + "\nShipment Address: "+cursor.getString(COL_ADDRESS)
                        + "\nRecipient: "+cursor.getString(COL_RECIPIENT)
                        + "\nItem Name: "+cursor.getString(COL_ITEM)
                        + "\nQuantity: "+cursor.getInt(COL_QUANTITY)
                        + "\nCarton Number: "+cursor.getString(COL_CARTONNUMBER);

                orderNums.add(cursor.getString(COL_ORDERNUMBER));
                display.add("OrderNumber: " + cursor.getString(COL_ORDERNUMBER));
                details.add(details_string);
            }
        }
    }


    /*
    Initializes buttons and sets OnClickListeners
     */
    private void createButtons(){
        FancyButton accept = findViewById(R.id.accept_close);
        accept.setOnClickListener(v -> {
            CloseOrderDialog dialog = new CloseOrderDialog(CloseOrders.this, selectedItems);
            dialog.createReasoningDialog();
        });

        FancyButton cancel = findViewById(R.id.cancel_close);
        cancel.setOnClickListener(v -> onBackPressed());
    }


    /*
    Initializes ListView and sets OnItemClickListener
     */
    private void setupListLayout(){
        ListView listView = findViewById(R.id.list_view);
        TextView detail_display = findViewById(R.id.display_details_close);

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.check_listview, display);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = orderNums.get(position);
            if(selectedItems.contains(selectedItem)){
                selectedItems.remove(selectedItem);
                detail_display.setText(details.get(position));

            }
            else{
                selectedItems.add(selectedItem);
                detail_display.setText(details.get(position));
            }
        });
    }
}
