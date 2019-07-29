package com.example.refresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

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
        final com.example.refresh.RecyclerView recyclerView = (com.example.refresh.RecyclerView) mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View mView = inflater.inflate(R.layout.newdetails_layout, null);
        DatabaseHelper myDb = new DatabaseHelper(mContext);
        builder.setCancelable(true);

        Cursor cursor = myDb.queryOrder(id);
        while(cursor.moveToNext()){
            String ordernum = cursor.getString(0);
            String address = cursor.getString(1);
            String recipient = cursor.getString(2);
            String item = cursor.getString(3);
            int quantity = cursor.getInt(7);
            String cartonnum = cursor.getString(8);

            TextView ordernum_view = mView.findViewById(R.id.newdetails_ordernum);
            TextView cartonnum_view = mView.findViewById(R.id.newdetails_cartonnum);
            TextView address_view = mView.findViewById(R.id.newdetails_address);
            TextView recipient_view = mView.findViewById(R.id.newdetails_recipient);
            TextView item_view = mView.findViewById(R.id.newdetails_item);
            TextView quantity_view = mView.findViewById(R.id.newdetails_quantity);
            FancyButton mapBtn = mView.findViewById(R.id.newdetails_map);

            ordernum_view.setText("Order Number: " + ordernum);
            cartonnum_view.setText("Carton Number: " + cartonnum);
            address_view.setText(address);
            recipient_view.setText(recipient);
            item_view.setText(item);
            quantity_view.setText(""+quantity);
            mapBtn.setOnClickListener(v -> recyclerView.openMap(address));

            builder.setView(mView);
            AlertDialog dialog = builder.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

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
