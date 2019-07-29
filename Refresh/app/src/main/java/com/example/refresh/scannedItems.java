package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class scannedItems extends AppCompatActivity {

    private ListView view;
    private ArrayList<String> orders = new ArrayList<>();
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> display = new ArrayList<>();
    private DatabaseHelper myDb;
    private FancyButton sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_items);
        myDb = new DatabaseHelper(this);
        layoutSetup();
        setOrderInformation();

        sign = findViewById(R.id.goto_sign);
        if(selectedItems.isEmpty()){
            sign.setEnabled(false);
        }
        else{
            sign.setEnabled(true);
        }

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
        view = findViewById(R.id.list);
        view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    public void setOrderInformation(){
        Cursor rawOrders = myDb.queryAllOrders();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String ordernumber = rawOrders.getString(0);
            int status = rawOrders.getInt(4);
            if(status == SCANNED || status == SELECTED){
                orders.add(ordernumber);
                display.add("OrderNumber: " + ordernumber);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.checklist, display);
        view.setAdapter(adapter);

        for (String x : selectedItems){
            int index = orders.indexOf(x);
            view.setItemChecked(index, true);
        }

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = orders.get(position);
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                    myDb.updateStatus(selectedItem, SCANNED);
                    if(selectedItems.isEmpty()){
                        sign.setEnabled(false);
                    }
                }
                else{
                    selectedItems.add(selectedItem);
                    myDb.updateStatus(selectedItem, SELECTED);
                    if(!sign.isEnabled()){
                        sign.setEnabled(true);
                    }
                }
            }
        });
    }

    public void openSign(){
        Intent intent = new Intent(this, Signature.class);
        if(selectedItems.isEmpty()){
            AlertDialog.Builder builder = buildStandardMessage("Error:", "Please select an order to sign");
            builder.setCancelable(true);
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
            intent = new Intent(this, Scandit.class);
        }
        startActivity(intent);
    }

    public void gotoMenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void noOrders(){

        AlertDialog.Builder builder = buildStandardMessage("Error:", "There are no orders available.");
        builder.setCancelable(false);
        builder.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                openScan();
            }
        });
        builder.setNeutralButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                gotoMenu();
            }
        });
        builder.show();
    }

    public AlertDialog.Builder buildStandardMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }

}
