package com.example.refresh.OrderDisplay;
/*
Description:
    The purpose of this activity is to display order information in a list,
    as well as allow the user to manipulate the list.

Specific features:
    Create RecyclerView which allows the user to:
        - View orders
        - Reposition orders

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_STATUS;
import static com.example.refresh.Model.PackageModel.COMPLETE;
import static com.example.refresh.Model.PackageModel.INCOMPLETE;
import static com.example.refresh.Model.PackageModel.SCANNED;
import static com.example.refresh.Model.PackageModel.SELECTED;

public class ViewOrders extends AppCompatActivity {

    /*
    private instance variables
     */
    private ArrayList<Integer> status_icons = new ArrayList<>();
    private ArrayList<String> more_details = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> allOrders = new ArrayList<>();

    /*
    Methods that are called when the activity begins
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        setOrderInformation();
        setTitle();
        initRecyclerView();
    }

    /*
    Query the database for the updated status of orders. Stores the information in private ArrayLists.
     */
    public void setOrderInformation(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor rawOrders = databaseHelper.queryAllOrders();
        if(rawOrders.getCount() == 0){
            return;
        }
        while(rawOrders.moveToNext()){
            String orderNumber = rawOrders.getString(COL_ORDERNUMBER);
            String address = rawOrders.getString(COL_ADDRESS);
            int status = rawOrders.getInt(COL_STATUS);

            allOrders.add(orderNumber);
            addresses.add(address);

            if(status == INCOMPLETE || status == SELECTED || status == SCANNED){
                status_icons.add(INCOMPLETE);
            }
            else{
                status_icons.add(COMPLETE);
            }
        }
    }

    /*
    Sets the title
     */
    public void setTitle(){
        Resources res = this.getResources();
        TextView title = findViewById(R.id.table_title);
        title.setText(String.format(res.getString(R.string.DeliveriesDate), returnDate()));
    }

    /*
    Gets the current date and returns it as a String
     */
    public String returnDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    /*
    Creates the RecyclerView, which is the scrollable, swappable, swipable list of orders.
     */
    private void initRecyclerView(){
        android.support.v7.widget.RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final ViewOrdersAdapter adapter = new ViewOrdersAdapter(allOrders, addresses, status_icons, more_details, this);
        DividerItemDecoration itemDecor = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseHelper databaseHelper = new DatabaseHelper(ViewOrders.this);
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull android.support.v7.widget.RecyclerView recyclerView,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder dragged,
                                          @NonNull android.support.v7.widget.RecyclerView.ViewHolder target) {

                        int position_dragged = dragged.getAdapterPosition();
                        int position_target = target.getAdapterPosition();

                        databaseHelper.moveOrder(allOrders.get(position_dragged), position_dragged, position_target);
                        status_icons.add(position_target, status_icons.remove(position_dragged));
                        addresses.add(position_target, addresses.remove(position_dragged));
                        allOrders.add(position_target, allOrders.remove(position_dragged));
                        adapter.notifyItemMoved(position_dragged,position_target);

                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int i) {

                        int position = viewHolder.getAdapterPosition();
                        databaseHelper.moveOrder(allOrders.get(position), position, allOrders.size()-1);
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
