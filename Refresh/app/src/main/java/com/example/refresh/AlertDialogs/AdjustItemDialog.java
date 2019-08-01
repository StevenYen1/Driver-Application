package com.example.refresh.AlertDialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.EditOrders.AdjustOrders;
import com.example.refresh.R;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.example.refresh.EditOrders.AddOrders.isParsable;
import static java.lang.Integer.parseInt;

public class AdjustItemDialog {

    private Context context;
    private int quantity;
    private int operation;
    private String orderId;
    private FancyButton ok;

    private static final int SUBTRACTION = 0;
    private static int ADDITION = 1;


    public AdjustItemDialog(Context context, String orderId, int quantity) {
        this.context = context;
        this.orderId = orderId;
        this.quantity = quantity;
        this.operation = SUBTRACTION;
    }

    public AlertDialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = inflater.inflate(R.layout.adjust_quantity_layout, null);
        ok = mView.findViewById(R.id.quantity_layout_ok);

        builder.setView(mView);
        AlertDialog alert = builder.create();
        setupInitialValues(mView, alert);
        return alert;

    }

    private void setupInitialValues(View mView, AlertDialog alert){
        TextView titleView = mView.findViewById(R.id.quantity_layout_title);
        titleView.setText("OrderNumber: " + orderId);

        final TextView quantityView = mView.findViewById(R.id.quantity_layout_input_quantity);
        quantityView.setText(""+quantity);


        TextView remainingView = mView.findViewById(R.id.quantity_layout_input_remaining);
        remainingView.setText(""+quantity);

        setupButtons(mView, remainingView, alert);
        setupValueInput(mView, remainingView);
    }

    private void setupValueInput(View mView, TextView remainingView){
        EditText adjustmentView = mView.findViewById(R.id.quantity_layout_input_adjustment);
        adjustmentView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(isParsable(s.toString())){
                        int remaining = calculateResult(""+quantity, s.toString());
                        remainingView.setText(""+remaining);
                        if(calculateResult(""+quantity, s.toString())>=0){
                            ok.setEnabled(true);
                        }
                        else{
                            ok.setEnabled(false);
                        }
                    }
                    else{
                        remainingView.setText("N/A");
                        ok.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupButtons(View mView, TextView remainingView, AlertDialog alert){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        ok.setOnClickListener(v -> {
            databaseHelper.updateQuantity(orderId, parseInt(remainingView.getText().toString()));
            alert.dismiss();
            Intent refresh = new Intent(context, AdjustOrders.class);
            refresh.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        });

        FancyButton cancel = mView.findViewById(R.id.quantity_layout_cancel);
        cancel.setOnClickListener(v -> {
            alert.cancel();
            operation = SUBTRACTION;
        });

        FancyButton sub = mView.findViewById(R.id.minus);
        FancyButton add = mView.findViewById(R.id.plus);

        add.setOnClickListener(v -> {
            operation = ADDITION;
            add.setGhost(false);
            sub.setGhost(true);
            remainingView.setText("Re-enter Adjustment Value");
            ok.setEnabled(false);
        });


        sub.setOnClickListener(v -> {
            operation = SUBTRACTION;
            sub.setGhost(false);
            add.setGhost(true);
            remainingView.setText("Re-enter Adjustment Value");
            ok.setEnabled(false);
        });
    }


    private int calculateResult(String quantity, String adjustment) throws Exception {
        int intQ = parseInt(quantity);
        boolean isParsable = isParsable(adjustment);
        if(isParsable){
            int intAdjust = parseInt(adjustment);
            if(operation==SUBTRACTION){return intQ - intAdjust;}
            return intQ + intAdjust;
        }
        return -1;
    }
}
