package com.example.refresh;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.ads.internal.gmsg.HttpClient;

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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class RestCalls extends AppCompatActivity {


    static TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);
        startAsycTask(textViewResult);
    }

    public void startAsycTask(View v){
        ExampleAsyncTask task = new ExampleAsyncTask();
        task.execute();
    }

    private class ExampleAsyncTask extends AsyncTask<Integer, Integer, String>{

        private static final String TAG = "TAG" ;

        @Override
        protected String doInBackground(Integer... integers) {
            URL url = null;
            try {
                //can probably make it order specific by doing 'string'+ordernumber
                url = new URL("http://10.0.2.2:8080/signaturesvc/v1/signature?orderID=1");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int status = con.getResponseCode();
                Log.d("TAG-------------------", "doInBackground: "+status);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                Log.d("TAG-------------------", "doInBackground: "+content.toString());
                in.close();
                con.disconnect();
                return content.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return"";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            byte[] bytes = Base64.decode(getIntent().getStringExtra("stra"), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Drawable img = new BitmapDrawable(getResources(), bmp);

            ImageView image = (ImageView) findViewById(R.id.imageview2);
            image.setImageDrawable(img);
        }
    }
}
