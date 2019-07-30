package com.example.refresh;

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

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mAddresses = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> imageNames, ArrayList<String> addresses, ArrayList<Integer> images, ArrayList<String> details, Context context){
        mImageNames = imageNames;
        mImages = images;
        mAddresses = addresses;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @TargetApi(26)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        com.example.refresh.RecyclerView recyclerView = (com.example.refresh.RecyclerView) mContext;

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


        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.itemView.setBackgroundResource(R.drawable.rowclick);
                setViewInformation(viewHolder.id);
            }
        });
    }


    public void setViewInformation(String id){
        OrderDetails orderDetails = new OrderDetails(mContext, id);
        orderDetails.formatOrderDetails();
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

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
