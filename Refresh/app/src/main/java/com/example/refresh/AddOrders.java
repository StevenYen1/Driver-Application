package com.example.refresh;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddOrders extends AppCompatActivity {

    private EditText orderno;
    private EditText address;
    private EditText recipient;
    private EditText item;
    private EditText quantity;
    private EditText cartonNum;
    private FancyButton submit;
    private DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders);

        myDb = new DatabaseHelper(this);
        orderno = findViewById(R.id.add_orderno);
        address = findViewById(R.id.add_address);
        recipient = findViewById(R.id.add_recipient);
        item = findViewById(R.id.add_item);
        quantity = findViewById(R.id.add_quantity);
        cartonNum = findViewById(R.id.add_carton_num);

        submit = findViewById(R.id.add_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(orderno) || isEmpty(address)|| isEmpty(recipient) || isEmpty(item) || isEmpty(quantity) || isEmpty(cartonNum)){
                    Toast.makeText(AddOrders.this, "PLEASE ENTER DATA FOR ALL ARGUMENTS", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        if(!isParsable(quantity.getText().toString()) || Integer.parseInt(quantity.getText().toString()) < 1) {
                            Toast.makeText(AddOrders.this, "QUANTITY MUST BE A VALID VALUE", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String newNo = orderno.getText().toString();
                            String newAddress = address.getText().toString();
                            String newRecip = recipient.getText().toString();
                            String newItem = item.getText().toString();
                            String newQuantity = quantity.getText().toString();
                            String newCarton = cartonNum.getText().toString();

                            Cursor cursor = myDb.queryOrder(newNo);
                            if(cursor.getCount()==0) {
                                myDb.insertOrder(newNo, newAddress, newRecip, newItem, 0, "No Signature Yet", myDb.returnSize(DatabaseHelper.ORDER_TABLE), Integer.parseInt(newQuantity), newCarton);
                                Toast.makeText(AddOrders.this, "A new order has been created.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddOrders.this, Menu.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(AddOrders.this, "Item with that order number already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static boolean isParsable(String input) throws Exception{
        try{
            Integer.parseInt(input);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    public boolean isEmpty(EditText editText){
        return editText.getText().toString().equals("");
    }
}
