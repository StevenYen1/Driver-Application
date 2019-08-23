package com.example.refresh.PrintLabel;
/*
The purpose of this class is open a connection to a bluetooth printer
and print a return label.

Steven Yen
Staples Intern Summer 2019

Credit to solution:
https://github.com/imrankst1221/Thermal-Printer-in-Android
 */
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ADDRESS;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_CARTONNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_ORDERNUMBER;
import static com.example.refresh.DatabaseHelper.DatabaseHelper.COL_RECIPIENT;

public class Bluetooth extends AppCompatActivity {
    /*
    private instance variables
     */
    private ArrayList<String> data;
    private TextView myLabel;

    private EditText myTextbox;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothConnector.BluetoothSocketWrapper socket;

    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    private FancyButton textButton;
    private FancyButton labelButton;
    private FancyButton closeButton;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setupData();
        setupButtons();

    }

    /*
    Data setup
     */
    private void setupData(){
        data = queryData(getIntent().getStringExtra("orderId"));
        myLabel = findViewById(R.id.bluetooth_status);
        myTextbox = findViewById(R.id.bluetooth_message);
    }

    /*
    Button setup
     */
    private void setupButtons(){
        try {
            FancyButton openButton = findViewById(R.id.open_bluetooth);
            openButton.setOnClickListener(v -> {
                try {
                    findBT("BlueTooth Printer");
                    openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            textButton = findViewById(R.id.print_text);
            textButton.setEnabled(false);
            textButton.setOnClickListener(v -> {
                try {
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            labelButton = findViewById(R.id.print_label);
            labelButton.setEnabled(false);
            labelButton.setOnClickListener(v -> printReturnLabel());

            closeButton = findViewById(R.id.close_bluetooth);
            closeButton.setEnabled(false);
            closeButton.setOnClickListener(v -> {
                try {
                    closeBT();
                    myLabel.setText("Bluetooth Closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Finds a specific bluetooth device within the list of paired devices
     */
    @SuppressLint("SetTextI18n")
    private void findBT(String printerName) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    if (device.getName().equals(printerName)) {
                        mmDevice = device;
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    /*
    tries to open a connection to the bluetooth printer device
     */
    @SuppressLint("SetTextI18n")
    void openBT() throws IOException {
        if(mmDevice==null){
            myLabel.setText("No Device Found. Please Check Connections.");
            return;
        }
        BluetoothConnector bluetoothConnector = new BluetoothConnector(this, mmDevice, true, mBluetoothAdapter, null);
        socket = bluetoothConnector.connect();


        mmOutputStream = socket.getOutputStream();
        mmInputStream = socket.getInputStream();

        beginListenForData();

        myLabel.setText("Bluetooth Opened");
        setButtons(true);

    }

    private void setButtons(boolean canPress){
        closeButton.setEnabled(canPress);
        labelButton.setEnabled(canPress);
        textButton.setEnabled(canPress);
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            // specify US-ASCII encoding
            Thread workerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(
                                            readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length
                                    );
                                    // specify US-ASCII encoding
                                    final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;
                                    handler.post(() -> myLabel.setText(data));
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    this will send text data to be printed by the bluetooth printer
     */
    @SuppressLint("SetTextI18n")
    private void sendData() throws IOException {
        try {

            // the text typed by the user
            String msg = myTextbox.getText().toString();
            msg += "\n";

            mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(msg);
            // tell the user data were sent
            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    prints a return label
     */
    private void printReturnLabel(){
        String str = "Return To:";
        ArrayList<String> parsedAddress = parseAddress(data.get(COL_ADDRESS));
        try {
            mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        printText(str.getBytes());
        printText(data.get(COL_RECIPIENT));
        printText(parsedAddress.get(0));
        printText(parsedAddress.get(1));
        printNewLine();
        try {
            mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        printText("Carton Number");
        printBarcode(data.get(COL_CARTONNUMBER));
        printText(data.get(COL_CARTONNUMBER));
        printNewLine();
        printText("Order Number");
        printBarcode(data.get(COL_ORDERNUMBER));
        printText(data.get(COL_ORDERNUMBER));
    }

    /*
    print barcode
     */
    public void printBarcode(String content) {
        try {
            Bitmap bmp = createBarcode(content);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            }else{
                Log.e("Print error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }


    /*
    print new line
     */
    private void printNewLine() {
        try {
            mmOutputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    print text
     */
    private void printText(String msg) {
        try {
            // Print normal text
            mmOutputStream.write(msg.getBytes());
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    print byte[]
     */
    private void printText(byte[] msg) {
        try {
            // Print normal text
            mmOutputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    close the connection to bluetooth printer.
     */
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            socket.close();
            myLabel.setText("Bluetooth Closed");
            setButtons(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Creates the bitmap of the barcode
     */
    private Bitmap createBarcode(String content){

        Bitmap bitmap = Bitmap.createBitmap(400, 80, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        try {
            BarcodeBitmap barcodeBitmap = new BarcodeBitmap(content);
            Bitmap barcode = barcodeBitmap.encodeAsBitmap(BarcodeFormat.UPC_A, 200, 80);

            Paint centerPaint = new Paint();
            centerPaint.setColor(Color.BLACK);
            centerPaint.setTextAlign(Paint.Align.CENTER);
            centerPaint.setTextSize(25);
            canvas.drawBitmap(barcode, 100, 0, null);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /*
    Gets information related to the specific orderId.
    Returns the information as an ArrayList of type String.
     */
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

    /*
    Parses an Address into two sections.
    NOTE: THIS ONLY WORKS FOR THE SPECIFIC ADDRESS FORMAT I CURRENTLY AM USING
     */
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
