package com.example.refresh;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddOrders extends AppCompatActivity {

    EditText orderno;
    EditText address;
    EditText recipient;
    EditText item;
    Button submit;
    DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders);
        myDb = new DatabaseHelper(this);
        orderno = findViewById(R.id.add_orderno);
        address = findViewById(R.id.add_address);
        recipient = findViewById(R.id.add_recipient);
        item = findViewById(R.id.add_item);
        submit = findViewById(R.id.add_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(orderno) || isEmpty(address)|| isEmpty(recipient) || isEmpty(item)){
                    Toast.makeText(AddOrders.this, "PLEASE ENTER DATA FOR ALL ARGUMENTS", Toast.LENGTH_SHORT).show();
                }
                else{
                    String newNo = orderno.getText().toString();
                    String newAddress = address.getText().toString();
                    String newRecip = recipient.getText().toString();
                    String newItem = item.getText().toString();

                    Cursor cursor = myDb.getInstance(newNo);
                    if(cursor.getCount()==0) {
                        myDb.insertData(newNo, newAddress, newRecip, newItem, 0, "No Signature Yet", myDb.returnSize(DatabaseHelper.TABLE_NAME));
                        Toast.makeText(AddOrders.this, "A new order has been created.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddOrders.this, Menu.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(AddOrders.this, "Item with that order number already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public boolean isEmpty(EditText editText){
        return editText.getText().toString().equals("");
    }
}
