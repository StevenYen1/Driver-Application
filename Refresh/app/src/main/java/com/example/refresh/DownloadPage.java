package com.example.refresh;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.processbutton.FlatButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

public class DownloadPage extends AppCompatActivity {
    private FancyButton download_btn;
    private FancyButton confirm;
    private FancyButton finish;
    private TextView title;
    DatabaseHelper myDb;
    ProgressBar progressBar;
    int counter = 0;
    ArrayList<Delivery_Item> list;

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
        title.setText("Welcome, " + getIntent().getStringExtra("username"));

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
                        }
                    });
                }
            }
        };

        t.schedule(tt, 0, 100);
    }

    public void staticList(){
        Delivery_Item item1 = new Delivery_Item("1", "1600 Pennsylvania Ave, Washington, DC", "President of the US", "How to Tweet 101", Delivery_Item.FAIL_SEND);
        Delivery_Item item2 = new Delivery_Item("2", "Seoul, South Korea");
        Delivery_Item item3 = new Delivery_Item("3", "Champ de Mars, Paris");
        Delivery_Item item4 = new Delivery_Item("4", "Los Angeles, California");
        Delivery_Item item5 = new Delivery_Item("5", "50 Vassar St, Cambridge, MA");
        Delivery_Item item6 = new Delivery_Item("6", "924 Avenue J East, Grand Prairie, TX");
        Delivery_Item item7 = new Delivery_Item("7", "908 Massachusetts Ave, Arlington, MA");
        Delivery_Item item8 = new Delivery_Item("8", "65 Harrison Ave Ste 306, Boston, MA");
        Delivery_Item item9 = new Delivery_Item("9", "124 Beach St, Ogunquit, ME");
        Delivery_Item item10 = new Delivery_Item("10", "〒150-8010東京都渋谷区", "Tetsuya Nomura", "Final Fantasy 7 Remake");
        Delivery_Item item11 = new Delivery_Item("11", "500 Staples Drive, Framingham, MA", "Saar Picker", "A Hardworking Intern");
        Delivery_Item item12 = new Delivery_Item("12", "211 Arlington Street, Acton MA");
        Delivery_Item item13 = new Delivery_Item("1112", "address", "recipient", "item", Delivery_Item.SCANNED);
        Delivery_Item item14 = new Delivery_Item("34422", "address", "recipient", "item", Delivery_Item.FAIL_SEND);

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
        newList.add(item13);
        newList.add(item14);

        list = newList;
    }




    public void openDeliveries(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }


}
