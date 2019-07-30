package com.example.refresh;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.refresh.Delivery_Item.COMPLETE;
import static com.example.refresh.Delivery_Item.INCOMPLETE;
import static com.example.refresh.Delivery_Item.SCANNED;
import static com.example.refresh.Delivery_Item.SELECTED;

public class RecyclerView extends AppCompatActivity {

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
        Cursor rawOrders = myDb.queryAllOrders();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String ordernumber = rawOrders.getString(0);
            String address = rawOrders.getString(1);
            int status = rawOrders.getInt(4);

            allOrders.add(ordernumber);
            addresses.add(address);

            if(status == INCOMPLETE || status == SELECTED || status == SCANNED){
                remainingOrders.add(ordernumber);
                status_icons.add(INCOMPLETE);
            }
            else{
                completedOrders.add(ordernumber);
                status_icons.add(COMPLETE);
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

    private void initRecyclerView(){
        android.support.v7.widget.RecyclerView recyclerView = findViewById(R.id.recyclerv_view);

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(allOrders, addresses, status_icons, more_details, this);
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

                        myDb.moveOrder(allOrders.get(position_dragged), position_dragged, position_target);
                        status_icons.add(position_target, status_icons.remove(position_dragged));
                        addresses.add(position_target, addresses.remove(position_dragged));
                        allOrders.add(position_target, allOrders.remove(position_dragged));
                        adapter.notifyItemMoved(position_dragged,position_target);

                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int i) {

                        int position = viewHolder.getAdapterPosition();
                        myDb.moveOrder(allOrders.get(position), position, allOrders.size()-1);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemInserted(allOrders.size()-1);

                        status_icons.add(status_icons.remove(position));
                        addresses.add(addresses.remove(position));
                        allOrders.add(allOrders.remove(position));
                    }
                });
        helper.attachToRecyclerView(recyclerView);

    }
}
