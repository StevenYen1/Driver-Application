package com.example.refresh.EditOrders;
/*
Description:
    This Activity's main function is to add a new order to the database.

Specific Features:
    Add a new order to the database.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.MainMenu.Menu;
import com.example.refresh.R;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.ORDER_TABLE;
import static java.lang.Integer.parseInt;

public class AddOrders extends AppCompatActivity {

    /*
    Methods that are called when the activity starts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders);
        initializeLayout();
    }

    /*
    Create EditTexts to get input information and submit button to create the new order.
     */
    private void initializeLayout(){
        EditText orderNumberInput = findViewById(R.id.add_orderno);
        EditText addressInput = findViewById(R.id.add_address);
        EditText recipientInput = findViewById(R.id.add_recipient);
        EditText itemInput = findViewById(R.id.add_item);
        EditText quantityInput = findViewById(R.id.add_quantity);
        EditText cartonNumberInput = findViewById(R.id.add_carton_num);

        FancyButton submit = findViewById(R.id.add_submit);
        submit.setOnClickListener(v -> {
            try {
                if(canCreate(orderNumberInput, addressInput, recipientInput, itemInput, quantityInput, cartonNumberInput)){
                    createOrder(orderNumberInput, addressInput, recipientInput, itemInput, quantityInput, cartonNumberInput);
                    openMenu();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /*
    Checks if a string is an integer.
    Returns true if it is, returns false otherwise.
     */
    public static boolean isParsable(String input) throws Exception{
        try{
            parseInt(input);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    /*
    Checks if an input is empty string. If so, returns true. Otherwise returns false.
     */
    private boolean isEmpty(EditText editText){
        return editText.getText().toString().equals("");
    }

    /*
    An order can be created if:
        - No argument is empty.
        - quantity value is a positive integer
    Returns true if the order can be created. False otherwise.
     */
    private boolean canCreate(EditText orderNumber, EditText address, EditText recipient,
                              EditText item, EditText quantity, EditText cartonNumber) throws Exception {
        if(isEmpty(orderNumber) || isEmpty(address) || isEmpty(recipient) ||
                isEmpty(item) || isEmpty(quantity) || isEmpty(cartonNumber)){
            Toast.makeText(this, "Enter a value for each argument.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!isValidQuantity(quantity)){
            Toast.makeText(this, "Quantity must be a positive integer.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    /*
    Creates order if orderNumber is a unique string.
    Inserts order information into an entry and saves it into the database.
     */
    private void createOrder(EditText orderNumber, EditText address, EditText recipient,
                             EditText item, EditText quantity, EditText cartonNumber){
        String newNo = orderNumber.getText().toString();
        String newAddress = address.getText().toString();
        String newRecip = recipient.getText().toString();
        String newItem = item.getText().toString();
        String newQuantity = quantity.getText().toString();
        String newCarton = cartonNumber.getText().toString();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if(isUniqueOrder(newNo)){
            databaseHelper.insertOrder(newNo, newAddress, newRecip, newItem, 0, "No SignaturePOST Yet",
                    databaseHelper.returnSize(ORDER_TABLE), parseInt(newQuantity), newCarton, "1000", "mockForNow");
        }
        else{
            Toast.makeText(this, "OrderNumber already exists.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Because OrderNumber must be unique due to how its currently written in the database,
    this method simply checks if we already have an order with the same OrderNumber.
    If we don't, the order is unique and we return true. If we do, the order is not unique and so we return false.
     */
    private boolean isUniqueOrder(String orderId){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor queryResult = databaseHelper.queryOrder(orderId);
        return queryResult.getCount()==0;
    }

    /*
    Checks if the quantity is a positive integer.
     */
    private boolean isValidQuantity(EditText quantity) throws Exception {
        String quantityStr = quantity.getText().toString();
        return (isParsable(quantityStr) && parseInt(quantityStr) > 0);
    }

    /*
    Opens the Menu activity.
     */
    private void openMenu(){
        Intent intent = new Intent(AddOrders.this, Menu.class);
        startActivity(intent);
    }
}
