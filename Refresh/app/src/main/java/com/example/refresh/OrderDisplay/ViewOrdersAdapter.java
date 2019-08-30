package com.example.refresh.OrderDisplay;
/*
Description:
    The purpose of this class is to create a ViewHolder item,
    which acts as a single entry in the RecyclerView.
    This class also places order information inside of each ViewHolder item.

Specific Features:
    Storing information inside each ViewHolder object.
    Setting the OnClickListener for each item.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.refresh.AlertDialogs.OrderDetails;
import com.example.refresh.R;

import java.util.ArrayList;

public class ViewOrdersAdapter extends RecyclerView.Adapter<ViewOrdersAdapter.ViewHolder>{

    /*
    private instance variable
     */
    private ArrayList<String> mImageNames;
    private ArrayList<String> mAddresses;
    private ArrayList<Integer> mImages;
    private Context mContext;

    /*
    constructor for adapter
     */
    public ViewOrdersAdapter(ArrayList<String> imageNames, ArrayList<String> addresses, ArrayList<Integer> images, ArrayList<String> details, Context context){
        mImageNames = imageNames;
        mImages = images;
        mAddresses = addresses;
        mContext = context;
    }

    /*
    Methods that occur when the ViewHolder is created
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /*
    Placing the order information from ArrayLists into the ViewHolder and sets OnClickListener
     */
    @TargetApi(26)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Drawable img;
        if(mImages.get(i)==0){
            img = mContext.getResources().getDrawable( R.drawable.ic_action_falsecheck );
        }
        else{
            img = mContext.getResources().getDrawable( R.drawable.ic_action_check );
        }
        img.setBounds( 0, 0, 60, 60 );

        viewHolder.id = mImageNames.get(i);
        viewHolder.address = mAddresses.get(i);
        viewHolder.display = "Order Number: " + viewHolder.id;
        viewHolder.imageName.setText(viewHolder.display);
        viewHolder.imageAddress.setText(viewHolder.address);
        viewHolder.statusIcon.setCompoundDrawables( img, null, null, null );


        viewHolder.parentLayout.setOnClickListener(v -> {
            viewHolder.itemView.setBackgroundResource(R.drawable.rowclick);
            viewOrderDetails(viewHolder.id);
        });
    }


    /*
    creates a OrderDetails dialog that displays order details.
     */
    private void viewOrderDetails(String id){
        OrderDetails orderDetails = new OrderDetails(mContext, id);
        orderDetails.formatOrderDetails();
    }

    /*
    returns how many items there are in the adapter.
     */
    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    /*
    Internal ViewHolder class that creates objects to hold order information.
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView statusIcon;
        TextView imageName;
        TextView imageAddress;
        RelativeLayout parentLayout;
        String display;
        String address;
        String id;

        public ViewHolder(View itemView){
            super(itemView);
            statusIcon = itemView.findViewById(R.id.icon_recycler);
            imageName = itemView.findViewById(R.id.recycle_orderNum);
            imageAddress = itemView.findViewById(R.id.recycle_address);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            display = "";
            address = "";
        }
    }
}
