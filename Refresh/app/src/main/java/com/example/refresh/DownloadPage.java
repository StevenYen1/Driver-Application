package com.example.refresh;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class DownloadPage extends AppCompatActivity {
    private Button deliveries;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_page);
        layout = findViewById(R.id.layout);
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

    public void openDeliveries(){
        Intent intent = new Intent(this, Address.class);
        startActivity(intent);
    }

}
