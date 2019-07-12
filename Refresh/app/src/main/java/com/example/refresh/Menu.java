package com.example.refresh;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.support.constraint.Constraints.TAG;

public class Menu extends AppCompatActivity {

    private FancyButton scan_btn;
    private FancyButton viewOrders;
    private FancyButton editOrders;
    private FancyButton restCall;
    private DatabaseHelper myDb;
    private ArrayList<String> sync_ids = new ArrayList<>();
    private ArrayList<String> sync_signs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myDb = new DatabaseHelper(this);
        scan_btn = findViewById(R.id.scan_btn_open);
        viewOrders = findViewById(R.id.view_route_btn);
        editOrders = findViewById(R.id.edit_orders_btn);
        restCall = findViewById(R.id.call_server_btn);


        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.choose_scan_layout, null);
                FancyButton openCamera = mView.findViewById(R.id.camera_btn);
                FancyButton openExternal = mView.findViewById(R.id.external_btn);
                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(Scanner.class);
                    }
                });

                openExternal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { openActivity(External_Scanner.class);
                    }
                });
                builder.setView(mView);
                builder.show();
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(RecyclerView.class);
            }
        });

        editOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.edit_orders_layout, null);
                FancyButton add = mView.findViewById(R.id.addOrder);
                FancyButton transfer = mView.findViewById(R.id.transfer_orders);
                FancyButton close = mView.findViewById(R.id.close);
                FancyButton reopen = mView.findViewById(R.id.reopen);
                FancyButton adjust = mView.findViewById(R.id.adjust_orders);
                FancyButton void_order = mView.findViewById(R.id.void_orders);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(AddOrders.class);
                    }
                });

                transfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(TransferOrders.class);
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(CloseOrders.class);
                    }
                });

                reopen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(ReopenOrders.class);
                    }
                });

                adjust.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(AdjustOrders.class);
                    }
                });

                void_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(VoidOrder.class);
                    }
                });
                builder.setView(mView);
                builder.show();
            }
        });

        restCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.choose_rest_layout, null);
                FancyButton get_btn = mView.findViewById(R.id.get_call);
                FancyButton post_btn = mView.findViewById(R.id.post_call);

                get_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(RestList.class);
                    }
                });
                post_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: ----------------------------------------------");
                        sync_signs.clear();
                        sync_ids.clear();
                        Cursor cursor = myDb.getAllData();
                        while(cursor.moveToNext()){
                            if(cursor.getInt(4) == Delivery_Item.FAIL_SEND){
                                sync_ids.add(cursor.getString(0));
                                sync_signs.add(cursor.getString(5));
                            }
                        }
                        Log.d(TAG, "onClick: # of loops: " + sync_signs.size());
                        startAsyncTask(v);
                    }
                });
                builder.setView(mView);
                builder.show();
            }
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

        byte[] b = sign.getBytes();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(b);
        fos.flush();
        fos.close();

        return f;
    }

    private class PostConnection extends AsyncTask<Integer, Integer, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Integer... integers) {
            String returnString = "";
            for(int i = 0; i < sync_ids.size(); i++){
                String id = sync_ids.get(i);
                String sign =  sync_signs.get(i);
                Log.d(TAG, "doInBackground: sync_id: " + id);
                Log.d(TAG, "doInBackground: sign: " + sign);

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
                        Log.d(TAG, "doInBackground: Post was UNSUCCESSFUL. PLEASE RECONNECT TO INTERNET.");
                        returnString += "\nPost Request " + i + ": FAILURE, ";
                    }
                    Log.d(TAG, "postResponseHTTP_status: " + postResponse.getCode());
                    Log.d(TAG, "postResponse: " + postResponse.getBody());

                } catch (UnirestException e) {
                    e.printStackTrace();
                    return "THERE IS NO INTERNET CONNECTION";
                }
            }
            returnString += "\nEND OF RESULTS";
            return returnString;
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            Toast.makeText(Menu.this, ""+result, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Post Result: "+result);

        }
    }
}
