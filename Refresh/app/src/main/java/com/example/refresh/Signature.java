package com.example.refresh;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.support.constraint.Constraints.TAG;
import static com.example.refresh.Delivery_Item.SELECTED;

public class Signature extends Activity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private FancyButton mClearButton;
    private FancyButton mSaveButton;
    private static String signatureImage;
    String filepath;
    private String recipient = "No Recipient Yet";
    private static ArrayList<String> currentOrders = new ArrayList<>();
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        myDb = new DatabaseHelper(this);
        setOrderInformation();
        setupSignaturePad();
    }

    public void startAsycTask(View v){
        PostConnection post = new PostConnection();
        post.execute();
    }



    public void setOrderInformation(){
        Cursor rawOrders = myDb.getAllData();
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
                else if(recipient != rawOrders.getString( 2)){
                    showMessage("Error: ", "Recipients are different. Please re-select orders.");
//                    returnToPrev();
                }
                else{
                    //do nothing?
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

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                TextView async = new TextView(Signature.this);
                startAsycTask(async);
                for(String x: currentOrders){
                    Log.d(TAG, "onClick: signatureImage: " + signatureImage);
                    myDb.updateStatus(x, Delivery_Item.COMPLETE);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Signature.this);
                builder.setTitle("Order Successful");
                builder.setMessage("The order has been confirmed.");
                builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnToPrev();
                    }
                });
                builder.show();
            }
        });
    }

    public File convertImageToFile(String id) throws IOException {
        File f = new File(Signature.this.getCacheDir(), "signature");
        f.createNewFile();

        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(b);
        fos.flush();
        fos.close();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        myDb.setSignature(id, encodedImage);
        Log.d(TAG, "convertImageToFile: " + encodedImage);
        signatureImage = encodedImage;
        return f;
    }

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Would you like to return to the previous page?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                returnToPrev();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void returnToPrev(){
        Intent intent = new Intent(this, scannedItems.class);
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
            File file = null;
            try {
                file = convertImageToFile(currentOrders.get(0));
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
                        .field("shipmentId", currentOrders.get(0))
                        .field("submissionDate", ""+time).asString();

                return postResponse.getBody();

            } catch (UnirestException e) {
                e.printStackTrace();
                myDb.updateStatus(currentOrders.get(0), Delivery_Item.FAIL_SEND);
            }
            return "";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d(TAG, "onPostExecute: it went through");
            }
            else{
                Log.d(TAG, "Post Successful: "+result);
            }

        }
    }
}
