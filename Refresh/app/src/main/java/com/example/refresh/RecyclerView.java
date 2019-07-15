package com.example.refresh;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;
import static java.lang.Integer.parseInt;

public class RecyclerView extends AppCompatActivity {

    private static final String TAG = "RecyclerView";
    private static final int INCOMPLETE_ICON = R.drawable.statusno;
    private static final int COMPLETE_ICON = R.drawable.complete;

    private ArrayList<String> simple_displays = new ArrayList<>();
    private ArrayList<Integer> status_icons = new ArrayList<>();
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
            int status = rawOrders.getInt(4);
            String details = formatDetails(rawOrders);

            more_details.add(details);
            allOrders.add(ordernumber);
            simple_displays.add("#"+ordernumber+'\n'+address);
            addresses.add(address);
            Log.d(TAG, "Status: "+ status);
            if(status == INCOMPLETE || status == SELECTED || status == SCANNED){
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
    }

    public String returnDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public String formatDetails(Cursor cursor){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Order number: \"" + cursor.getString(0)+"\"\n");
        buffer.append("\n");
        buffer.append("Address: \"" + cursor.getString(1)+"\"\n");
        buffer.append("\n");
        buffer.append("Recipient: \"" + cursor.getString(2)+"\"\n");
        buffer.append("\n");
        buffer.append("Item: \"" + cursor.getString(3)+"\"\n");
        buffer.append("\n");
        buffer.append("Quantity: \"" + cursor.getInt(7)+"\"\n");
        buffer.append("\n");
        buffer.append("Carton Number: \"" + cursor.getInt(8)+"\"\n");
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

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(simple_displays, status_icons, more_details, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutmanager);

        DividerItemDecoration itemDecor = new DividerItemDecoration(recyclerView.getContext(), layoutmanager.getOrientation());
        recyclerView.addItemDecoration(itemDecor);


        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull android.support.v7.widget.RecyclerView recyclerView,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder dragged,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder target) {

                        int position_dragged = dragged.getAdapterPosition();
                        int position_target = target.getAdapterPosition();

                        myDb.updateIndex(allOrders.get(position_dragged), position_dragged, position_target);
                        status_icons.add(position_target, status_icons.remove(position_dragged));
                        simple_displays.add(position_target, simple_displays.remove(position_dragged));
                        more_details.add(position_target, more_details.remove(position_dragged));
                        allOrders.add(position_target, allOrders.remove(position_dragged));
                        adapter.notifyItemMoved(position_dragged,position_target);

                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int i) {

                        int position = viewHolder.getAdapterPosition();
                        ArrayList<String> deletedItem = myDb.removeIndex(allOrders.get(position), position);
                        adapter.notifyItemRemoved(position);
                        myDb.insertData(deletedItem.get(0),deletedItem.get(1),deletedItem.get(2),deletedItem.get(3),parseInt(deletedItem.get(4)),deletedItem.get(5), allOrders.size()-1, parseInt(deletedItem.get(7)));

                        adapter.notifyItemInserted(allOrders.size()-1);

                        status_icons.add(status_icons.remove(position));
                        simple_displays.add(simple_displays.remove(position));
                        allOrders.add(allOrders.remove(position));
                        more_details.add(more_details.remove(position));
                        addresses.add(addresses.remove(position));
                    }
                });
        helper.attachToRecyclerView(recyclerView);

    }
}
