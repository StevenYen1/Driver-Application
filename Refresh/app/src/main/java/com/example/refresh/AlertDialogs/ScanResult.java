package com.example.refresh.AlertDialogs;
/*
Description:
    The goal of this class is to check if the scanned barcode exists in the driver's list of remaining orders.
    If it does, the order is marked as "SCANNED" and this class creates a custom Success Popup Window.

Specific Features:
    Status Check.
    Success Popup Window Creation.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.R;
import com.example.refresh.ScanPackages.Scandit;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_STATUS;
import static com.example.refresh.ItemModel.PackageModel.SCANNED;
import static com.example.refresh.ScanPackages.Scandit.getScanner;

public class ScanResult {

    private Context context;
    private String orderId;

    public ScanResult(Context context, String orderId){
        this.context = context;
        this.orderId = orderId;
    }

    public void checkScan(){
        Cursor queryResult = getOrderDetails();
        if(isIncomplete(queryResult)){
            if(context instanceof Scandit){ getScanner().pauseScanning(); }
            buildSuccessWindow();
            statusScanned();
        }
    }

    private Cursor getOrderDetails(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.queryOrder(orderId);
    }

    private boolean isIncomplete(Cursor queryResult){
        while(queryResult.moveToNext()){
            if(queryResult.getInt(COL_STATUS)==0){
                return true;
            }
        }
        return false;
    }

    private void statusScanned(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.updateStatus(orderId, SCANNED);
    }

    private void buildSuccessWindow(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = inflater.inflate(R.layout.success_layout, null);
        TextView scannedOrder = mView.findViewById(R.id.success_ordernum);
        scannedOrder.setText("Order Number: " + orderId);
        dialog.setView(mView);
        AlertDialog alert = dialog.create();
        alert.show();

        WindowManager.LayoutParams lp = alert.getWindow().getAttributes();
        lp.dimAmount=0.8f;
        alert.getWindow().setAttributes(lp);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Handler handler = new Handler();
        Runnable runnable = () -> {
            if (alert.isShowing()) {
                alert.dismiss();
                lp.dimAmount = 0.0f;
                alert.getWindow().setAttributes(lp);
                if(context instanceof Scandit){
                    getScanner().resumeScanning();
                }
            }
        };
        alert.setOnDismissListener(dialog1 -> handler.removeCallbacks(runnable));
        handler.postDelayed(runnable, 2000);
    }
}
