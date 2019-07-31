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
import android.view.View;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.ItemModel.PackageModel;
import com.example.refresh.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
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

import static com.example.refresh.ItemModel.PackageModel.SELECTED;

public class Signature extends Activity {
    private SignaturePad mSignaturePad;
    private FancyButton mClearButton;
    private FancyButton mSaveButton;
    private String recipient = "No Recipient Yet";
    private ArrayList<String> currentOrders = new ArrayList<>();
    private DatabaseHelper myDb;

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
        Cursor rawOrders = myDb.queryAllOrders();
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

        mClearButton.setOnClickListener(view -> mSignaturePad.clear());

        mSaveButton.setOnClickListener(view -> {
            TextView async = new TextView(Signature.this);
            startAsycTask(async);
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
        myDb.updateSignature(id, encodedImage);
        return file;
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

            for(int i = 0; i < currentOrders.size(); i++){
                File file = null;
                try {
                    file = convertImageToFile(currentOrders.get(i));
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
                            .field("shipmentId", currentOrders.get(i))
                            .field("submissionDate", ""+time).asString();

                    if(postResponse.getCode() > 204){
                        myDb.updateStatus(currentOrders.get(i), PackageModel.FAIL_SEND);
                    }
                    else{
                        myDb.updateStatus(currentOrders.get(i), PackageModel.COMPLETE);
                    }



                } catch (UnirestException e) {
                    e.printStackTrace();
                    myDb.updateStatus(currentOrders.get(i), PackageModel.FAIL_SEND);
                }
            }
            return "Complete";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            returnToPrev();
        }
    }
}
