package com.example.refresh.SignPackages;
/*
Description:
    The purpose of this class is to capture a customer signature and store it in an online REST api.

Specific Features:
    Captures SignaturePOST
    Sends POST request to online REST api.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.example.refresh.AlertDialogs.StandardMessage;
import com.example.refresh.Authentication.MainActivity;
import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Model.EmployeeModel;
import com.example.refresh.Model.PackageModel;
import com.example.refresh.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_BARCODE;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CUSTOMERID;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_STATUS;
import static com.example.refresh.Model.PackageModel.SELECTED;
import static java.lang.Integer.parseInt;

public class SignaturePOST extends Activity {
    /*
    private instance variables
     */
    private SignaturePad mSignaturePad;
    private FancyButton mClearButton;
    private FancyButton mSaveButton;
    private String recipient = "No Recipient Yet";
    private ArrayList<String> currentOrders = new ArrayList<>();
    private String encodedImage;

    /*
    Methods that occur on startup of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        setOrderInformation();
        setupSignaturePad();
    }

    /*
    Starts the async task that posts the signature to the REST api.
     */
    public void startAsyncTask(){
        StoreSignature post = new StoreSignature();
        post.execute();
    }


    /*
    Grabs the information from the 'order_table' to see which have been processed and are ready to sign.
     */
    private void setOrderInformation(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor queryResult = databaseHelper.queryAllOrders();
        if(queryResult.getCount() == 0){
            return;
        }
        while(queryResult.moveToNext()){
            String orderNumber = queryResult.getString(COL_ORDERNUMBER);
            String orderRecipient = queryResult.getString(COL_RECIPIENT);
            int status = queryResult.getInt(COL_STATUS);
            if(status == SELECTED){
                currentOrders.add(orderNumber);
                if (recipient.equals("No Recipient Yet")) {
                    recipient = orderRecipient;
                }
                else {
                    if(!recipient.equals(orderRecipient)){
                        StandardMessage standardMessage = new StandardMessage(SignaturePOST.this);
                        standardMessage.buildStandardMessage("Warning: ","Recipients are different. Please re-select orders." ).show();
                    }
                }

            }
        }
    }

    /*
    SignaturePOST pad setup. Creates pad OnSignedListener and button OnClickedListeners.
     */
    private void setupSignaturePad(){
        mSignaturePad = findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = findViewById(R.id.clear_button);
        mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setEnabled(false);
        mClearButton.setEnabled(false);

        mClearButton.setOnClickListener(view -> mSignaturePad.clear());

        mSaveButton.setOnClickListener(view -> {
            startAsyncTask();
        });
    }

    /*
    In a previous version of the product, we outputted the signature as a file.
    The file was not used in later versions, but was still kept in case ever needed.
     */
    private File convertImageToFile(String id) throws IOException {
        File file = new File(SignaturePOST.this.getCacheDir(), "signature");
        file.createNewFile();

        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(b);
        fos.flush();
        fos.close();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.updateSignature(id, encodedImage);
        databaseHelper.close();
        this.encodedImage = encodedImage;
        return file;
    }

    /*
    creates the json file needed to send to the REST api
     */
    private JSONObject createJson(String orderId){
        PackageModel orderInformation = queryData(orderId);
        EmployeeModel employeeInformation = queryEmployeeData();
        if(orderInformation==null){return null;}
        JSONObject jsonObject = new JSONObject();
        //This is where the json would be constructed.
        //The code was removed for legal purposes
        return jsonObject;
    }

    /*
    Queries the 'user_table' for the current user and stores information in an EmployeeModel
     */
    private EmployeeModel queryEmployeeData(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor userData = databaseHelper.queryUser(MainActivity.currentUser);
        while(userData.moveToNext()){
            String username = userData.getString(0);
            String firstname = userData.getString(2);
            String lastname = userData.getString(3);

            EmployeeModel currentEmployee = new EmployeeModel(firstname, lastname, username);
            databaseHelper.close();
            return currentEmployee;
        }
        databaseHelper.close();
        return new EmployeeModel("null", "null", "null");
    }

    /*
    Queries the 'order_table' based on an order number and stores it in a PackageModel
     */
    private PackageModel queryData(String orderId){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.queryOrder(orderId);
        while(data.moveToNext()){
            String address = data.getString(COL_ADDRESS);
            String recipient = data.getString(COL_RECIPIENT);
            String item = data.getString(COL_ITEM);
            String quantity = data.getString(COL_QUANTITY);
            String cartonNumber = data.getString(COL_CARTONNUMBER);
            String barcode = data.getString(COL_BARCODE);
            String customerId = data.getString(COL_CUSTOMERID);
            PackageModel packageModel = new PackageModel(orderId, address, recipient, item, PackageModel.COMPLETE, parseInt(quantity), cartonNumber, barcode, customerId);
            databaseHelper.close();
            return packageModel;
        }
        databaseHelper.close();
        return null;
    }

    /*
    Overrides usual onBackPressed method. Prompts the user if they would like to return to the scannedItems page.
     */
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Would you like to return to the previous page?");
        builder.setPositiveButton("Yes", (dialog, which) -> returnToPrev());
        builder.setNeutralButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /*
    Returns to the previous activity. Continues to store which scanner was most recently used.
     */
    public void returnToPrev(){
        Intent intent = new Intent(this, ScannedItems.class);
        intent.putExtra("previousActivity", getIntent().getStringExtra("previousActivity"));
        startActivity(intent);
    }

    /*
    Internal class that creates a POST call to the signature REST service,
    storing the SignaturePOST and Order related information.
     */
    private class StoreSignature extends AsyncTask<Integer, Integer, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Integer... integers) {
            DatabaseHelper databaseHelper = new DatabaseHelper(SignaturePOST.this);
            StringBuilder responseString = new StringBuilder();
            for(int i = 0; i < currentOrders.size(); i++){
                try {
                    convertImageToFile(currentOrders.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject jsonFile = createJson(currentOrders.get(i));
                try {
                    final HttpResponse<String> postResponse = Unirest.post("url of endpoint")
                            .header("Content-Type", "application/json")
                            .basicAuth("mockUsername", "mockPassword")
                            .body(jsonFile.toString())
                            .asString();
                    databaseHelper.updateStatus(currentOrders.get(i), PackageModel.COMPLETE);
                    responseString.append(postResponse.getBody()).append("\n\n");

                } catch (UnirestException e) {
                    e.printStackTrace();
                    databaseHelper.updateStatus(currentOrders.get(i), PackageModel.FAIL_SEND);
                }
            }
            return responseString.toString();
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            returnToPrev();
        }
    }
}
