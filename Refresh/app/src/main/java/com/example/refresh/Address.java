package com.example.refresh;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

public class Address extends AppCompatActivity {

    private static final String TAG = "Address";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    private String ordernumdefault = "12345678";
    private String addressdefault = "49 Alton Street Arlington MA 02474";
    private ArrayList<Delivery_Item> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        staticList();

        if(isServicesOK()){
            init();
        }
    }

    private void staticList(){
        ArrayList<Delivery_Item> newList = new ArrayList<Delivery_Item>();
        //order number is NOT CORRECT. FIX LATER. MAYBE USE A STRING TO SAVE THE VALUE THEN PARSE IT.
        Delivery_Item item1 = new Delivery_Item("01001000", "1600 Pennsylvania Ave NW Washington, DC 20500 ");
        Delivery_Item item2 = new Delivery_Item("0002", "Seoul, South Korea");
        newList.add(item1);
        newList.add(item2);
        this.list = newList;
    }

    private void init(){
        Button openMaps = (Button) findViewById(R.id.openMaps);
        openMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delivery_Item item = list.get(0);
                ordernumdefault = item.getOrderNumber();
                addressdefault = item.getOrderString();
                Intent intent = new Intent(Address.this, MapActivity.class);
                intent.putExtra("orderNumber", ordernumdefault);
                intent.putExtra("orderString", addressdefault);
                startActivity(intent);
            }
        });
    }


    //version issue
    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Address.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error has occurred but can be resolved
            Log.d(TAG, "isServicesOK: An Error has occurred but is resolvable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Address.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
