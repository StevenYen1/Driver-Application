package com.example.refresh;
/*
Description:
    The purpose of this class is to create a return/shipping label based on the order.

Specific Features:
    Generates Shipping Label
    Generates Return Label
    Prints Label

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.scandit.recognition.Barcode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;

public class Printer extends AppCompatActivity {

    private final int RETURN_LABEL = 1;
    private final int SHIPPING_LABEL = 2;

    private String orderId;
    private int bitmapPrint = 0;
    private Bitmap shippingLabel = null;
    private Bitmap returnLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        orderId = getIntent().getStringExtra("orderId");
        createLabels(orderId);
        setupButtons();

    }

    private void createLabels(String orderId){
        createShippingLabel(orderId);
        createReturnLabel(orderId);
    }

    private void setupButtons(){
        ImageView image = findViewById(R.id.barcode_image);

        Button wifiPrint = findViewById(R.id.print_wifi);
        wifiPrint.setEnabled(false);
        wifiPrint.setOnClickListener(v -> doBitmapPrint());

        Button bluetoothFind = findViewById(R.id.find_bluetooth);
        bluetoothFind.setOnClickListener(v -> {
            Intent intent = new Intent(this, Bluetooth.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

        FancyButton shippingBtn = findViewById(R.id.shiplabel_button);
        shippingBtn.setOnClickListener(v -> {
            bitmapPrint = SHIPPING_LABEL;
            image.setImageBitmap(shippingLabel);
            wifiPrint.setEnabled(true);

        });

        FancyButton returnBtn = findViewById(R.id.returnlabel_button);
        returnBtn.setOnClickListener(v -> {
            bitmapPrint = RETURN_LABEL;
            image.setImageBitmap(returnLabel);
            wifiPrint.setEnabled(true);
        });
    }

    private void doBitmapPrint(){
        PrintHelper bitmapPrinter = new PrintHelper(this);
        bitmapPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap;
        if(bitmapPrint == RETURN_LABEL){
            bitmap = returnLabel;
            bitmapPrinter.printBitmap("return-testprint: "+ orderId, bitmap);
        }
        else{
            bitmap = shippingLabel;
            bitmapPrinter.printBitmap("shipment-testprint: "+ orderId, bitmap);
        }
    }

    private void createShippingLabel(String orderId){
        Bitmap bitmap = Bitmap.createBitmap(600, 700, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        ArrayList<String> list = queryData(orderId);
        ArrayList<String> parsedAddress = parseAddress(list.get(COL_ADDRESS));

        canvas.drawText("Staples Inc. Corporate", 20,20, paint);
        canvas.drawText("500 Staples Drive", 20, 45, paint);
        canvas.drawText("Framingham MA 01702", 20, 70, paint);

        paint.setTextSize(35);
        canvas.drawText(list.get(COL_RECIPIENT), 80, 200, paint);
        canvas.drawText(parsedAddress.get(0), 80, 240, paint);
        canvas.drawText(parsedAddress.get(1), 80, 280, paint);

        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("SHIP", 20, 200, paint);
        canvas.drawText("TO:", 20, 220, paint);

        paint.setStrokeWidth(6);
        canvas.drawLine(0, 310, 600, 310, paint);
        canvas.drawLine(0, 670, 600, 670, paint);

        paint.setStrokeWidth(3);
        canvas.drawLine(0,0,599,0, paint);
        canvas.drawLine(0,700,599,700, paint);
        canvas.drawLine(0,0,0,700, paint);
        canvas.drawLine(599,0,599,700, paint);


        Paint centerPaint = new Paint();
        centerPaint.setTextAlign(Paint.Align.CENTER);
        centerPaint.setTextSize(28);
        int xPos = (canvas.getWidth() / 2);
        canvas.drawText("Order Tracking #", xPos, 350, centerPaint);
        BarcodeBitmap barcodeBitmap = new BarcodeBitmap(orderId);

        Bitmap barcode;
        try {
            barcode = barcodeBitmap.encodeAsBitmap(BarcodeFormat.CODE_128, 500, 250);
            canvas.drawBitmap(barcode, 50, 370, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        canvas.drawText(this.orderId, xPos,650, centerPaint);
        this.shippingLabel = bitmap;
    }


    private void createReturnLabel(String orderId){
        ArrayList<String> data = queryData(orderId);
        Bitmap bitmap = Bitmap.createBitmap(600, 750, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Return To :", 20,20, paint);
        canvas.drawText(data.get(COL_RECIPIENT), 20, 45, paint);
        ArrayList<String> parsedAddress = parseAddress(data.get(COL_ADDRESS));
        canvas.drawText(parsedAddress.get(0), 20, 70, paint);
        canvas.drawText(parsedAddress.get(1), 20, 95, paint);

        try {
            BarcodeBitmap cartonNumBitmap = new BarcodeBitmap(data.get(COL_CARTONNUMBER));
            BarcodeBitmap orderNumBitmap = new BarcodeBitmap(data.get(COL_ORDERNUMBER));


            Bitmap cartonBarcode = cartonNumBitmap.encodeAsBitmap(BarcodeFormat.CODE_128, 600, 200);
            Bitmap orderBarcode = orderNumBitmap.encodeAsBitmap(BarcodeFormat.CODE_128, 400, 200);

            Paint centerPaint = new Paint();
            centerPaint.setTextAlign(Paint.Align.CENTER);
            centerPaint.setTextSize(25);
            int xPos = (canvas.getWidth() / 2);
            int startIndex = 170;
            int index = startIndex;
            canvas.drawText("Carton Id", xPos, index, centerPaint);
            index+=10;
            canvas.drawBitmap(cartonBarcode, 0, index, null);
            index+=225;
            canvas.drawText(data.get(COL_CARTONNUMBER), xPos,index, centerPaint);
            index+=60;
            canvas.drawText("Order Number", xPos, index, centerPaint);
            index+=10;
            canvas.drawBitmap(orderBarcode, 100, index, null);
            index+=225;
            canvas.drawText(data.get(COL_ORDERNUMBER), xPos,index, centerPaint);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        paint.setStrokeWidth(3);
        canvas.drawLine(0,0,599,0, paint);
        canvas.drawLine(0,750,599,750, paint);
        canvas.drawLine(0,0,0,750, paint);
        canvas.drawLine(599,0,599,750, paint);
        this.returnLabel = bitmap;
        Log.d("tag", "createReturnLabel: " + returnLabel.toString());
    }

    private ArrayList<String> queryData(String orderId){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor queryResults = databaseHelper.queryOrder(orderId);
        while(queryResults.moveToNext()){
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0; i < 9; i++){
                list.add(queryResults.getString(i));
            }
            return list;
        }
        return new ArrayList<>();
    }

    //THIS ONLY WORKS FOR THE SPECIFIC ADDRESS FORMAT I CURRENTLY AM UTILIZING
    private ArrayList<String> parseAddress(String address){
        ArrayList<String> parsedAddress = new ArrayList<>();
        for(int i = 0; i < address.length(); i++){
            if(address.charAt(i)==','){
                parsedAddress.add(address.substring(0, i));
                parsedAddress.add(address.substring(i+2));
                return parsedAddress;
            }
        }
        parsedAddress.add("No address available");
        parsedAddress.add("No address available");
        return parsedAddress;
    }
}
