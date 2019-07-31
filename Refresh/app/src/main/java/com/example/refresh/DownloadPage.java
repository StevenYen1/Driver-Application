package com.example.refresh;
/*
Description:
    Setup page to download orders and confirm load.

Specific Functions:
    Download Orders
    Confirm Load
    Post Username

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.ItemModel.PackageModel;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

public class DownloadPage extends AppCompatActivity {

    /*
    private instance variables
     */
    private FancyButton download_btn;
    private int countProgress = 0;
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
        setupOrderList();
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
    Initializes a list of orders and stores it in the database
     */
    private void setupOrderList(){
        ArrayList<PackageModel> listData = createListData();
        populateDatabase(listData);
    }

    /*
    Static data created for testing purposes.
     */
    private ArrayList<PackageModel> createListData(){
        PackageModel item1 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "1600 Pennsylvania Ave, Washington, DC", "President of the US", "A Book", 0, 3, "7364322720");
        PackageModel item2 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "968 Woodside Circle, Glendale, FL 32433", "John Smith", "Hammer", 0, 1, "3258756119");
        PackageModel item3 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "3668 Young Road Boise, Idaho 83716", "Anthony Green", "Extention Cable", 0, 2, "3419677734");
        PackageModel item4 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "3295 Fantages Way, Bingham, ME 04920", "Trisha Patricks", "Shovel", 0 , 1, "0424509038");
        PackageModel item5 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "50 Vassar St, Cambridge, MA 02139", "Jamal Husain", "Acer E-Aspire", 0, 1, "4727767227");
        PackageModel item6 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "924 Avenue J East, Grand Prairie, TX 75050", "Charles Nguyen", "Vans", 0, 5, "4218026124");
        PackageModel item7 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "908 Massachusetts Ave, Arlington, MA 02476", "Scarlet Yin", "Summer Dress", 0, 1, "8393563789");
        PackageModel item8 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "65 Harrison Ave Ste 306, Boston, MA 02111", "Margaret Silva", "AA Batteries", 0, 10, "0247724439");
        PackageModel item9 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "124 Beach St, Ogunquit, ME 03907", "Takeya Shiguromo", "Samsung Galaxy 9", 0, 1, "6551938287");
        PackageModel item10 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "Walt Disney World Resort, Orlando, FL 32830", "Jessica Arsenault", "Tableware", 0, 1, "1632391305");
        PackageModel item11 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "500 Staples Drive, Framingham, MA 01702", "Sam Pickman", "Android Tablet", 0, 1, "4994061171");
        PackageModel item12 = new PackageModel(""+(long) Math.floor(Math.random() * 9_000_000_000L), "211 Arlington Street, Acton MA 01720", "Xiaoyu Chen", "Colored Pencil Set", 0, 1, "4361195587");

        ArrayList<PackageModel> newList = new ArrayList<PackageModel>();

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

        return newList;
    }

    /*
    Populates the database with a list of Delivery_Items
     */
    private void populateDatabase(ArrayList<PackageModel> list){
        DatabaseHelper myDb = new DatabaseHelper(this);
        myDb.clearTables();
        int i = 0;
        for(PackageModel x: list){
            myDb.insertOrder(x.getOrderNumber(), x.getAddress(), x.getRecipient(), x.getItem(), x.getStatus(), x.getSignature(), i, x.getQuantity(), x.getCartonNumber());
            i++;
        }
    }

    /*
    Instantiate Buttons and set their OnClickListeners
     */
    private void setupButtons(){
        download_btn = findViewById(R.id.download);
        download_btn.setOnClickListener(v -> startAsyncTask());


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
    Executes the actual post command.
     */
    private void startAsyncTask(){
        PostUsername postUsername = new PostUsername();
        postUsername.execute();
    }

    /*
    Runs post route to rest api.
    Stores username to user table.
     */
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

                runOnUiThread(() -> Toast.makeText(DownloadPage.this, "Connection made.", Toast.LENGTH_SHORT).show());
                return postResponse.getBody();

            } catch (UnirestException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(DownloadPage.this, "Please connect to the Staples Network.", Toast.LENGTH_SHORT).show());
            }
            return "Post Failure";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            if(!result.equals("Post Failure")){
                progress();
            }
        }
    }

    /*
    Mock progressbar to simulate process of downloading orders.
     */
    private void progress(){
        ProgressBar progressBar = findViewById(R.id.progressbar_download);
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run(){
                countProgress +=20;
                progressBar.setProgress(countProgress);

                if(countProgress == 100){
                    t.cancel();
                    runOnUiThread(() -> download_btn.setBackgroundColor(getResources().getColor(R.color.success)));
                    ordersDownloaded = true;
                }
            }
        };

        t.schedule(tt, 0, 100);
    }
}
