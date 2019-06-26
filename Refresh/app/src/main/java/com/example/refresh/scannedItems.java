package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class scannedItems extends AppCompatActivity {

    LinearLayout layout;
    ListView view;
    ArrayList<String> orders = new ArrayList<>();
    ArrayList<String> selectedItems = new ArrayList<>();
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_items);
        myDb = new DatabaseHelper(this);
        layoutSetup();
        setOrderInformation();

        Button sign = findViewById(R.id.goto_sign);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSign();
            }
        });
        if(orders.size()>0){
            createView();
        }
        else{
            noOrders();
        }
    }



    public void layoutSetup(){
        layout = findViewById(R.id.order_area);
        TextView title = new TextView(this);
        title.setText("Scanned Orders:");
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);

        view = findViewById(R.id.list);
        view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        view.addHeaderView(title, "Scanned Orders: ", false);
    }

    public void setOrderInformation(){
        Cursor rawOrders = myDb.getAllData();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String ordernumber = rawOrders.getString(0);
            int status = rawOrders.getInt(4);
            if(status == SCANNED || status == SELECTED){
                orders.add(ordernumber);
                if(status == SELECTED){
                    selectedItems.add(ordernumber);
                }
            }

        }
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go back to scanning?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                openScan();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void createView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.checklist, orders);
        view.setAdapter(adapter);

        for (String x : selectedItems){
            int index = orders.indexOf(x)+1;
            view.setItemChecked(index, true);
        }

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem =((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                    myDb.updateStatus(selectedItem, SCANNED);
                }
                else{
                    selectedItems.add(selectedItem);
                    myDb.updateStatus(selectedItem, SELECTED);
                    Cursor cursor = myDb.getInstance(selectedItem);
                    while(cursor.moveToNext()){
                        Toast.makeText(scannedItems.this, ""+ cursor.getInt(4), Toast.LENGTH_SHORT).show();
                    }

                }
//                showSelectedItems();

            }
        });
    }

    public void openSign(){
        Intent intent = new Intent(this, Feature1.class);
        if(selectedItems.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error: ");
            builder.setCancelable(true);
            builder.setMessage("Please select an order to sign");
            builder.show();
            return;
        }
        intent.putExtra("previousActivity", getIntent().getStringExtra("previousActivity"));
        startActivity(intent);
    }

    public void openScan(){
        Intent intent;
        if(getIntent().getStringExtra("previousActivity").equals("e")){
            intent = new Intent(this, External_Scanner.class);
        }
        else{
            intent = new Intent(this, Scanner.class);
        }
        startActivity(intent);
    }

    public void gotoOrders(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void noOrders(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("There are no orders available");
        builder.setCancelable(false);
        builder.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                openScan();
            }
        });
        builder.setNeutralButton("Orders", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                gotoOrders();
            }
        });
        builder.show();
    }

}
