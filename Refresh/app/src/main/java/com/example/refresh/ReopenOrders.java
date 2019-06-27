package com.example.refresh;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReopenOrders extends AppCompatActivity {

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reopen_orders);
        myDb = new DatabaseHelper(this);

        Cursor cursor = myDb.get_closed();
        String str="";
        while(cursor.moveToNext()){
            str+=cursor.getString(0) + "\n";
        }
        TextView textView = findViewById(R.id.text_view_reopen);
        textView.setText(str);
    }
}
