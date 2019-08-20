package com.example.refresh;
/*
Description:
    Setup page to download orders and confirm load.

Specific Functions:
    Download Orders
    Confirm Load
    Post Username

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.ItemModel.ItemModel;
import com.example.refresh.ItemModel.PackageModel;
import com.example.refresh.RetrieveSignatures.SignatureCall;
import com.example.refresh.RetrieveSignatures.SignatureInterface;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.support.constraint.Constraints.TAG;
import static com.example.refresh.ItemModel.PackageModel.INCOMPLETE;
import static java.lang.Integer.parseInt;

public class DownloadPage extends AppCompatActivity {

    /*
    private instance variables
     */
    private FancyButton download_btn;
    private String name;
    private Boolean ordersDownloaded = false;

    /*
    Methods that are executed when this Activity is opened.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_page);
        setTitle();
        setupButtons();
    }

    /*
    Set Title Text
     */
    private void setTitle(){
        TextView title = findViewById(R.id.download_title);
        name = getIntent().getStringExtra("username");
        title.setText("Welcome, " + name);
    }

    /*
    Instantiate Buttons and set their OnClickListeners
     */
    private void setupButtons(){
        download_btn = findViewById(R.id.download);
        download_btn.setOnClickListener(v -> {
            download_btn.setText("Downloading...");
            startAsyncTask();
        });


        FancyButton confirm = findViewById(R.id.confirm_load);
        confirm.setOnClickListener(v -> confirm.setBackgroundColor(getResources().getColor(R.color.success)));


        FancyButton finish = findViewById(R.id.download_continue);
        finish.setOnClickListener(v -> {
            if(ordersDownloaded){
                completeSetup();
            }
            else{
                Toast.makeText(this, "Please Download Orders Before Moving On", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    Completes Setup and opens Manage Route Menu.
     */
    private void completeSetup(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    private void startAsyncTask(){
        GetOrders task = new GetOrders();
        task.execute();
    }

    private class GetOrders extends AsyncTask<Integer, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... integers) {

            try {
                final HttpResponse<JsonNode> getResponse = Unirest.get("http://10.0.2.2:8080/signaturesvc/v1/roadnet/dailyorders")
                        .basicAuth("epts_app", "uB25J=UUwU")
                        .asJson();
                if (getResponse.getCode()!=200){
                    return null;
                }
                JSONObject jsonObject = getResponse.getBody().getObject();
                return jsonObject;

            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(JSONObject result) {
            if(result==null){
                return;
            }
            try {
                DatabaseHelper databaseHelper = new DatabaseHelper(DownloadPage.this);
                databaseHelper.clearTables();
                JSONArray jsonArray = result.getJSONArray("orders");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject order = jsonArray.getJSONObject(i);
                    String orderNumber = order.getString("orderNumber");
                    String address = order.getString("address");
                    String customer = order.getString("customer");
                    String customerId = order.getString("customerId");
                    String cartonNumber = order.getString("cartonNumber");
                    String quantity = order.getString("quantity");
                    JSONArray items = order.getJSONArray("items");
                    ArrayList<ItemModel> itemList = new ArrayList<>();
                    for(int j = 0; j < items.length(); j++){
                        JSONObject item = items.getJSONObject(j);
                        String barcode = item.getString("barcode");
                        String itemName = item.getString("item");
                        String barcodeType = item.getString("barcodeType");
                        itemList.add(new ItemModel(barcode, barcodeType, itemName));
                    }
                    databaseHelper.insertOrder(orderNumber, address, customer, "Placeholder", INCOMPLETE, null, i, parseInt(quantity), cartonNumber);
                }
                ordersDownloaded = true;
                download_btn.setText("Completed Download!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
