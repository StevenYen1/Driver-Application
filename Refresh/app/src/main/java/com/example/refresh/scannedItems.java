package com.example.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class scannedItems extends AppCompatActivity {

    ArrayList<String> orders;
    ArrayList<String> incompleteOrders;
    ArrayList<String> completedOrders;
    LinearLayout layout;
    ListView view;
    ArrayList<String> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_items);

        layout = findViewById(R.id.order_area);
        TextView title = new TextView(this);
        title.setText("Scanned Orders:");
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);

        view = findViewById(R.id.list);
        view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        view.addHeaderView(title);

        orders = getIntent().getStringArrayListExtra("scannedOrders");
        incompleteOrders = getIntent().getStringArrayListExtra("remainingOrders");
        completedOrders = getIntent().getStringArrayListExtra("completedOrders");
        if(getIntent().getStringArrayListExtra("selectedOrders")!=null){
            selectedItems = getIntent().getStringArrayListExtra("selectedOrders");
            for(String x: selectedItems){
                orders.remove(x);
            }
        }
        orders.addAll(selectedItems);

        Button sign = findViewById(R.id.goto_sign);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSign();
            }
        });
        if(orders.size()>0){
            createView();
        }
        else{
            noOrders();
        }
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go back to scanning?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                openScan();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void showSelectedItems(){
        String items="";
        for(String item: selectedItems){
            items+="-"+item+"\n";

        }
        Toast.makeText(this, "Your current list is :\n"+items,Toast.LENGTH_SHORT).show();
    }

    public void createView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.checklist, orders);
        view.setAdapter(adapter);
        for(int i = 0; i < selectedItems.size(); i++){
            view.setItemChecked(orders.size()-i, true);
        }
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem =((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                }
                else{
                    selectedItems.add(selectedItem);
                }
                showSelectedItems();

            }
        });
    }

    public void openSign(){
        Intent intent = new Intent(this, Feature1.class);
        if(selectedItems.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error: ");
            builder.setCancelable(true);
            builder.setMessage("Please select an order to sign");
            builder.show();
            return;
        }
        for(String x: selectedItems){
            orders.remove(x);
        }
        intent.putExtra("selectedOrders", selectedItems);
        intent.putExtra("completedOrders", completedOrders);
        intent.putExtra("scannedOrders", orders);
        intent.putExtra("remainingOrders", incompleteOrders);
        startActivity(intent);
    }

    public void openScan(){
        Intent intent = new Intent(this, Scanner.class);
        intent.putExtra("completedOrders", completedOrders);
        intent.putExtra("remainingOrders", incompleteOrders);
        intent.putExtra("scannedOrders", orders);
        intent.putExtra("selectedOrders", selectedItems);
        startActivity(intent);
    }

    public void gotoOrders(){
        Intent intent = new Intent(this, Address.class);
        intent.putExtra("completedOrders", completedOrders);
        intent.putExtra("remainingOrders", incompleteOrders);
        startActivity(intent);
    }

    public void noOrders(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("There are no orders available");
        builder.setCancelable(false);
        builder.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                openScan();
            }
        });
        builder.setNeutralButton("Orders", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                gotoOrders();
            }
        });
        builder.show();
    }

}
