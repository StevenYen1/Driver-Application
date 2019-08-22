package com.example.refresh.EditOrders;
/*
Description:
    The purpose of this activity is to adjust the quantity of an item.

Specific Features:
    Search for items using keywords
    Adjust the quantity of items.
 */
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.refresh.AlertDialogs.AdjustItemDialog;
import com.example.refresh.AlertDialogs.StandardMessage;
import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Model.ItemModel;
import com.example.refresh.MainMenu.Menu;
import com.example.refresh.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;

public class AdjustOrders extends AppCompatActivity {

    /*
    private instance variables
     */
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> orderNums = new ArrayList<>();
    ArrayList<Integer> quantities = new ArrayList<>();

    /*
    Methods that occur when the activity starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_orders);
        populateList();
        createListView();
        setupButtons();
    }

    /*
    Initializes layout buttons and sets OnClickListeners
     */
    private void setupButtons(){
        FancyButton show_all_btn = findViewById(R.id.adjust_show_all);
        show_all_btn.setOnClickListener(v -> refreshList());

        FancyButton search_btn = findViewById(R.id.adjust_search_btn);
        search_btn.setOnClickListener(v -> filterListView());
    }

    /*
    Creates a new list by filtering with a keyword. Replaces the old list.
     */
    private void filterListView(){
        ListView listView = findViewById(R.id.adjust_listview);
        clearLists();
        ArrayList<String> searchList = search();
        if (searchList.size() == 0) {
            StandardMessage standardMessage = new StandardMessage(AdjustOrders.this);
            standardMessage.buildStandardMessage("No Items", "There are no items here.").show();
        }
        ArrayAdapter newAdapter = new ArrayAdapter(AdjustOrders.this, android.R.layout.simple_list_item_1, searchList);
        listView.setAdapter(newAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> createAdjustPopup(position));
    }

    /*
    Searches all order information using a keyword obtained from the search bar.
    Returns a list of orders that have any information containing the keyword.
     */
    private ArrayList<String> search(){
        ArrayList<String> searchList = new ArrayList<>();
        EditText searchView = findViewById(R.id.adjust_search);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor orderList = databaseHelper.queryAllOrders();
        while (orderList.moveToNext()) {
            ArrayList<String> instanceList = new ArrayList<>();
            String itemString = orderList.getString(COL_ITEM);
            Type type = new TypeToken<ArrayList<ItemModel>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<ItemModel> finalOutputString = gson.fromJson(itemString, type);
            String items = "";
            int i = 0;
            for(; i < finalOutputString.size()-1; i++){
                items+= finalOutputString.get(i).getItem()+", ";
                instanceList.add(finalOutputString.get(i).getItem());
            }
            items+= finalOutputString.get(i).getItem();
            instanceList.add(finalOutputString.get(i).getItem());

            for (String listItem : instanceList) {
                String keyword = searchView.getText().toString();
                if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(listItem).find()) {
                    searchList.add("Items: \n" + items + "\n\nQuantity: " + orderList.getString(COL_QUANTITY));
                    orderNums.add(orderList.getString(COL_ORDERNUMBER));
                    quantities.add(orderList.getInt(COL_QUANTITY));
                    break;
                }
            }
        }
        searchView.setText("");
        return searchList;
    }

    /*
    clears all ArrayLists
     */
    private void clearLists(){
        display.clear();
        orderNums.clear();
        quantities.clear();
    }

    /*
    Queries the database and stores the information inside the ArrayList
     */
    private void populateList(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.queryAllOrders();
        while(cursor.moveToNext()){
            String orderNum = cursor.getString(0);
            String itemString = cursor.getString(COL_ITEM);
            Type type = new TypeToken<ArrayList<ItemModel>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<ItemModel> finalOutputString = gson.fromJson(itemString, type);
            String items = "";
            int i = 0;
            for(; i < finalOutputString.size()-1; i++){
                items+= finalOutputString.get(i).getItem()+", ";
            }
            items+= finalOutputString.get(i).getItem();

            int quantity = cursor.getInt(7);
            orderNums.add(orderNum);
            quantities.add(quantity);
            display.add("Items: \n" + items + "\n\nQuantity: " + quantity);
        }
    }

    /*
    Method that creates a list of all orders.
     */
    private void createListView(){
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, display);
        ListView listView = findViewById(R.id.adjust_listview);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> createAdjustPopup(position));
    }

    /*
    Creates the popup that is visible when an item is selected to adjust.
     */
    private void createAdjustPopup(int position){
        int quantity = quantities.get(position);
        String orderId = orderNums.get(position);
        AdjustItemDialog newDialog = new AdjustItemDialog(AdjustOrders.this, orderId, quantity);
        newDialog.createDialog().show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdjustOrders.this, Menu.class);
        startActivity(intent);
    }

    /*
        Refreshes the old list and displays the most updated version.
         */
    public void refreshList(){
        clearLists();
        populateList();
        createListView();
    }

}
