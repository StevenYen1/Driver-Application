package com.example.refresh.MainMenu;
/*
Description:
    Main Menu that user navigates from.

Specific Functions:
    Creates buttons and routes that lead to these functions:
        Scan Packages
        View Route
        Edit Orders (Add, Close, Reopen, Transfer, Adjust, Void)
        Call Server (Retrieve SignaturePOST, Sync)
        Sign Out

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.example.refresh.Authentication.MainActivity;
import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.EditOrders.AddOrders;
import com.example.refresh.EditOrders.AdjustOrders;
import com.example.refresh.EditOrders.CloseOrders;
import com.example.refresh.EditOrders.ReopenOrders;
import com.example.refresh.EditOrders.TransferOrders;
import com.example.refresh.EditOrders.VoidOrder;
import com.example.refresh.Model.PackageModel;
import com.example.refresh.OrderDisplay.ViewOrders;
import com.example.refresh.R;
import com.example.refresh.RetrieveSignatures.SignatureInterface;
import com.example.refresh.ScanPackages.ExternalScanner;
import com.example.refresh.ScanPackages.Scandit;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.json.JSONObject;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_STATUS;

public class Menu extends AppCompatActivity {

    /*
    private instance variables
     */
    private DatabaseHelper databaseHelper;
    private ArrayList<String> sync_ids = new ArrayList<>();
    private Handler mHandler = new Handler();

    /*
    Methods that are executed when this Activity is opened.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        databaseHelper = new DatabaseHelper(this);
        setCurrentProgress();
        setupScanBtn();
        setupViewOrders();
        setupEditOrdersBtn();
        setupRestCallBtn();
        setupLogoutBtn();
    }

    /*
    Instantiate Scan Packages button and set OnClickListener
     */
    private void setupScanBtn(){
        CardView scan_btn = findViewById(R.id.scan_btn_open);
        scan_btn.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.choose_scan_layout, null);
                FancyButton openCamera = mView.findViewById(R.id.camera_btn);
                FancyButton openExternal = mView.findViewById(R.id.external_btn);

                openCamera.setOnClickListener(v1 -> {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(checkPermission()){
                            Toast.makeText(Menu.this, "Permission is granted!", Toast.LENGTH_LONG).show();
                            openActivity(Scandit.class);
                        }
                        else{
                            requestPermission();
                        }
                    }
                    else{
                        openActivity(Scandit.class);
                    }
                });
                openExternal.setOnClickListener(v2 -> openActivity(ExternalScanner.class));
                builder.setView(mView);
                builder.show();
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });
    }

    /*
    Instantiate View Route button and set OnClickListener
     */
    private void setupViewOrders(){
        CardView viewOrders = findViewById(R.id.view_route_btn);
        viewOrders.setOnClickListener(v -> openActivity(ViewOrders.class));
    }

    /*
    Instantiate Edit Orders button and set OnClickListener
     */
    private void setupEditOrdersBtn(){
        CardView editOrders = findViewById(R.id.edit_orders_btn);
        editOrders.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.edit_orders_layout, null);

                FancyButton add = mView.findViewById(R.id.addOrder);
                add.setOnClickListener(v1 -> openActivity(AddOrders.class));

                FancyButton transfer = mView.findViewById(R.id.transfer_orders);
                transfer.setOnClickListener(v1 -> openActivity(TransferOrders.class));

                FancyButton close = mView.findViewById(R.id.close);
                close.setOnClickListener(v1 -> openActivity(CloseOrders.class));

                FancyButton reopen = mView.findViewById(R.id.reopen);
                reopen.setOnClickListener(v1 -> openActivity(ReopenOrders.class));

                FancyButton adjust = mView.findViewById(R.id.adjust_orders);
                adjust.setOnClickListener(v1 -> openActivity(AdjustOrders.class));

                FancyButton void_order = mView.findViewById(R.id.void_orders);
                void_order.setOnClickListener(v1 -> openActivity(VoidOrder.class));

                builder.setView(mView);
                builder.show();
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });
    }

    /*
    Instantiate Call Server button and set OnClickListener
     */
    private void setupRestCallBtn(){
        CardView restCall = findViewById(R.id.call_server_btn);
        restCall.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = ()-> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.choose_rest_layout, null);
                FancyButton get_btn = mView.findViewById(R.id.get_call);
                FancyButton post_btn = mView.findViewById(R.id.post_call);

                get_btn.setOnClickListener(v1 -> openActivity(SignatureInterface.class));
                post_btn.setOnClickListener(v1 -> {
                    sync_ids.clear();

                    Cursor cursor = databaseHelper.queryAllOrders();
                    while(cursor.moveToNext()){
                        if(cursor.getInt(COL_STATUS) == PackageModel.FAIL_SEND){
                            sync_ids.add(cursor.getString(COL_ORDERNUMBER));
                        }
                    }
                    startAsyncTask();
                });

                builder.setView(mView);
                builder.show();
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });
    }

    /*
    Instantiate Sign Out button and set OnClickListener
     */
    private void setupLogoutBtn(){
        CardView logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> {
                Intent intent = new Intent(Menu.this, MainActivity.class);
                intent.putExtra("logout", "logout");
                startActivity(intent);
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });
    }

    /*
    Executes actual post command
     */
    private void startAsyncTask() {
        SyncSignatures post = new SyncSignatures();
        post.execute();
    }

    /*
    Generic method for opening an activity.
     */
    public void openActivity(Class nextView){
        Intent intent = new Intent(Menu.this, nextView);
        startActivity(intent);
    }

    /*
    Overrides ability to return to download page
     */
    @Override
    public void onBackPressed() {
    }

    /*
    Sets the graphic for the "Current Delivery Progress" based on completed orders
     */
    private void setCurrentProgress(){
       PercentageChartView currentProgress = findViewById(R.id.current_progress_chart);
        Cursor cursor = databaseHelper.queryAllOrders();
        float completed = 0;
        float total = 0;
        while(cursor.moveToNext()){
            int status = cursor.getInt(4);
            if(status== PackageModel.COMPLETE || status== PackageModel.FAIL_SEND){
                completed++;
            }
            total++;
        }
        if(total == 0) {
            currentProgress.setProgress(100, true);
        }
        else{
            currentProgress.setProgress((completed/total)*100, true);
        }
    }

    /*
    creates the json file needed to send to the REST api
     */
    private JSONObject createJson(String orderId){
        //This is where the json would be constructed.
        //The code was removed for legal purposes
        return new JSONObject();
    }

    /*
    Post route to rest api.
    Stores signature information.
     */
    private class SyncSignatures extends AsyncTask<Integer, Integer, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Integer... integers) {
            String returnString = "";
            for(int i = 0; i < sync_ids.size(); i++){
                String id = sync_ids.get(i);

                try {
                    final HttpResponse<String> postResponse = Unirest.post("signature rest api endpoint")
                            .basicAuth("mockUsername", "mockPassword")
                            .body(createJson(sync_ids.get(i)).toString())
                            .asString();

                    if(postResponse.getCode() == 201 || postResponse.getCode()== 200){
                        databaseHelper.updateStatus(id, 2);
                        returnString += "\nPost Request " + i + ": SUCCESS, ";
                    }
                    else{
                        returnString += "\nPost Request " + i + ": FAILURE, ";
                    }

                } catch (UnirestException e) {
                    e.printStackTrace();
                    return "THERE IS NO INTERNET CONNECTION";
                }
            }
            if(returnString.isEmpty()){
                return "THERE IS NOTHING TO SYNC";
            }
            returnString += "\nEND OF RESULTS";
            return "SYNC COMPLETE. RESULTS DOWN BELOW: \n\n"+returnString;
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            Toast.makeText(Menu.this, ""+result, Toast.LENGTH_LONG).show();

        }
    }

    /*
    Permission Check for camera.
     */
    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    /*
    Executes camera permission request for the user.
     */
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    /*
    Checks if permission has been granted before opening Scandit class
     */
    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]){
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(Menu.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        openActivity(Scandit.class);
                    }
                    else{
                        Toast.makeText(Menu.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
