package com.example.refresh;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ramijemli.percentagechartview.PercentageChartView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.support.constraint.Constraints.TAG;

public class Menu extends AppCompatActivity {

    private CardView scan_btn;
    private CardView viewOrders;
    private CardView editOrders;
    private CardView restCall;
    private CardView logout;
    private DatabaseHelper myDb;
    private ArrayList<String> sync_ids = new ArrayList<>();
    private ArrayList<String> sync_signs = new ArrayList<>();
    private PercentageChartView currentProgress;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myDb = new DatabaseHelper(this);
        scan_btn = findViewById(R.id.scan_btn_open);
        viewOrders = findViewById(R.id.view_route_btn);
        editOrders = findViewById(R.id.edit_orders_btn);
        restCall = findViewById(R.id.call_server_btn);
        logout = findViewById(R.id.logout_btn);
        currentProgress = findViewById(R.id.current_progress_chart);
        setCurrentProgress();



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
                openExternal.setOnClickListener(v12 -> openActivity(External_Scanner.class));

                builder.setView(mView);
                builder.show();
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });

        viewOrders.setOnClickListener(v -> openActivity(RecyclerView.class));

        editOrders.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.edit_orders_layout, null);
                FancyButton add = mView.findViewById(R.id.addOrder);
                FancyButton transfer = mView.findViewById(R.id.transfer_orders);
                FancyButton close = mView.findViewById(R.id.close);
                FancyButton reopen = mView.findViewById(R.id.reopen);
                FancyButton adjust = mView.findViewById(R.id.adjust_orders);
                FancyButton void_order = mView.findViewById(R.id.void_orders);

                add.setOnClickListener(v13 -> openActivity(AddOrders.class));
                transfer.setOnClickListener(v14 -> openActivity(TransferOrders.class));
                close.setOnClickListener(v15 -> openActivity(CloseOrders.class));
                reopen.setOnClickListener(v16 -> openActivity(ReopenOrders.class));
                adjust.setOnClickListener(v17 -> openActivity(AdjustOrders.class));
                void_order.setOnClickListener(v18 -> openActivity(VoidOrder.class));

                builder.setView(mView);
                builder.show();
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });

        restCall.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = ()-> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.choose_rest_layout, null);
                FancyButton get_btn = mView.findViewById(R.id.get_call);
                FancyButton post_btn = mView.findViewById(R.id.post_call);

                get_btn.setOnClickListener(v19 -> openActivity(RestList.class));
                post_btn.setOnClickListener(v110 -> {
                    sync_signs.clear();
                    sync_ids.clear();

                    Cursor cursor = myDb.getAllData();
                    while(cursor.moveToNext()){
                        if(cursor.getInt(4) == Delivery_Item.FAIL_SEND){
                            sync_ids.add(cursor.getString(0));
                            sync_signs.add(cursor.getString(5));
                        }
                    }
                    startAsyncTask(v110);
                });

                builder.setView(mView);
                builder.show();
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });

        logout.setOnClickListener(v -> {
            Runnable mUpdateTimeTask = () -> {
                Intent intent = new Intent(Menu.this, MainActivity.class);
                intent.putExtra("logout", "logout");
                startActivity(intent);
            };
            mHandler.postDelayed(mUpdateTimeTask, 100);
        });
    }

    private void startAsyncTask(View v) {
        PostConnection post = new PostConnection();
        post.execute();
    }

    public void openActivity(Class nextView){
        Intent intent = new Intent(Menu.this, nextView);
        startActivity(intent);
    }

    //cannot go back to the download page
    @Override
    public void onBackPressed() {
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.O)
    public File convertImageToFile(String id, String sign) throws IOException {
        File f = new File(Menu.this.getCacheDir(), "signature");
        f.createNewFile();

        byte[] b = Base64.decode(sign, Base64.DEFAULT);

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(b);
        fos.flush();
        fos.close();

        return f;
    }

    private void setCurrentProgress(){
        Cursor cursor = myDb.getAllData();
        float completed = 0;
        float total = 0;
        while(cursor.moveToNext()){
            int status = cursor.getInt(4);
            if(status==Delivery_Item.COMPLETE || status==Delivery_Item.FAIL_SEND){
                completed++;
            }
            total++;
        }
        Log.d(TAG, "setCurrentProgress: " + (completed/total)*100);
        currentProgress.setProgress((completed/total)*100, true);
    }

    private class PostConnection extends AsyncTask<Integer, Integer, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Integer... integers) {
            String returnString = "";
            for(int i = 0; i < sync_ids.size(); i++){
                String id = sync_ids.get(i);
                String sign =  sync_signs.get(i);

                File file = null;
                try {
                    file = convertImageToFile(id, sign);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Date date = new Date();
                long time = date.getTime();
                try {
                    final HttpResponse<String> postResponse = Unirest.post("http://10.244.185.101:80/signaturesvc/v1/capture")
                            .basicAuth("epts_app", "uB25J=UUwU")
                            .field("status", "CLOSED")
                            .field("signature", file)
                            .field("shipmentId", id)
                            .field("submissionDate", ""+time).asString();

                    if(postResponse.getCode() == 201 || postResponse.getCode()== 200){
                        myDb.updateStatus(id, 2);
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

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(Menu.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }

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
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                displayAlertMessage("You need to allow access for both positions.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(Menu.this)
                .setTitle("Alert:")
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
