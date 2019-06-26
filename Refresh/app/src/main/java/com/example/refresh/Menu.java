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
        Button restCall = findViewById(R.id.button5);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Scanner.class);
                startActivity(intent);
            }
        });

        openExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, External_Scanner.class);
                startActivity(intent);
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, RecyclerView.class);
                startActivity(intent);
            }
        });

        restCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, RestCalls.class);
                startActivity(intent);
            }
        });
    }

    //cannot go back to the download page
    @Override
    public void onBackPressed() {
    }
}
