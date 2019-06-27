package com.example.refresh;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button openCamera = findViewById(R.id.camera_btn);
        Button openExternal = findViewById(R.id.external_btn);
        Button viewOrders = findViewById(R.id.button4);
        final Button restCall = findViewById(R.id.button5);
        Button add = findViewById(R.id.addOrder);
        Button transfer = findViewById(R.id.transfer_orders);
        Button close = findViewById(R.id.close);
        Button reopen = findViewById(R.id.reopen);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(Scanner.class);
            }
        });

        openExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openActivity(External_Scanner.class);
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(RecyclerView.class);
            }
        });

        restCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(RestCalls.class);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AddOrders.class);
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(TransferOrders.class);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(CloseOrders.class);
            }
        });

        reopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(ReopenOrders.class);
            }
        });

    }

    public void openActivity(Class nextView){
        Intent intent = new Intent(Menu.this, nextView);
        startActivity(intent);
    }

    //cannot go back to the download page
    @Override
    public void onBackPressed() {
    }
}
