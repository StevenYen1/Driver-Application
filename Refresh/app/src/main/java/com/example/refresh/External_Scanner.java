package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.COL_STATUS;
import static com.example.refresh.Delivery_Item.COMPLETE;
import static com.example.refresh.Delivery_Item.FAIL_SEND;
import static com.example.refresh.Delivery_Item.INCOMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class External_Scanner extends AppCompatActivity {

    private String orders_status;
    private String recentId = "";
    private DatabaseHelper myDb;
    private String Barcode="";
    private EditText input;
    private FancyButton sideMenu;
    private Handler mHandler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
        setContentView(R.layout.activity_external_scanner);
        sideMenu = findViewById(R.id.external_sideMenu);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setInput();
        TextView mostRecent = findViewById(R.id.recent_id);
        mostRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRecentInformation();
            }
        });

    }

    public void viewRecentInformation(){
        if(recentId.equals("")){
            Toast.makeText(this, "Please scan a package first.", Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.newdetails_layout, null);
            builder.setCancelable(true);

            Cursor cursor = myDb.queryOrder(recentId);
            while (cursor.moveToNext()) {
                String ordernum = cursor.getString(0);
                String address = cursor.getString(1);
                String recipient = cursor.getString(2);
                String item = cursor.getString(3);
                int quantity = cursor.getInt(7);
                String cartonnum = cursor.getString(8);

                TextView ordernum_view = mView.findViewById(R.id.newdetails_ordernum);
                TextView cartonnum_view = mView.findViewById(R.id.newdetails_cartonnum);
                TextView address_view = mView.findViewById(R.id.newdetails_address);
                TextView recipient_view = mView.findViewById(R.id.newdetails_recipient);
                TextView item_view = mView.findViewById(R.id.newdetails_item);
                TextView quantity_view = mView.findViewById(R.id.newdetails_quantity);
                FancyButton mapBtn = mView.findViewById(R.id.newdetails_map);

                ordernum_view.setText("Order Number: " + ordernum);
                cartonnum_view.setText("Carton Number: " + cartonnum);
                address_view.setText(address);
                recipient_view.setText(recipient);
                item_view.setText(item);
                quantity_view.setText("" + quantity);
                mapBtn.setOnClickListener(v -> openMap(address));

                builder.setView(mView);
                AlertDialog dialog = builder.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    public void openMap(String id){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("orderString", id);
        startActivity(intent);
    }

    public void setInput(){
        input = findViewById(R.id.editText);
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN && (keyCode==301 || keyCode == 302 || keyCode == 303)){
                    Barcode = input.getText().toString();
                }
                if(event.getAction()==KeyEvent.ACTION_UP && (keyCode==301 || keyCode == 302 || keyCode == 303)){
                    input.getText().clear();
                    handleResult(Barcode);
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.scanner_done, null);

        FancyButton exit = mView.findViewById(R.id.Exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mUpdateTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        displayAlertMessage("This will discard all saved scans. Do you still wish to continue?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goBack();
                            }
                        });
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });

        FancyButton orders = mView.findViewById(R.id.orders);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mUpdateTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        viewAll();
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });

        FancyButton done = mView.findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScannedItems();
            }
        });
        builder.setView(mView);
        final AlertDialog dialog = builder.show();

        FancyButton continueB = mView.findViewById(R.id.Continue);
        continueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mUpdateTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                };
                mHandler.postDelayed(mUpdateTimeTask, 250);
            }
        });

    }

    public void openScannedItems(){
        Intent intent = new Intent(this, scannedItems.class);
        intent.putExtra("previousActivity", "e");
        startActivity(intent);
    }

    public void goBack(){
        Intent intent = new Intent(this, Menu.class);
        Cursor rawOrders = myDb.queryAllOrders();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String id = rawOrders.getString(0);
            int status = rawOrders.getInt(4);
            if(status == SCANNED || status == SELECTED){
                myDb.updateStatus(id, INCOMPLETE);
            }
        }
        startActivity(intent);
    }

    public void viewAll(){
        Cursor rawOrders = myDb.queryAllOrders();
        if(rawOrders.getCount() == 0){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (rawOrders.moveToNext()) {
            int status = rawOrders.getInt(4);
            if(status == COMPLETE || status == FAIL_SEND){
                buffer.append("Current Status: COMPLETED\n");
            }
            else if(status == SCANNED){
                buffer.append("Current Status: SCANNED\n");
            }
            else if(status == SELECTED){
                buffer.append("Current Status: SELECTED\n");
            }
            else{
                buffer.append("Current Status: INCOMPLETE\n");
            }
            buffer.append("--------------------------------------------------------\n");
            buffer.append("Order number: " + rawOrders.getString(0)+"\n");
            buffer.append("Address: " + rawOrders.getString(1)+"\n");
            buffer.append("Recipient: " + rawOrders.getString(2)+"\n");
            buffer.append("Item: " + rawOrders.getString(3)+"\n");
            buffer.append("Quantity: " + rawOrders.getInt(7)+"\n");
            buffer.append("Carton Number: " + rawOrders.getString(8)+"\n");
            buffer.append("--------------------------------------------------------\n");
            buffer.append("\n");
            buffer.append("\n");
        }
        orders_status = buffer.toString();
        showMessage("Order Information", orders_status);
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage(message);
        builder.show();
    }


    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(External_Scanner.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void handleResult(String scanResult) {
        Cursor cursor = myDb.queryOrder(scanResult);
        int status = -1;
        if(cursor!=null){
            status = cursor.getInt(COL_STATUS);
        }

        if(status == INCOMPLETE){
            myDb.updateStatus(scanResult, SCANNED);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.success_layout, null);
            TextView scannedOrder = mView.findViewById(R.id.success_ordernum);
            scannedOrder.setText("Order Number: " + scanResult);
            dialog.setView(mView);

            final AlertDialog alert = dialog.create();
            alert.show();

            WindowManager.LayoutParams lp = alert.getWindow().getAttributes();
            lp.dimAmount=0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            alert.getWindow().setAttributes(lp);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alert.isShowing()) {
                        alert.dismiss();
                        lp.dimAmount = 0.0f;
                        alert.getWindow().setAttributes(lp);
                        recentId = scanResult;
                    }
                }
            };

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 2000);
        }
    }
}
