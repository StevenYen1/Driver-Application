package com.example.refresh;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.drm.DrmStore;
import android.nfc.Tag;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.refresh.Delivery_Item.INCOMPLETE;

public class RecyclerView extends AppCompatActivity {

    private static final String TAG = "RecyclerView";
    private static final String INCOMPLETE_ICON = "https://cdn3.iconfinder.com/data/status_icons/flat-actions-status_icons-9/792/Close_Icon_Dark-512.png";
    private static final String COMPLETE_ICON ="https://cdn3.iconfinder.com/data/status_icons/flat-actions-status_icons-9/792/Tick_Mark_Dark-512.png";

    private ArrayList<String> display_details = new ArrayList<>();
    private ArrayList<String> status_icons = new ArrayList<>();
    private ArrayList<String> more_details = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> remainingOrders = new ArrayList<>();
    private ArrayList<String> completedOrders = new ArrayList<>();
    private ArrayList<String> allOrders = new ArrayList<>();
    DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        myDb = new DatabaseHelper(this);
        setOrderInformation();
        setToolbarActivities();
        initRecyclerView();



    }


    public void setOrderInformation(){
        Cursor rawOrders = myDb.getAllData();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String ordernumber = rawOrders.getString(0);
            String address = rawOrders.getString(1);
            int status = rawOrders.getInt(5);
            String details = formatDetails(rawOrders);

            more_details.add(details);
            allOrders.add(ordernumber);
            display_details.add("#"+ordernumber+'\n'+address);
            addresses.add(address);
            if(status == INCOMPLETE){
                remainingOrders.add(ordernumber);
                status_icons.add(INCOMPLETE_ICON);
            }
            else{
                completedOrders.add(ordernumber);
                status_icons.add(COMPLETE_ICON);
            }
        }
    }

    public void setToolbarActivities(){
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

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    @Override
    public void onBackPressed() {
    }

    public String formatDetails(Cursor cursor){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Order number: " + cursor.getString(0)+"\n");
        buffer.append("Address: " + cursor.getString(1)+"\n");
        buffer.append("Recipient: " + cursor.getString(2)+"\n");
        buffer.append("Item: " + cursor.getString(3)+"\n");
        buffer.append("Status: " + cursor.getString(4)+"\n");
        buffer.append("Sign: " + cursor.getString(5)+"\n");
        buffer.append("\n");
        return buffer.toString();
    }


    public void openMap(String id){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("orderString", id);
        startActivity(intent);
    }

    private void initRecyclerView(){
        android.support.v7.widget.RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(display_details, status_icons, more_details, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull android.support.v7.widget.RecyclerView recyclerView,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder dragged,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder target) {

                        int position_dragged = dragged.getAdapterPosition();
                        int position_target = target.getAdapterPosition();

                        myDb.updateIndex(allOrders.get(position_dragged), position_dragged, position_target);
<<<<<<< Updated upstream
                        mImageUrls.add(position_target, mImageUrls.remove(position_dragged));
                        mNames.add(position_target, mNames.remove(position_dragged));
                        allOrders.add(position_target, allOrders.remove(position_dragged));


=======
                        status_icons.add(position_target, status_icons.remove(position_dragged));
                        display_details.add(position_target, display_details.remove(position_dragged));
                        allOrders.add(position_target, allOrders.remove(position_dragged));
>>>>>>> Stashed changes
                        adapter.notifyItemMoved(position_dragged,position_target);

                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int i) {
                        int position = viewHolder.getAdapterPosition();
                        Log.d(TAG, "getAdapterPosition: " + position);
                        Log.d(TAG, "Item moved: " + allOrders.get(position));
                        Log.d(TAG, "Before: ---------------------------------------------------------");
                        Cursor x = myDb.getAllData();
                        while(x.moveToNext()){
                            Log.d(TAG, "order: #"+x.getString(0)+" | index: "+x.getInt(6));
                        }
                        Log.d(TAG, "\n");
                        ArrayList<String> deletedItem = myDb.removeIndex(allOrders.get(position), position);
                        adapter.notifyItemRemoved(position);
                        myDb.insertData(deletedItem.get(0),deletedItem.get(1),deletedItem.get(2),deletedItem.get(3),deletedItem.get(4),deletedItem.get(5), allOrders.size()-1);
                        adapter.notifyItemInserted(allOrders.size()-1);

                        mImageUrls.add(mImageUrls.remove(position));
                        mNames.add(mNames.remove(position));
                        allOrders.add(allOrders.remove(position));
                        mDetails.add(mDetails.remove(position));
                        mAddress.add(mAddress.remove(position));

                        Cursor y = myDb.getAllData();
                        Log.d(TAG, "After: ---------------------------------------------------------");
                        while(y.moveToNext()){
                            Log.d(TAG, "order: #"+y.getString(0)+" | index: "+y.getInt(6));
                        }
                        Log.d(TAG, "\n");
                        Log.d(TAG, "num image urls: "+mImageUrls.size());
                        Log.d(TAG, "num names: "+mNames.size());
                        Log.d(TAG, "num orders: "+allOrders.size());
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
