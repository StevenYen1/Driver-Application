package com.example.refresh;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button signature_button;
    private Button scanner_button;
    private Button address_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signature_button = (Button) findViewById(R.id.button);
        signature_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openFeature1();
            }
        });

        scanner_button = (Button) findViewById(R.id.scanButton);
        scanner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View w) {
                openScanner();
            }
        });

        address_button = (Button) findViewById(R.id.mapsButton);
        address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openMaps();
            }
        });

    }

    public void openMaps(){
        Intent intent = new Intent(this, Address.class);
        startActivity(intent);
    }

    public void openFeature1(){
        Intent intent = new Intent(this, Feature1.class);
        startActivity(intent);
    }

    public void openScanner(){
        Intent intent = new Intent(this, Scanner.class);
        startActivity(intent);
    }

}
