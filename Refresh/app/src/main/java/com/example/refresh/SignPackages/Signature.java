package com.example.refresh.SignPackages;

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
import android.util.Log;

import com.example.refresh.Authentication.MainActivity;
import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.Model.EmployeeModel;
import com.example.refresh.Model.PackageModel;
import com.example.refresh.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_BARCODE;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CUSTOMERID;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ITEM;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_QUANTITY;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;
import static com.example.refresh.Model.PackageModel.SELECTED;
import static java.lang.Integer.parseInt;

public class Signature extends Activity {
    private SignaturePad mSignaturePad;
    private FancyButton mClearButton;
    private FancyButton mSaveButton;
    private String recipient = "No Recipient Yet";
    private ArrayList<String> currentOrders = new ArrayList<>();
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        setOrderInformation();
        setupSignaturePad();
    }

    public void startAsycTask(){
        PostConnection post = new PostConnection();
        post.execute();
    }



    public void setOrderInformation(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor rawOrders = databaseHelper.queryAllOrders();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String ordernumber = rawOrders.getString(0);
            String orderRecipient = rawOrders.getString(2);
            int status = rawOrders.getInt(4);
            if(status == SELECTED){
                currentOrders.add(ordernumber);
                if (recipient=="No Recipient Yet") {
                    recipient = orderRecipient;
                }
                else {
                    if(recipient != rawOrders.getString( 2)){
                        showMessage("Warning: ", "Recipients are different. Please re-select orders.");
                    }
                }

            }
        }
    }

    public void setupSignaturePad(){
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
            startAsycTask();
        });
    }

    public File convertImageToFile(String id) throws IOException {
        File file = new File(Signature.this.getCacheDir(), "signature");
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

    private static String getUTCDateTimeAsString() {
        final String DATEFORMATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATEFORMATE);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    private JSONObject createJson(String orderId){
        PackageModel orderInformation = queryData(orderId);
        EmployeeModel employeeInformation = queryEmployeeData();
        if(orderInformation==null){return null;}
        JSONObject jsonObject = new JSONObject();
        JSONObject employee = new JSONObject();
        JSONObject contact = new JSONObject();
        JSONObject receipt = new JSONObject();
        JSONObject reason = new JSONObject();
        try {
            employee.put("firstname", employeeInformation.getFirstname());
            employee.put("lastname", employeeInformation.getLastname());
            employee.put("username", employeeInformation.getUsername());

            jsonObject.put("employee", employee);
            jsonObject.put("printReceipt", true);
            jsonObject.put("signature", encodedImage);
            jsonObject.put("ordernumber", orderInformation.getOrderNumber());
            jsonObject.put("address", orderInformation.getAddress());
            jsonObject.put("customer", orderInformation.getRecipient());
            jsonObject.put("customerId", orderInformation.getCustomerId());
            jsonObject.put("description", "Medium Box/Env/18X12X13");
            jsonObject.put("purposeNumber", "deliver");
            jsonObject.put("barcode", "00000004720580220440");
            jsonObject.put("barcodeType", "UPC");
            jsonObject.put("timestamp", getUTCDateTimeAsString());

            contact.put("firstname", "John");
            contact.put("lastname", "Wick");
            contact.put("contactnumber", "10010010");
            contact.put("active", true);

            jsonObject.put("contact", contact);
            jsonObject.put("note", "insert note here");

            receipt.put("scanned", false);

            reason.put("code", 454);
            reason.put("description", "Address not found");

            receipt.put("reason", reason);

            jsonObject.put("receipt", receipt);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private EmployeeModel queryEmployeeData(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor userData = databaseHelper.queryUser(MainActivity.currentUser);
        Log.d("TAG", "queryEmployeeData: " + MainActivity.currentUser);
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

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Would you like to return to the previous page?");
        builder.setPositiveButton("Yes", (dialog, which) -> returnToPrev());
        builder.setNeutralButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void returnToPrev(){
        Intent intent = new Intent(this, ScannedItems.class);
        intent.putExtra("previousActivity", getIntent().getStringExtra("previousActivity"));
        startActivity(intent);
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private class PostConnection extends AsyncTask<Integer, Integer, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Integer... integers) {
            DatabaseHelper databaseHelper = new DatabaseHelper(Signature.this);

            for(int i = 0; i < currentOrders.size(); i++){
                try {
                    convertImageToFile(currentOrders.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject jsonFile = createJson(currentOrders.get(i));
                Log.d("Signature", "Json Message: \n" + jsonFile.toString());

                try {
                    final HttpResponse<String> postResponse = Unirest.post("http://10.244.185.101:80/signaturesvc/v1/capture")
                            .header("Content-Type", "application/json")
                            .basicAuth("epts_app", "uB25J=UUwU")
                            .body(jsonFile.toString())
                            .asString();

                    if(postResponse.getCode() > 204){
                        databaseHelper.updateStatus(currentOrders.get(i), PackageModel.FAIL_SEND);
                    }
                    else{
                        databaseHelper.updateStatus(currentOrders.get(i), PackageModel.COMPLETE);
                    }

                    return postResponse.getBody();
                } catch (UnirestException e) {
                    e.printStackTrace();
                    databaseHelper.updateStatus(currentOrders.get(i), PackageModel.FAIL_SEND);
                }
            }
            return "Complete";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            Log.d("Result", "onPostExecute: " + result);
            returnToPrev();
        }
    }
}
