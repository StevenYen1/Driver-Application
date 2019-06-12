package com.example.refresh;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button address_button;
    private TextInputEditText textInputUsername;
    private TextInputEditText textInputPass;
    private String pass = "";
    private String user = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputPass = findViewById(R.id.password);
        textInputUsername = findViewById(R.id.username);

        address_button = findViewById(R.id.mapsButton);
        address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openDownload();
            }
        });

    }

    public void openDownload(){
        Intent intent = new Intent(this, DownloadPage.class);
        user = textInputUsername.getText().toString();
        pass = textInputPass.getText().toString();
        intent.putExtra("username", user);
        intent.putExtra("pass", pass);
        startActivity(intent);
    }

}
