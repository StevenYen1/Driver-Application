package com.example.refresh;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

public class DownloadPage extends AppCompatActivity {
    private FancyButton download_btn;
    private FancyButton confirm;
    private FancyButton finish;
    private TextView title;
    private DatabaseHelper myDb;
    private ProgressBar progressBar;
    private int counter = 0;
    private ArrayList<Delivery_Item> list;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_page);
        myDb = new DatabaseHelper(this);
        staticList();
        myDb.clear();
        int i = 0;
        for(Delivery_Item x: list){
            myDb.insertData(x.getOrderNumber(), x.getOrderString(), x.getRecipient(), x.getItem(), x.getStatus(), x.getSignature(), i, x.getQuantity(), x.getCartonNumber());
            i++;
        }

        download_btn = findViewById(R.id.download);
        confirm = findViewById(R.id.confirm_load);
        finish = findViewById(R.id.download_continue);
        title = findViewById(R.id.download_title);
        name = getIntent().getStringExtra("username");
        title.setText("Welcome, " + name);

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                progress();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setBackgroundColor(getResources().getColor(R.color.success));
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeliveries();
            }
        });



    }

    public void startAsyncTask(){
        PostUsername postUsername = new PostUsername();
        postUsername.execute();
    }

    public void progress(){
        progressBar = findViewById(R.id.progressbar_download);
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run(){
                counter+=4;
                progressBar.setProgress(counter);

                if(counter == 100){
                    t.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            download_btn.setBackgroundColor(getResources().getColor(R.color.success));
                            startAsyncTask();
                        }
                    });
                }
            }
        };

        t.schedule(tt, 0, 100);
    }

    public void staticList(){
        Delivery_Item item1 = new Delivery_Item("9082416310", "1600 Pennsylvania Ave, Washington, DC", "President of the US", "A Book", 0, 3, "7364322720");
        Delivery_Item item2 = new Delivery_Item("6315422520", "968 Woodside Circle, Glendale, FL 32433", "John Smith", "Hammer", 0, 1, "3258756119");
        Delivery_Item item3 = new Delivery_Item("8971204330", "3668 Young Road Boise, Idaho 83716", "Anthony Green", "Extention Cable", 0, 2, "3419677734");
        Delivery_Item item4 = new Delivery_Item("5985212780", "3295 Fantages Way, Bingham, ME 04920", "Trisha Patricks", "Shovel", 0 , 1, "0424509038");
        Delivery_Item item5 = new Delivery_Item("1989364740", "50 Vassar St, Cambridge, MA 02139", "Jamal Husain", "Acer E-Aspire", 0, 1, "4727767227");
        Delivery_Item item6 = new Delivery_Item("8217401950", "924 Avenue J East, Grand Prairie, TX 75050", "Charles Nguyen", "Vans", 0, 5, "4218026124");
        Delivery_Item item7 = new Delivery_Item("4090050200", "908 Massachusetts Ave, Arlington, MA 02476", "Scarlet Yin", "Summer Dress", 0, 1, "8393563789");
        Delivery_Item item8 = new Delivery_Item("5937198550", "65 Harrison Ave Ste 306, Boston, MA 02111", "Margaret Silva", "AA Batteries", 0, 10, "0247724439");
        Delivery_Item item9 = new Delivery_Item("7436101569", "124 Beach St, Ogunquit, ME 03907", "Takeya Shiguromo", "Samsung Galaxy 9", 0, 1, "6551938287");
        Delivery_Item item10 = new Delivery_Item("0398390120", "Walt Disney World Resort, Orlando, FL 32830", "Jessica Arsenault", "Tableware", 0, 1, "1632391305");
        Delivery_Item item11 = new Delivery_Item("1898172240", "500 Staples Drive, Framingham, MA 01702", "Sam Pickman", "Android Tablet", 0, 1, "4994061171");
        Delivery_Item item12 = new Delivery_Item("4010003210", "211 Arlington Street, Acton MA 01720", "Xiaoyu Chen", "Colored Pencil Set", 0, 1, "4361195587");

        ArrayList<Delivery_Item> newList = new ArrayList<Delivery_Item>();

        newList.add(item1);
        newList.add(item2);
        newList.add(item3);
        newList.add(item4);
        newList.add(item5);
        newList.add(item6);
        newList.add(item7);
        newList.add(item8);
        newList.add(item9);
        newList.add(item10);
        newList.add(item11);
        newList.add(item12);

        list = newList;
    }




    public void openDeliveries(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    private class PostUsername extends AsyncTask<Integer, Integer, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Integer... integers) {

            Date date = new Date();
            long time = date.getTime();
            try {
                final HttpResponse<String> postResponse = Unirest.post("http://10.244.185.101:80/signaturesvc/v1/user")
                        .basicAuth("epts_app", "uB25J=UUwU")
                        .field("userid", name)
                        .field("last_login", ""+time).asString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DownloadPage.this, "Connection made.", Toast.LENGTH_SHORT).show();
                    }
                });

                return postResponse.getBody();

            } catch (UnirestException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DownloadPage.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return "Complete";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            Log.d("TAG", "onPostExecute: " + result);
        }
    }


}
