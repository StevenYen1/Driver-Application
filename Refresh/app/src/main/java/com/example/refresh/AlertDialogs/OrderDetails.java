package com.example.refresh.AlertDialogs;
/*
Description:
    Class designed to take in order details and create a custom AlertDialog.

Specific Features:
    Creates OrderDetails object.
    Assigns data to the layout.
    Open GoogleMaps api.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Model.ItemModel;
import com.example.refresh.GPSLocation.MapActivity;
import com.example.refresh.PrintLabel.Printer;
import com.example.refresh.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;

public class OrderDetails extends Application {
    /*
    private instance variables
     */
    private Context context;
    private String orderId;

    /*
    Constructor
     */
    public OrderDetails(Context context, String orderId){
        this.context = context;
        this.orderId = orderId;
    }

    /*
    Queries the "order table" for the speciic order.
     */
    private Cursor getOrderDetails(){
        DatabaseHelper myDb = new DatabaseHelper(context);
        return myDb.queryOrder(orderId);
    }

    /*
    Formats information from database and enters it into the layout.
     */
    public void formatOrderDetails(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.newdetails_layout, null);
        Cursor queryResult = getOrderDetails();

        while(queryResult.moveToNext()){
            String ordernum = queryResult.getString(COL_ORDERNUMBER);
            String address = queryResult.getString(COL_ADDRESS);
            String recipient = queryResult.getString(COL_RECIPIENT);
            String itemString = queryResult.getString(COL_ITEM);
            Type type = new TypeToken<ArrayList<ItemModel>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<ItemModel> finalOutputString = gson.fromJson(itemString, type);
            String items = "";
            int i = 0;
            for(; i < finalOutputString.size()-1; i++){
                items+= finalOutputString.get(i).getItem()+",\n";
            }
            items+= finalOutputString.get(i).getItem();
            int quantity = queryResult.getInt(COL_QUANTITY);
            String cartonnum = queryResult.getString(COL_CARTONNUMBER);

            TextView ordernum_view = view.findViewById(R.id.newdetails_ordernum);
            ordernum_view.setText("Order Number: " + ordernum);

            TextView cartonnum_view = view.findViewById(R.id.newdetails_cartonnum);
            cartonnum_view.setText("Carton Number: " + cartonnum);

            TextView address_view = view.findViewById(R.id.newdetails_address);
            address_view.setText(address);

            TextView recipient_view = view.findViewById(R.id.newdetails_recipient);
            recipient_view.setText(recipient);

            TextView item_view = view.findViewById(R.id.newdetails_item);
            item_view.setText(items);

            TextView quantity_view = view.findViewById(R.id.newdetails_quantity);
            quantity_view.setText("" + quantity);

            FancyButton mapBtn = view.findViewById(R.id.newdetails_map);
            mapBtn.setOnClickListener(v -> startGoogleMap(address));

            FancyButton printBtn = view.findViewById(R.id.newdetails_print);
            printBtn.setOnClickListener(v -> startPrint(ordernum));

            alertBuilder.setView(view);
            AlertDialog dialog = alertBuilder.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /*
    Starts activity that allows user to view GoogleMaps api
     */
    private void startGoogleMap(String address){
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    private void startPrint(String orderId){
        Intent intent = new Intent(context, Printer.class);
        intent.putExtra("orderId", orderId);
        context.startActivity(intent);
    }
}
