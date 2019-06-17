package com.example.refresh;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DownloadPage extends AppCompatActivity {
    private Button deliveries;
    private ConstraintLayout layout;
    DatabaseHelper myDb;
    ArrayList<Delivery_Item> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_page);
        myDb = new DatabaseHelper(this);
        layout = findViewById(R.id.layout);
        staticList();
        for(Delivery_Item x: list){
            myDb.insertData(x.getOrderNumber(), x.getOrderString(), x.getRecipient(), x.getItem(), x.getStatus(), x.getSignature());
        }
        createWelcome();

        deliveries = findViewById(R.id.download);
        deliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openDeliveries();
            }
        });

    }

    @TargetApi(26)
    public void createWelcome(){

        ConstraintLayout.LayoutParams params = createMargins();

        TextView welcome = new TextView(this);
        welcome.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        String username = this.getIntent().getStringExtra("username");
        welcome.setText("Welcome, " + username);
        welcome.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        welcome.setGravity(Gravity.CENTER);
        welcome.setLayoutParams(params);
        layout.addView(welcome, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public ConstraintLayout.LayoutParams createMargins(){
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);

        return params;
    }

    public void staticList(){
        Delivery_Item item1 = new Delivery_Item("1", "1600 Pennsylvania Ave, Washington, DC", "President of the US", "How to Tweet 101");
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
        Intent intent = new Intent(this, Address.class);
        startActivity(intent);
    }


}
