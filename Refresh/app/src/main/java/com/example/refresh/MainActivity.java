package com.example.refresh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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

import com.dd.processbutton.iml.ActionProcessButton;

public class MainActivity extends AppCompatActivity {
    private Button address_button;
    private ActionProcessButton actionProcessButton;
    private TextInputEditText textInputUsername;
    private Handler mHandler = new Handler();
    private TextInputEditText textInputPass;
    private String pass = "";
    private String user = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputPass = findViewById(R.id.password);
        textInputUsername = findViewById(R.id.username);

        actionProcessButton = findViewById(R.id.mapsButton);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                actionProcessButton.setProgress(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actionProcessButton.setProgress(100);
                                mHandler.postDelayed(mUpdateTimeTask, 1000);
                            }
                        });
                    }
                }).start();
            }
        });

    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            openDownload();
        }
    };

    public void openDownload(){
        Intent intent = new Intent(this, DownloadPage.class);
        user = textInputUsername.getText().toString();
        pass = textInputPass.getText().toString();
        intent.putExtra("username", user);
        intent.putExtra("pass", pass);
        startActivity(intent);
    }

}
