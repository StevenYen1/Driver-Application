package com.example.refresh;

import android.app.Dialog;
import android.database.Cursor;
import android.provider.ContactsContract;
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

import com.isapanah.awesomespinner.AwesomeSpinner;

import java.util.ArrayList;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.AddOrders.isParsable;
import static com.example.refresh.DatabaseHelper.*;
import static java.lang.Integer.parseInt;

public class AdjustOrders extends AppCompatActivity {

    private ListView listView;
    private EditText search;
    private boolean isMinus = true;
    DatabaseHelper myDb;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> orderNums = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Integer> quantities = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_orders);

        myDb = new DatabaseHelper(this);
        listView = findViewById(R.id.adjust_listview);
        search = findViewById(R.id.adjust_search);

        FancyButton show_all_btn = findViewById(R.id.adjust_show_all);
        show_all_btn.setOnClickListener(v -> refreshList());

        FancyButton search_btn = findViewById(R.id.adjust_search_btn);
        search_btn.setOnClickListener(v -> {

            ArrayList<String> searchList = new ArrayList<>();
            orderNums.clear();
            quantities.clear();

            String keyword = search.getText().toString();
            Cursor orderList = myDb.queryAllOrders();
            while (orderList.moveToNext()) {
                ArrayList<String> instanceList = new ArrayList<>();
                instanceList.add(orderList.getString(COL_ORDERNUMBER));
                instanceList.add(orderList.getString(COL_ADDRESS));
                instanceList.add(orderList.getString(COL_RECIPIENT));
                instanceList.add(orderList.getString(COL_ITEM));
                instanceList.add(orderList.getString(COL_STATUS));
                instanceList.add(orderList.getString(COL_QUANTITY));
                instanceList.add(orderList.getString(COL_CARTONNUMBER));


                for (String listItem : instanceList) {

                    if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(listItem).find()) {
                        searchList.add("Item: " + orderList.getString(COL_ITEM) + "\nQuantity: " + orderList.getString(COL_QUANTITY));
                        orderNums.add(orderList.getString(COL_ORDERNUMBER));
                        quantities.add(orderList.getInt(COL_QUANTITY));
                        break;
                    }
                }
            }

            ArrayAdapter newAdapter = new ArrayAdapter(AdjustOrders.this, android.R.layout.simple_list_item_1, searchList);
            listView.setAdapter(newAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> createAdjustPopup(position));
            if (searchList.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdjustOrders.this);
                builder.setTitle("No Items");
                builder.setMessage("There are no items here");
                builder.show();
            }
            search.setText("");
        });
        populateList();
        createListView();
    }

    private void populateList(){
        Cursor cursor = myDb.queryAllOrders();
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

    private void createListView(){
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> createAdjustPopup(position));
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
