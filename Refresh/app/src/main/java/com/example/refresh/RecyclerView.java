package com.example.refresh;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class RecyclerView extends AppCompatActivity {

    private static final String TAG = "RecyclerView";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mDetails = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<String> remainingOrders = new ArrayList<>();
    private ArrayList<String> completedOrders = new ArrayList<>();
    private ArrayList<String> allOrders = new ArrayList<>();
    DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        myDb = new DatabaseHelper(this);
        if(getIntent().getStringArrayListExtra("remainingOrders")!=null){
            remainingOrders = getIntent().getStringArrayListExtra("remainingOrders");
        }
        if(getIntent().getStringArrayListExtra("completedOrders")!=null){
            completedOrders = getIntent().getStringArrayListExtra("completedOrders");
        }
        allOrders.addAll(remainingOrders);
        allOrders.addAll(completedOrders);

        Resources res = this.getResources();
        TextView title = findViewById(R.id.table_title);
        title.setText(String.format(res.getString(R.string.DeliveriesDate), returnDate()));

        Button button = findViewById(R.id.scan1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });

        Log.d(TAG,"onCreate: started");
        initImageBitmaps();



    }

    public String returnDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public void openScanner(){
        Intent intent = new Intent(this, Scanner.class);
        intent.putExtra("remainingOrders", remainingOrders);
        intent.putExtra("completedOrders", completedOrders);
        startActivity(intent);
    }

    public ArrayList<String> getmAddress() {
        return mAddress;
    }

    @Override
    public void onBackPressed() {
    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");

        for(String x: allOrders){
            Cursor cursor = myDb.getInstance(x);
            if(cursor.getCount() == 0){
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()) {
                if(remainingOrders.contains(x)) {
                    mImageUrls.add("https://cdn3.iconfinder.com/data/icons/flat-actions-icons-9/792/Close_Icon_Dark-512.png");
                }
                else{
                    mImageUrls.add("https://cdn3.iconfinder.com/data/icons/flat-actions-icons-9/792/Tick_Mark_Dark-512.png");
                }
                mNames.add("#"+cursor.getString(0)+'\n'+cursor.getString(1));
                buffer.append("Order number: " + cursor.getString(0)+"\n");
                buffer.append("Address: " + cursor.getString(1)+"\n");
                buffer.append("Recipient: " + cursor.getString(2)+"\n");
                buffer.append("Item: " + cursor.getString(3)+"\n");
                buffer.append("Status: " + cursor.getString(4)+"\n");
                buffer.append("Sign: " + cursor.getString(5)+"\n");
                buffer.append("\n");
                mDetails.add(buffer.toString());
                mAddress.add(cursor.getString(1));
            }
        }

        initRecyclerView();

    }

    public void openMap(String id){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("orderString", id);
        startActivity(intent);
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView");
        android.support.v7.widget.RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mImageUrls, mDetails, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                    @Override
                    public boolean onMove(@NonNull android.support.v7.widget.RecyclerView recyclerView,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder dragged,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder target) {

                        int position_dragged = dragged.getAdapterPosition();
                        int position_target = target.getAdapterPosition();

                        Collections.swap(mImageUrls, position_dragged, position_target);
                        Collections.swap(mNames, position_dragged, position_target);

                        adapter.notifyItemMoved(position_dragged,position_target);


                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int i) {

                    }
                });
        helper.attachToRecyclerView(recyclerView);

    }

    //version issue
    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error has occurred but can be resolved
            Log.d(TAG, "isServicesOK: An Error has occurred but is resolvable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, 9001);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
