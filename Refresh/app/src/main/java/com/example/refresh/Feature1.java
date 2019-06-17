package com.example.refresh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Feature1 extends Activity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private String signatureImage;
    private ArrayList<String> completedOrders;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature1);

        myDb = new DatabaseHelper(this);

        mSignaturePad = findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(Feature1.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = findViewById(R.id.clear_button);
        mSaveButton = findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] b = stream.toByteArray();

                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                File directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(directory, getIntent().getStringExtra("orderNumber")+"_sign.txt");

                saveFile(file, encodedImage);
//                Toast.makeText(Feature1.this, "The string being written to file:\n"+encodedImage, Toast.LENGTH_LONG).show();
                if(!file.exists()){
                    try {
                        file.createNewFile();
                        saveFile(file, encodedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                signatureImage = file.getName();
                completedOrders = getIntent().getStringArrayListExtra("completedOrders");
                for(String x: completedOrders){
                    myDb.updateData(x, "Delivered", encodedImage);
                    viewInstance(x);
                }
                //FIND A WAY TO RETURN TO SCANNED ITEMS WHILE UPDATING THE REMAINING COMPLETED ORDERS
//                returnToList();
            }
        });
    }

    public void viewInstance(String order_num){
        Log.d("ORDER NUMBER: ", order_num);
        Cursor res = myDb.getInstance(order_num);
        if(res.getCount() == 0){
            showMessage("Error", "No Data Found");
            return;
        }

        StringBuffer buffer = new StringBuffer();

        while (res.moveToNext()) {
            buffer.append("Order number: " + res.getString(0)+"\n");
            buffer.append("Address: " + res.getString(1)+"\n");
            buffer.append("Recipient: " + res.getString(2)+"\n");
            buffer.append("Item: " + res.getString(3)+"\n");
            buffer.append("Status: " + res.getString(4)+"\n");
            buffer.append("Sign: " + res.getString(5)+"\n");
            buffer.append("\n");
        }

        showMessage("Order Information", buffer.toString());

    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setPositiveButton("Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
            }
        });
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.setMessage(message);
        builder.show();
    }

    public void saveFile(File file, String text){
        try{
            FileOutputStream fos = openFileOutput(file.getName(), Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Fails1", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Fails2", Toast.LENGTH_SHORT).show();
        }
    }



    public void returnToList(){
        Intent intent = new Intent(this, Address.class);
        intent.putExtra("orderComplete", this.getIntent().getStringExtra("orderNumber"));
//        intent.putExtra("completedOrders", updateCompletedOrders());
        intent.putExtra("signature", signatureImage);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Feature1.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        Feature1.this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
