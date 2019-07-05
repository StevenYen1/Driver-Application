package com.example.refresh;

import android.annotation.SuppressLint;
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
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.ads.internal.gmsg.HttpClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedReader;
import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class RestCalls extends AppCompatActivity {

    TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);
        TextView async = new TextView(this);

//        Button getRequest = new Button(this);
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
                final HttpResponse<JsonNode> getResponse = Unirest.get("http://10.0.2.2:8080/signaturesvc/v1/signature")
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
                AlertDialog.Builder builder = new AlertDialog.Builder(RestCalls.this);
                builder.setTitle("Error: Missing Entry");
                builder.setMessage("There is no such id in the database. Please try again");
                builder.setPositiveButton("RETURN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RestCalls.this, RestList.class);
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
                    textViewResult.setText("orderId: "+orderId
                            + "\nsubmissionDate: " + submissionDate
                            + "\nstatus: " + status);
                    recreateSignature(signature);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void recreateSignature(String base64str){
        Log.d("TAG", "onPostExecute: " + base64str);
        byte[] bmp_bytes = Base64.decode(base64str, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(bmp_bytes, 0, bmp_bytes.length);
        Drawable img = new BitmapDrawable(getResources(), bmp);

        ImageView image = (ImageView) findViewById(R.id.imageview2);
        image.setImageDrawable(img);
    }
}
