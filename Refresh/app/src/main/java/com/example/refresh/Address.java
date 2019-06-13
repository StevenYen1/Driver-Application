package com.example.refresh;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
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

import java.io.Serializable;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Address extends AppCompatActivity implements Serializable {

    private static final String TAG = "Address";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    private static final String ORDER_NUMBER_DEFAULT = "0";
    private static final String ADDRESS_DEFAULT = "Massachusetts";
    private static final String STATUS = "N/A";
    private ArrayList<Delivery_Item> list;
    private ArrayList<String> completedOrders = new ArrayList<String>();
    private String order_num = ORDER_NUMBER_DEFAULT;
    private String address = ADDRESS_DEFAULT;
    private String status = STATUS;
    private String item = "N/A";
    private String recipient = "N/A";
    private TableLayout t1;
    private TableRow tr;
    private TextView tableTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        staticList();

        Resources res = this.getResources();

        tableTitle = findViewById(R.id.table_title);
        tableTitle.setText(String.format(res.getString(R.string.DeliveriesDate), returnDate()));


        t1 = findViewById(R.id.Table);
        makeTableHeader();
        addRowFromList();

//        if(isServicesOK()){
//            init();
//        }
    }

@TargetApi(26)
    private void makeTableHeader(){
        tr = new TableRow(this);
        TextView status = new TextView(this);
        status.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        status.setText(this.getString(R.string.status));
        status.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        status.setGravity(Gravity.RIGHT);
        tr.addView(status);

        TextView order_num = new TextView(this);
        order_num.setText(this.getString(R.string.table_order_num));
        order_num.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        order_num.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        order_num.setGravity(Gravity.CENTER);
        tr.addView(order_num);

        TextView details = new TextView(this);
        details.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        details.setText(this.getString(R.string.table_details));
        details.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        tr.addView(details);

        t1.addView(tr);
    }

    @Override
    public void onBackPressed(){

    }


@TargetApi(26)
    public void addRowFromList() {
        for (Delivery_Item x: list){
            tr = new TableRow(this);
            TextView status = new TextView(this);
            status.setText(x.getStatus());
            status.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            status.setGravity(Gravity.RIGHT);
            if(x.getStatus().equals(this.getString(R.string.Done))){
                status.setTextColor(0xFF008C00);
            }
            tr.addView(status);

            TextView order_num = new TextView(this);
            order_num.setText(x.getOrderNumber());
            order_num.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            order_num.setGravity(Gravity.CENTER);
            tr.addView(order_num);

            final Delivery_Item ptr = x;
            Button details_button = new Button(this);
            details_button.setText(this.getString(R.string.open));
            details_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setData(ptr);
                    if(isServicesOK()){
                        openMap(v);
                    }
                }
            });
            tr.addView(details_button);
            t1.addView(tr);
        }
    }

    private String returnDate(){
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    private void setData(Delivery_Item x){
        order_num = x.getOrderNumber();
        address = x.getOrderString();
        recipient = x.getRecipient();
        status = x.getStatus();
        item = x.getItem();



    }

    private void openMap(View v){
        Intent intent = new Intent(Address.this, MapActivity.class);
        intent.putExtra("orderNumber", order_num);
        intent.putExtra("orderString", address);
        intent.putExtra("status", status);
        intent.putExtra("item", item);
        intent.putExtra("recipient", recipient);
        intent.putExtra("completedOrders", completedOrders);
        intent.putExtra("signature", this.getIntent().getStringExtra("signature"));
        startActivity(intent);
    }


    private void staticList(){
        Delivery_Item item1 = new Delivery_Item("0100100010", "1600 Pennsylvania Ave NW Washington, DC 20500 ", "President of the US", "How to Tweet 101");
        Delivery_Item item2 = new Delivery_Item("0002100034", "Seoul, South Korea");
        Delivery_Item item3 = new Delivery_Item("6112019555", "Champ de Mars, Paris, Ile de France 75007");
        Delivery_Item item4 = new Delivery_Item("0101010101", "Los Angeles, California, United States");
        Delivery_Item item5 = new Delivery_Item("5000000000", "50 Vassar St, Cambridge, Massachusetts 02139, United States");
        Delivery_Item item6 = new Delivery_Item("1231231231", "924 Avenue J East, Grand Prairie, TX 75050");
        Delivery_Item item7 = new Delivery_Item("5749403-21", "908 Massachusetts Ave, Arlington, MA 02476, United States");
        Delivery_Item item8 = new Delivery_Item("1-342351-1", "65 Harrison Ave Ste 306, Boston, MA 02111");
        Delivery_Item item9 = new Delivery_Item("6754456321", "124 Beach St, Ogunquit, ME 03907, United States");
        Delivery_Item item10 = new Delivery_Item("9312341129", "〒150-8010東京都渋谷区", "Tetsuya Nomura", "Final Fantasy 7 Remake");
        Delivery_Item item11 = new Delivery_Item("5512345555", "500 Staples Drive, Framingham, MA 01702", "Saar Picker", "A Hardworking Intern");
        Delivery_Item item12 = new Delivery_Item("8888888888", "211 Arlington Street, Acton MA 01720");

        ArrayList<Delivery_Item> newList = new ArrayList<Delivery_Item>();

        newList.add(item1);
        newList.add(item2);
        newList.add(item3);
        newList.add(item4);
        newList.add(item5);
        newList.add(item6);
        newList.add(item7);
        newList.add(item8);
        newList.add(item9);
        newList.add(item10);
        newList.add(item11);
        newList.add(item12);

        completedOrders = this.getIntent().getStringArrayListExtra("completedOrders");
        if(completedOrders != null) {
            for (Delivery_Item x : newList){
                if(completedOrders.contains(x.getOrderNumber())){
                    x.setStatus("Delivered");
                }
            }
        }

        this.list = newList;
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
