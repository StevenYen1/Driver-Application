package com.example.refresh;

import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.isapanah.awesomespinner.AwesomeSpinner;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.AddOrders.isParsable;
import static java.lang.Integer.parseInt;

public class AdjustOrders extends AppCompatActivity {

    private ListView listView;
    private EditText search;
    private FancyButton search_btn;
    private FancyButton show_all_btn;
    private AwesomeSpinner dropdown;
    private String filterBy = "No Filter";
    private boolean isMinus = true;
    DatabaseHelper myDb;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> orderNums = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Integer> quantities = new ArrayList<>();
    final String[] filters = {"Item", "OrderNumber", "CartonNumber", "Address"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_orders);

        myDb = new DatabaseHelper(this);
        listView = findViewById(R.id.adjust_listview);
        search = findViewById(R.id.adjust_search);
        dropdown = findViewById(R.id.adjust_filter);

        show_all_btn = findViewById(R.id.adjust_show_all);
        show_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList();
            }
        });

        search_btn = findViewById(R.id.adjust_search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filterBy.equals("No Filter")){
                    ArrayList<String> searchList = new ArrayList<>();
                    orderNums.clear();
                    quantities.clear();
                    Cursor cursor = myDb.queryInstance(filterBy, search.getText().toString());
                    while(cursor.moveToNext()){
                        String orderNum = cursor.getString(0);
                        String item = cursor.getString(3);
                        int quantity = cursor.getInt(7);
                        searchList.add("Item: " + item + "\nQuantity: " + quantity);
                        orderNums.add(orderNum);
                        quantities.add(quantity);
                    }

                    ArrayAdapter newAdapter = new ArrayAdapter(AdjustOrders.this, android.R.layout.simple_list_item_1, searchList);
                    listView.setAdapter(newAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            createAdjustPopup(position);
                        }
                    });
                    if (searchList.size()==0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdjustOrders.this);
                        builder.setTitle("No Items");
                        builder.setMessage("There are no items here");
                        builder.show();
                    }
                    search.setText("");
                }
                else{
                    Toast.makeText(AdjustOrders.this, "PLEASE SEARCH BY A CATEGORY", Toast.LENGTH_SHORT).show();
                }
            }
        });
        populateList();
        createFilterDropdown();
        createListView();
    }

    private void populateList(){
        Cursor cursor = myDb.getAllData();
        while(cursor.moveToNext()){
            String orderNum = cursor.getString(0);
            String item = cursor.getString(3);
            int quantity = cursor.getInt(7);
            orderNums.add(orderNum);
            items.add(item);
            quantities.add(quantity);
            display.add("Item: " + item + "\nQuantity: " + quantity);
        }
    }

    private void createFilterDropdown() {
        ArrayAdapter<String> filter_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        dropdown.setAdapter(filter_adapter);
        dropdown.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                filterBy = filters[position];
            }
        });
    }

    private void createListView(){
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createAdjustPopup(position);
            }
        });
    }

    private int result(String quantity, String adjustment) throws Exception {
        int intQ = parseInt(quantity);
        boolean isParsable = isParsable(adjustment);
        if(isParsable){
            int intAdjust = parseInt(adjustment);
            if(isMinus){return intQ - intAdjust;}
            return intQ + intAdjust;

        }
        return -1;
    }

    private void createAdjustPopup(final int position){
        final int quantity = quantities.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(AdjustOrders.this);
        View mView = getLayoutInflater().inflate(R.layout.adjust_quantity_layout, null);

        TextView titleView = mView.findViewById(R.id.quantity_layout_title);
        titleView.setText("OrderNumber: " + orderNums.get(position));

        final TextView quantityView = mView.findViewById(R.id.quantity_layout_input_quantity);
        quantityView.setText(""+quantity);

        EditText adjustmentView = mView.findViewById(R.id.quantity_layout_input_adjustment);


        final TextView remainingView = mView.findViewById(R.id.quantity_layout_input_remaining);
        remainingView.setText(""+quantity);

        final FancyButton ok = mView.findViewById(R.id.quantity_layout_ok);
        FancyButton cancel = mView.findViewById(R.id.quantity_layout_cancel);
        final FancyButton add = mView.findViewById(R.id.plus);
        final FancyButton sub = mView.findViewById(R.id.minus);

        adjustmentView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(isParsable(s.toString())){
                        int remaining = result(""+quantity, s.toString());
                        remainingView.setText(""+remaining);
                        if(result(""+quantity, s.toString())>=0){
                            ok.setEnabled(true);
                        }
                        else{
                            ok.setEnabled(false);
                        }
                    }
                    else{
                        remainingView.setText("N/A");
                        ok.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        builder.setView(mView);
        final Dialog alert = builder.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.updateQuantity(orderNums.get(position), parseInt(remainingView.getText().toString()));
                refreshList();
                alert.dismiss();
                isMinus=true;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMinus=true;
                alert.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMinus = false;
                add.setGhost(false);
                sub.setGhost(true);
                remainingView.setText("Re-enter Adjustment Value");
                ok.setEnabled(false);
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMinus = true;
                sub.setGhost(false);
                add.setGhost(true);
                remainingView.setText("Re-enter Adjustment Value");
                ok.setEnabled(false);
            }
        });
    }

    public void refreshList(){
        display.clear();
        orderNums.clear();
        items.clear();
        quantities.clear();
        populateList();
        createListView();
    }

}
