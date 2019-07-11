package com.example.refresh;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mashape.unirest.http.Unirest;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RestList extends AppCompatActivity {

    DatabaseHelper myDb;
    String inputString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_list);
        myDb = new DatabaseHelper(this);
        Spinner dropdown = findViewById(R.id.rest_dropdown);

        Cursor cursor = myDb.getAllData();
        final ArrayList<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            String s = cursor.getString(0);
            list.add(s);
            if(inputString.equals("")){inputString=s;}
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inputString = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                inputString = "";
            }
        });

        Button btn = findViewById(R.id.rest_search_btn);
        //right now search bar trumps item select. Maybe make "No id" option for dropdown, then get input text if that is selected
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestList.this, RestCalls.class);
                intent.putExtra("id", inputString);
                startActivity(intent);
            }
        });
    }
}
