package com.example.refresh;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Text;

import java.lang.annotation.Target;
import java.util.ArrayList;

public class Address extends AppCompatActivity {

    private static final String TAG = "Address";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    private static final String ORDER_NUMBER_DEFAULT = "0";
    private static final String ADDRESS_DEFAULT = "Massachusetts";
    private ArrayList<Delivery_Item> list;
    private String order_num = ORDER_NUMBER_DEFAULT;
    private String address = ADDRESS_DEFAULT;
    private int num_columns = 3;
    private TableLayout t1;
    private TableRow tr;
    private TextView tv1, tv2, tv3;
    private ArrayList<TextView> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        staticList();
        Button go = (Button) findViewById(R.id.import_orders);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRowFromList();
            }
        });

        t1 = (TableLayout) findViewById(R.id.Table);
        for(int i = 0; i < num_columns; i++){
            t1.setColumnStretchable(i, true);
        }
//        if(isServicesOK()){
//            init();
//        }
    }


//@TargetApi(Build.VERSION_CODES.O)
//    public void addRow(){
//        tr = new TableRow(this);
//        viewList = new ArrayList<TextView>();
//        tv1 = new TextView(this);
//        tv2 = new TextView(this);
//        tv3 = new TextView(this);
//        viewList.add(tv1);
//        viewList.add(tv2);
//        viewList.add(tv3);
//
//        for (TextView e : viewList){
//            e.setText("DEFAULT");
//            e.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//            e.setGravity(Gravity.CENTER);
//            tr.addView(e);
//        }
//        t1.addView(tr);
//
//    }

@TargetApi(26)
    public void addRowFromList() {
        int i = 1;
        for (Delivery_Item x: list){
            tr = new TableRow(this);
            TextView delivery_num = new TextView(this);
            delivery_num.setText(""+i);
            i++;
            delivery_num.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            delivery_num.setGravity(Gravity.CENTER);
            tr.addView(delivery_num);

            TextView order_num = new TextView(this);
            order_num.setText(x.getOrderNumber());
            delivery_num.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            delivery_num.setGravity(Gravity.CENTER);
            tr.addView(order_num);

            final Delivery_Item ptr = x;
            Button details_button = new Button(this);
            details_button.setText("Open");
            details_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setData(ptr);
                    if(isServicesOK()){
                        openMap();
                    }
                }
            });
            tr.addView(details_button);
            t1.addView(tr);
        }
    }

    private void setData(Delivery_Item x){
        order_num = x.getOrderNumber();
        address = x.getOrderString();
        Log.d(TAG, "---------------------------- order_num: " + order_num + " ----------------------------");
        Log.d(TAG, "---------------------------- address: " + address + " ----------------------------");
    }

    private void openMap(){
        Intent intent = new Intent(Address.this, MapActivity.class);
        intent.putExtra("orderNumber", order_num);
        intent.putExtra("orderString", address);
        startActivity(intent);
    }


    private void staticList(){
        ArrayList<Delivery_Item> newList = new ArrayList<Delivery_Item>();
        Delivery_Item item1 = new Delivery_Item("01001000", "1600 Pennsylvania Ave NW Washington, DC 20500 ");
        Delivery_Item item2 = new Delivery_Item("0002", "Seoul, South Korea");
        newList.add(item1);
        newList.add(item2);
        this.list = newList;
    }

//    private void init(){
//        Button openMaps = (Button) findViewById(R.id.openMaps);
//        openMaps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMap();
//            }
//        });
//    }


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
