package com.example.refresh;
/*
Description:
    The Main Activity of the application (The first page the user sees.)
    Acts as a mandatory sign-in screen. Currently mock user authentication.

Specific Functions:
    Sign-In page for the user.
    Stores Username and Password data.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

public class MainActivity extends AppCompatActivity {

    /*
    private instance data
     */
    private EditText username;
    private EditText password;
    private Runnable mUpdateTimeTask = () -> signIn();

    /*
    Methods that are executed when this Activity is opened.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout();
        setupActionButton();
    }

    /*
    Setup of the main view (UI)
     */
    private void setupLayout(){
        setContentView(R.layout.activity_main);
        ImageView logo = findViewById(R.id.logo_main);
        logo.setColorFilter(Color.WHITE);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
    }

    /*
    Creates instance of ActionButton and sets OnClickListener.
    Button checks if Username and Password are acceptable, and opens Download Page if they are.
     */
    private void setupActionButton(){
        Handler mHandler = new Handler();
        ActionProcessButton actionProcessButton = findViewById(R.id.sign_in_btn);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(v -> {
            if(username.getText().toString().equals("")){
                Toast.makeText(MainActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            }
            else{
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

    /*
    Stores username and password and opens:
        Download page for first use of the day.
        Menu page for returning use.
     */
    public void signIn(){
        Intent intent = new Intent(this, DownloadPage.class);
        String user = username.getText().toString();
        String pass = password.getText().toString();
        intent.putExtra("username", user);
        intent.putExtra("pass", pass);
        if(getIntent().getStringExtra("logout")!=null){
            Intent continueFromSession = new Intent(MainActivity.this, Menu.class);
            startActivity(continueFromSession);
        }
        else{
            startActivity(intent);
        }
    }

}
