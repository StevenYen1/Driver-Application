package com.example.refresh;
/*
Description:
    The purpose of this class is to pull up a shipping label based on the order.

Specific Features:
    Generates Barcode
    Prints Barcode

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;

public class Printer extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    public final int RETURN_LABEL = 1;
    public final int SHIPPING_LABEL = 2;

    private String orderId;
    private int bitmapPrint = 0;
    private Bitmap shippingLabel = null;
    private Bitmap returnLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        orderId = getIntent().getStringExtra("orderId");
        createShippingLabel(orderId);
        createReturnLabel(orderId);

        ImageView image = findViewById(R.id.barcode_image);

        Button print = findViewById(R.id.print_button);
        print.setEnabled(false);
        print.setOnClickListener(v -> doBitmapPrint());

        Button shippingBtn = findViewById(R.id.shiplabel_button);
        shippingBtn.setOnClickListener(v -> {
            bitmapPrint = SHIPPING_LABEL;
            image.setImageBitmap(shippingLabel);
            print.setEnabled(true);

        });

        Button returnBtn = findViewById(R.id.returnlabel_button);
        returnBtn.setOnClickListener(v -> {
            bitmapPrint = RETURN_LABEL;
            image.setImageBitmap(returnLabel);
            print.setEnabled(true);
        });
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    private void doBitmapPrint(){
        PrintHelper bitmapPrinter = new PrintHelper(this);
        bitmapPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = null;
        if(bitmapPrint == RETURN_LABEL){
            bitmap = returnLabel;
            bitmapPrinter.printBitmap("return-testprint: "+ orderId, bitmap);
        }
        else{
            bitmap = shippingLabel;
            bitmapPrinter.printBitmap("shipment-testprint: "+ orderId, bitmap);
        }
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private void createShippingLabel(String orderId){
        Bitmap bitmap = Bitmap.createBitmap(600, 700, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(WHITE);

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
        Bitmap barcode = null;
        try {
            barcode = encodeAsBitmap(orderId, BarcodeFormat.CODE_128, 500, 250);
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
        canvas.drawColor(WHITE);
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
            Bitmap cartonBarcode = encodeAsBitmap(data.get(COL_CARTONNUMBER), BarcodeFormat.CODE_128, 600, 200);
            Bitmap orderBarcode = encodeAsBitmap(data.get(COL_ORDERNUMBER), BarcodeFormat.CODE_128, 400, 200);

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
