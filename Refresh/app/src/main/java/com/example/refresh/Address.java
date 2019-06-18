package com.example.refresh;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class Address extends AppCompatActivity implements Serializable {

    private static final String TAG = "Address";

    private static final int ERROR_DIALOG_REQUEST = 9001;



    private static final String ORDER_NUMBER_DEFAULT = "0";
    private static final String ADDRESS_DEFAULT = "Massachusetts";
    private static final String STATUS = "N/A";
    private ArrayList<Delivery_Item> list;
    private ArrayList<String> incompleteOrders;
    private ArrayList<String> completedOrders;
    private ArrayList<String> allOrders = new ArrayList<>();
    private String order_num = ORDER_NUMBER_DEFAULT;
    private String address = ADDRESS_DEFAULT;
    private String status = STATUS;
    private String item = "N/A";
    private String recipient = "N/A";
    private String signature = "No Signature Yet";
    private TableLayout t1;
    private TableRow tr;
    private TextView tableTitle;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        myDb = new DatabaseHelper(this);

        incompleteOrders = getIntent().getStringArrayListExtra("remainingOrders");
        completedOrders = getIntent().getStringArrayListExtra("completedOrders");
        if(completedOrders == null){
            completedOrders = new ArrayList<>();
        }
        if(incompleteOrders == null){
            incompleteOrders = new ArrayList<>();
        }
        allOrders.addAll(incompleteOrders);
        allOrders.addAll(completedOrders);

        Button button = findViewById(R.id.scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });

        Resources res = this.getResources();

        tableTitle = findViewById(R.id.table_title);
        tableTitle.setText(String.format(res.getString(R.string.DeliveriesDate), returnDate()));


        t1 = findViewById(R.id.Table);
        makeTableHeader();
        addRowFromListNew();

    }

@TargetApi(26)
    private void makeTableHeader(){

        tr = new TableRow(this);

        TextView status = new TextView(this);
        status.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        status.setText(this.getString(R.string.status)+"  ");
        status.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        tr.addView(status);

        TextView order_num = new TextView(this);
        order_num.setText(this.getString(R.string.table_order_num));
        order_num.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        order_num.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        tr.addView(order_num);

        t1.addView(tr);
    }

    @Override
    public void onBackPressed(){

    }

    @TargetApi(26)
    public void addRowFromListNew() {
        for (final String x: allOrders){
            tr = new TableRow(this);

            TextView status = new TextView(this);
            status.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            status.setGravity(Gravity.CENTER);
            if(incompleteOrders.contains(x)){
                status.setText("X");
                status.setTextColor(0xFFa70000);
            }
            else{
                status.setTextColor(0xFF008C00);
                status.setText("O");
            }
            tr.addView(status);

            TextView order_num = new TextView(this);
            final String address_id = getAddress(x);
            String order_info = String.format(
                    this.getResources().getString(R.string.orderInfo), x, address_id);

            order_num.setText(order_info);
            order_num.setWidth(MATCH_PARENT);
            order_num.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            tr.addView(order_num);


            tr.setBackgroundResource(R.drawable.rowclick);
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isServicesOK()){
                        viewInstance(x,address_id);
                    }
                }
            });

            t1.addView(tr);
        }
    }

    private String returnDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    private void openMap(String id){
        Intent intent = new Intent(Address.this, MapActivity.class);
        intent.putExtra("orderString", id);
        startActivity(intent);
    }

    private void openScanner(){
        Intent intent = new Intent(Address.this, Scanner.class);
        intent.putExtra("remainingOrders", incompleteOrders);
        intent.putExtra("completedOrders", completedOrders);
        startActivity(intent);
    }

    public void viewInstance(String id, String address){
        Cursor res = myDb.getInstance(id);
        if(res.getCount() == 0){
            showMessage("Error", "No Data Found");
            return;
        }

        StringBuffer buffer = new StringBuffer();

        while (res.moveToNext()) {
            buffer.append("Order number: " + res.getString(0)+"\n");
            buffer.append("Address: " + res.getString(1)+"\n");
            buffer.append("Recipient: " + res.getString(2)+"\n");
            buffer.append("Item: " + res.getString(3)+"\n");
            buffer.append("Status: " + res.getString(4)+"\n");
            buffer.append("Sign: " + res.getString(5)+"\n");
            buffer.append("\n");
        }

        showMessageMap("Order Information", buffer.toString(), address);

    }

    public String getAddress(String id){
        Cursor res = myDb.getInstance(id);
        String returnString = "";
        if(res.getCount() == 0){
            showMessage("Error", "No Data Found");
            return "";
        }
        while (res.moveToNext()) {
            returnString=res.getString(1);

        }
        return returnString;
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.setMessage(message);
        builder.show();
    }


    public void showMessageMap(String title, String message, final String id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setPositiveButton("Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                openMap(id);
            }
        });
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.setMessage(message);
        builder.show();
    }

//    public String readFile(String file){
//        String text = "";
//
//        try{
//            FileInputStream fis = openFileInput(file);
//            int size = fis.available();
//            byte[] buffer = new byte[size];
//            fis.read(buffer);
//            fis.close();
//            text = new String(buffer);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return text;
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
