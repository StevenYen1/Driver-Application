package com.example.refresh.Authentication;
/*
Description:
    The Main Activity of the application (The first page the user sees.)
    Acts as a mandatory sign-in screen. Currently mock user authentication.

Specific Functions:
    Sign-In page for the user.
    Stores Username and Password data.
    Post Username

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.RetrieveOrders.DownloadPage;
import com.example.refresh.MainMenu.Menu;
import com.example.refresh.R;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /*
    private instance data
     */
    private ActionProcessButton actionProcessButton;
    private EditText username;
    private EditText password;

    public static String currentUser;

    /*
    Methods that are executed when this Activity is opened.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.createTables();
        setupActionButton();
        accountCreationSetup();
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
        actionProcessButton = findViewById(R.id.sign_in_btn);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(v -> {
            if(username.getText().toString().equals("")){
                Toast.makeText(MainActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            }
            else{
                String u = username.getText().toString();
                String p = password.getText().toString();
                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                if(databaseHelper.userExists(u) && databaseHelper.validatePassword(u, p)){
                    actionProcessButton.setProgress(1);
                    new Thread(() -> {
                        SystemClock.sleep(3000);
                        startAsyncTask();
                    }).start();
                }
                else{
                    Toast.makeText(MainActivity.this, "Incorrect Username or Password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    Text button that routes to create account page.
     */
    private void accountCreationSetup(){
        TextView newAccount = findViewById(R.id.createAccount);
        newAccount.setOnClickListener(v -> openNewAccount());
    }

    /*
    Moves to AccountCreation
     */
    private void openNewAccount(){
        Intent intent = new Intent(this, AccountCreation.class);
        startActivity(intent);
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
        currentUser = user;
        if(getIntent().getStringExtra("logout")!=null){
            Intent continueFromSession = new Intent(MainActivity.this, Menu.class);
            startActivity(continueFromSession);
        }
        else{
            startActivity(intent);
        }
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

            try {
                final HttpResponse<String> postResponse = Unirest.post("url endpoint for users")
                        .basicAuth("mockUsername", "mockPassword")
                        .field("username", username.getText().toString())
                        .field("field 2", "").asString();

                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connection made.", Toast.LENGTH_SHORT).show());
                return postResponse.getBody();

            } catch (UnirestException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please connect to the Staples Network.", Toast.LENGTH_SHORT).show());
            }
            return "Post Failure";
        }

        @TargetApi(Build.VERSION_CODES.O)
        protected void onPostExecute(String result) {
            if(!result.equals("Post Failure")){
                MainActivity.this.runOnUiThread(() -> {
                    actionProcessButton.setProgress(100);
                });
                signIn();
            }
        }
    }

}
