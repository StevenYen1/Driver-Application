package com.example.refresh.RetrieveSignatures;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.refresh.R;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import static android.support.constraint.Constraints.TAG;

public class SignatureCall extends AppCompatActivity {

    private TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);
        TextView async = new TextView(this);

        startAsycTask(async);

    }

    public void startAsycTask(View v){
        GetConnection task = new GetConnection();
        task.execute();
    }

    private class GetConnection extends AsyncTask<Integer, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... integers) {

            try {
                final HttpResponse<JsonNode> getResponse = Unirest.get("http://10.244.185.101:80/signaturesvc/v1/signature")
                        .basicAuth("epts_app", "uB25J=UUwU")
                        .field("orderID", getIntent().getStringExtra("id"))
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SignatureCall.this);
                builder.setTitle("Error: Missing Entry");
                builder.setMessage("There is no such id in the database. Please try again");
                builder.setCancelable(false);
                builder.setPositiveButton("RETURN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignatureCall.this, SignatureList.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
            else{
                Log.d(TAG, "onPostExecute: " + result.toString());
                String orderId = null;
                String signature = null;
                String submissionDate = null;
                String status = null;
                try {
                    orderId = result.get("shipmentId").toString();
                    signature = result.get("signature").toString();
                    submissionDate = result.get("submissionDate").toString();
                    status = result.get("status").toString();

                    String displayString =
                            "orderId: " + orderId
                            + "\nsubmissionDate: " + submissionDate
                            + "\nstatus: " + status;

                    textViewResult.setText(displayString);
                    recreateSignature(signature);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void recreateSignature(String base64str){
        byte[] bmp_bytes = Base64.decode(base64str, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(bmp_bytes, 0, bmp_bytes.length);
        Drawable img = new BitmapDrawable(getResources(), bmp);

        ImageView image = findViewById(R.id.imageview2);
        image.setImageDrawable(img);
    }
}
