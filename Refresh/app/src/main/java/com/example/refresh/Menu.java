package com.example.refresh;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mehdi.sakout.fancybuttons.FancyButton;

public class Menu extends AppCompatActivity {

    private FancyButton scan_btn;
    private FancyButton viewOrders;
    private FancyButton editOrders;
    private FancyButton restCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        scan_btn = findViewById(R.id.scan_btn_open);
        viewOrders = findViewById(R.id.view_route_btn);
        editOrders = findViewById(R.id.edit_orders_btn);
        restCall = findViewById(R.id.call_server_btn);


        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.choose_scan_layout, null);
                Button openCamera = mView.findViewById(R.id.camera_btn);
                Button openExternal = mView.findViewById(R.id.external_btn);
                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(Scanner.class);
                    }
                });

                openExternal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { openActivity(External_Scanner.class);
                    }
                });
                builder.setView(mView);
                builder.show();
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(RecyclerView.class);
            }
        });

        editOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setCancelable(true);
                View mView = getLayoutInflater().inflate(R.layout.edit_orders_layout, null);
                Button add = mView.findViewById(R.id.addOrder);
                Button transfer = mView.findViewById(R.id.transfer_orders);
                Button close = mView.findViewById(R.id.close);
                Button reopen = mView.findViewById(R.id.reopen);
                Button adjust = mView.findViewById(R.id.adjust_orders);
                Button void_order = mView.findViewById(R.id.void_orders);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(AddOrders.class);
                    }
                });

                transfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(TransferOrders.class);
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(CloseOrders.class);
                    }
                });

                reopen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(ReopenOrders.class);
                    }
                });

                adjust.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(AdjustOrders.class);
                    }
                });

                void_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(VoidOrder.class);
                    }
                });
                builder.setView(mView);
                builder.show();
            }
        });

        restCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(RestList.class);
            }
        });
    }

    public void openActivity(Class nextView){
        Intent intent = new Intent(Menu.this, nextView);
        startActivity(intent);
    }

    //cannot go back to the download page
    @Override
    public void onBackPressed() {
    }
}
