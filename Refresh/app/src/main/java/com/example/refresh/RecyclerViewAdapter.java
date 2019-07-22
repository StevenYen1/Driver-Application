package com.example.refresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private ArrayList<String> mDetails = new ArrayList<>();
    private ArrayList<String> mAddresses = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> imageNames, ArrayList<String> addresses, ArrayList<Integer> images, ArrayList<String> details, Context context){
        mImageNames = imageNames;
        mAddresses = addresses;
        mImages = images;
        mContext = context;
        mDetails = details;
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
        final ArrayList<String> addressList = recyclerView.getAddresses();

        Drawable img;
        if(mImages.get(i)==0){
            img = mContext.getResources().getDrawable( R.drawable.ic_action_falsecheck );
        }
        else{
            img = mContext.getResources().getDrawable( R.drawable.ic_action_check );
        }
        img.setBounds( 0, 0, 60, 60 );
        viewHolder.imageName.setText("Order Number: " + mImageNames.get(i));
        viewHolder.imageAddress.setText(mAddresses.get(i));
        viewHolder.statusIcon.setCompoundDrawables( img, null, null, null );
        viewHolder.moreDetails = mDetails.get(i);
        viewHolder.address = addressList.get(i);

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.itemView.setBackgroundResource(R.drawable.rowclick);
                showMessageMap("Order Information", viewHolder.moreDetails, viewHolder.address);
            }
        });
    }


    public void showMessageMap(String title, String message, final String id){
        final com.example.refresh.RecyclerView recyclerView = (com.example.refresh.RecyclerView) mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View mView = inflater.inflate(R.layout.order_details_layout, null);
        builder.setCancelable(true);
        TextView titleView = mView.findViewById(R.id.details_title);
        titleView.setText(title);
        TextView bodyView = mView.findViewById(R.id.details_body);
        bodyView.setText(message);
        builder.setPositiveButton("Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                recyclerView.openMap(id);
            }
        });
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(mView);
        builder.show();
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
        String moreDetails;
        String address;

        public ViewHolder(View itemView){
            super(itemView);
            statusIcon = itemView.findViewById(R.id.icon_recycler);
            imageName = itemView.findViewById(R.id.recycle_orderNum);
            imageAddress = itemView.findViewById(R.id.recycle_address);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            moreDetails = "";
            address = "";
        }



    }
}
