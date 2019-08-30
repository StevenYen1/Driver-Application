package com.example.refresh.RetrieveSignatures;
/*
Description:
    Given an orderNumber this class sends a GET request to the Signature REST api.
    Displays the returned information.

Specific Features:
    GET request to REST api.
    Recreate Bitmap image from Base64 String.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.refresh.AlertDialogs.StandardMessage;
import com.example.refresh.R;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

public class SignatureGET extends AppCompatActivity {

    /*
    private instance variables
     */
    private TextView textViewResult;
    private String orderNumber;

    /*
    Method that occurs when the activity starts up.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);
        orderNumber = getIntent().getStringExtra("id");
        startAsyncTask();

    }

    /*
    Starts the async task that gets the signature from the REST api.
     */
    public void startAsyncTask(){
        SignatureQuery task = new SignatureQuery();
        task.execute();
    }

    /*
    Internal class that creates a GET request to the signature REST service,
    getting all SignaturePOST related information that corresponds to the orderNumber.
     */
    private class SignatureQuery extends AsyncTask<Integer, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... integers) {

            try {
                final HttpResponse<JsonNode> getResponse = Unirest.get("url of endpoint")
                        .basicAuth("mockUsername", "mockPassword")
                        .field("ordernumber", orderNumber)
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
            if (result == null) {
                StandardMessage standardMessage = new StandardMessage(SignatureGET.this);
                standardMessage.buildStandardMessage("Error: Missing Entry",
                        "There is no such id in the database. Please try again")
                        .setPositiveButton("RETURN", (dialog, which) -> {
                                    Intent intent = new Intent(SignatureGET.this, SignatureInterface.class);
                                    startActivity(intent);
                                })
                        .setCancelable(false)
                        .show();
            }
            else{
                /*
                Here, there were multiple fields parsed from the json object obtained from the REST api.
                However, they have been replaced with two generic fields to avoid legal action.
                 */
                String field1 = null;
                String field2 = null;
                String signature = null;
                try {
                    field1 = result.get("field1").toString();
                    field2 = result.get("field2").toString();
                    signature = result.get("signature").toString();

                    String displayString =
                            "field1: " + field1
                            + "\nfield2: " + field2;

                    textViewResult.setText(displayString);
                    recreateSignature(signature);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Converts a base64 String into a Bitmap image.
     */
    private void recreateSignature(String base64str){
        byte[] bmp_bytes = Base64.decode(base64str, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(bmp_bytes, 0, bmp_bytes.length);
        Drawable img = new BitmapDrawable(getResources(), bmp);

        ImageView image = findViewById(R.id.imageview2);
        image.setImageDrawable(img);
    }
}
