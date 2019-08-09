package com.example.refresh;
/*
Description:
    The purpose of this class is to pull up a shipping label based on the order.

Specific Features:
    
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.refresh.OrderDisplay.ViewOrders;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.EnumMap;
import java.util.Map;

public class Printer extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    public static final String SAMPLE_FILE = "resume2019.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        barcode = getIntent().getStringExtra("orderId");

//        pdfView= (PDFView)findViewById(R.id.pdfView);
//        displayFromAsset(SAMPLE_FILE);
        createBarcode(barcode);
        setupPrint();

    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    private void setupPrint(){
        Button print = findViewById(R.id.print_button);
        print.setOnClickListener(v -> doBitmapPrint());
    }

    private void doBitmapPrint(){
        PrintHelper bitmapPrinter = new PrintHelper(this);
        bitmapPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = createBarcode(barcode);
        bitmapPrinter.printBitmap("barcode-testprint: "+barcode, bitmap);
    }

    private Bitmap createBarcode(String orderId){
        String id = orderId;
        Bitmap barcodeBitmap = null;
        ImageView image = findViewById(R.id.barcode_image);
        TextView barcodeText = findViewById(R.id.barcode_text);
        barcodeText.setText("OrderNumber: "+id);

        try {
            barcodeBitmap = encodeAsBitmap(id, BarcodeFormat.CODE_128, 600, 300);
            image.setImageBitmap(barcodeBitmap);
            return barcodeBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
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
}
