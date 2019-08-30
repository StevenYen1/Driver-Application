package com.example.refresh.RetrieveOrders;
/*
Description:
    Setup page to download orders and confirm load.

Specific Functions:
    Download Orders
    Confirm Load

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Model.ItemModel;
import com.example.refresh.MainMenu.Menu;
import com.example.refresh.Model.PackageModel;
import com.example.refresh.R;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.Model.PackageModel.INCOMPLETE;
import static com.example.refresh.Model.PackageModel.SCANNED;
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

    /*
    Starts the async task that gets the orders from the REST api.
     */
    private void startAsyncTask(){
        GetOrders task = new GetOrders();
        task.execute();
    }

    /*
    private internal class that makes a GET request to get daily orders and routes for drivers.
     */
    private class GetOrders extends AsyncTask<Integer, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... integers) {

            try {
                final HttpResponse<JsonNode> getResponse = Unirest.get("url endpoint for orders")
                        .basicAuth("mockUsername", "mockPassword")
                        .asJson();
                /*
                For the server side, the response code was set to be HTTP.OK (200)
                 */
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
                /*
                Here, there were multiple fields parsed from the json object obtained from the REST api.
                However, they have been replaced to avoid legal action.
                 */
                JSONArray jsonArray = result.getJSONArray("root");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject order = jsonArray.getJSONObject(i);
                    String orderNumber = "Parsed from Json";
                    String address = "Parsed from Json";
                    String customer = "Parsed from Json";
                    String customerId = "Parsed from Json";
                    String cartonNumber = "Parsed from Json";
                    String quantity = "Parsed from Json";
                    String trackingBarcode = "Parsed from Json";
                    String items = "Parsed from Json";

                    databaseHelper.insertOrder(orderNumber, address, customer, items, INCOMPLETE, null, i, parseInt(quantity), cartonNumber, trackingBarcode, customerId);
                }
                ordersDownloaded = true;
                download_btn.setText("Completed Download!");
                download_btn.setBackgroundColor(getResources().getColor(R.color.success));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
