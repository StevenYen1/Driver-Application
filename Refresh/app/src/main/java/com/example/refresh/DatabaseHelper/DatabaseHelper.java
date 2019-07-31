package com.example.refresh.DatabaseHelper;
/*
Description:
    This class is the single direct access point to the SQLite database.
    It is able to create, edit, and delete tables and their entries.

Specific Functions:
    Create database tables "order_table" and "closed_orders_table"
    Insert data into tables
    Delete data from tables
    Query tables for all data, or a specific instance (given an id)
    Update Status, Signature, or Quantity in "order_table"
    Edit "position"(index) of an entry in a table.
    Transfer an entry from one table to the other.

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import static java.lang.Integer.parseInt;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Order.db";
    public static final String ORDER_TABLE = "order_table";
    public static final String CLOSED_TABLE = "closed_orders_table";

    public static final int COL_ORDERNUMBER = 0;
    public static final int COL_ADDRESS = 1;
    public static final int COL_RECIPIENT = 2;
    public static final int COL_ITEM = 3;
    public static final int COL_STATUS = 4;
    public static final int COL_SIGNATURE = 5;
    public static final int COL_INDEX = 6;
    public static final int COL_QUANTITY = 7;
    public static final int COL_CARTONNUMBER = 8;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /*
    Creates tables of the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ORDER_TABLE + " (OrderNumber TEXT PRIMARY KEY, ADDRESS TEXT, RECIPIENT TEXT, ITEM TEXT, STATUS INT, SIGNATURE TEXT, IDX INTEGER, QUANTITY INTEGER, cartonnumber STRING) ");
        db.execSQL("create table if not exists " + CLOSED_TABLE + "(OrderNumber TEXT PRIMARY KEY, ADDRESS TEXT, RECIPIENT TEXT, ITEM TEXT, STATUS INT, SIGNATURE TEXT, IDX INTEGER, QUANTITY INTEGER, cartonnumber STRING)");
    }

    /*
    Creates new versions of each table.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ ORDER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CLOSED_TABLE);
        onCreate(db);
    }

    /*
    Clears the order table and closed table.
     */
    public void clearTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ ORDER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CLOSED_TABLE);
        onCreate(db);
    }

    /*
    Inserts new entry into the order table.
     */
    public void insertOrder(String num, String addr, String recip, String item, int status, String sign, int i, int quantity, String carton){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("replace into order_table VALUES ('"+num+"', '"+addr+"', '"+recip+"', '"+item+"', '"+status+"', '"+sign+"', '"+i+"', '"+quantity+"', '"+carton+"')");
        db.close();
    }

    /*
    Inserts an entry into the closed table.
     */
    public void insertClosed(String num, String addr, String recip, String item, int status, String sign, int i, int quantity, String carton){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("replace into closed_orders_table VALUES ('"+num+"', '"+addr+"', '"+recip+"', '"+item+"', '"+status+"', '"+sign+"', '"+i+"', '"+quantity+"', '"+carton+"')");
        db.close();
    }

    /*
    Queries all orders in the order table.
     */
    public Cursor queryAllOrders(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+ ORDER_TABLE +" order by idx",null);
    }

    /*
    Queries the order table for a specific entry given an OrderNumber
     */
    public Cursor queryOrder(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+ ORDER_TABLE +" where OrderNumber = ?",new String[] { id });
    }

    /*
    Queries all orders from the closed table.
     */
    public Cursor queryClosedAllOrders(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+CLOSED_TABLE+" order by idx",null);
    }

    /*
    Queries the closed table for a specific entry given an OrderNumber
     */
    private Cursor queryClosedOrder(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+CLOSED_TABLE+" where OrderNumber = ?",new String[] { id });
    }

    /*
    Deletes an order from the order table and returns that order as an ArrayList of type String.
     */
    public ArrayList<String> deleteOrder(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor queryResult = db.rawQuery("select * from "+ ORDER_TABLE +" where OrderNumber = ?",new String[] { id });
        ArrayList<String> orderData = dataToList(queryResult);
        db.delete(ORDER_TABLE, "OrderNumber = ?", new String[] {id});
        return orderData;
    }

    /*
    Updates the status of a particular entry in the order table.
     */
    public void updateStatus(String id, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Status", status);
        db.update(ORDER_TABLE, cv, "OrderNumber = ?", new String[] { id });
        db.close();
    }

    /*
    Updates the signature of a particular entry in the order table.
     */
    public void updateSignature(String id, String sign){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Signature", sign);
        db.update(ORDER_TABLE, cv, "OrderNumber = ?", new String[] { id });
        db.close();
    }

    /*
    Updates the quantity of a particular entry in the order table.
     */
    public void updateQuantity(String id, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        db.update(ORDER_TABLE, cv, "OrderNumber = ?", new String[] { id });
        db.close();
    }

    /*
    Converts the query data from a Cursor object to an ArrayList of type String
     */
    private ArrayList<String> dataToList(Cursor data){
        ArrayList<String> dataList = new ArrayList<>();
        while (data.moveToNext()) {
            dataList.add(data.getString(COL_ORDERNUMBER));
            dataList.add(data.getString(COL_ADDRESS));
            dataList.add(data.getString(COL_RECIPIENT));
            dataList.add(data.getString(COL_ITEM));
            dataList.add(data.getString(COL_STATUS));
            dataList.add(data.getString(COL_SIGNATURE));
            dataList.add(data.getString(COL_QUANTITY));
            dataList.add(data.getString(COL_CARTONNUMBER));
        }
        return dataList;
    }

    /*
    Move an order from its current position(index) to a new position in the database.
     */
    public void moveOrder(String id, int remove, int insert){
        ArrayList<String> removedInstance = popFromList(id, remove);
        pushToList(removedInstance, insert);
    }

    /*
    Pops an order from the order table and returns the data as an ArrayList of type String.
     */
    private ArrayList<String> popFromList(String id, int remove){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor queryResult = db.rawQuery("select * from "+ ORDER_TABLE +" where IDX > "+remove +" order by idx", null);
        if(queryResult.getCount() > 0){
            shiftEntriesDown(db, queryResult, remove, ORDER_TABLE);
        }
        ArrayList<String> removedInstance = deleteOrder(id);
        db.close();
        return removedInstance;
    }


    /*
    Takes an order as an ArrayList of type String and pushes it to the order table.
     */
    private void pushToList(ArrayList<String> dataList, int insert){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ ORDER_TABLE +" where idx >= "+insert+" order by idx", null);
        if(res.getCount() > 0){
            shiftEntriesUp(db, res, insert, ORDER_TABLE);
        }
        insertOrder(dataList.get(0), dataList.get(1), dataList.get(2), dataList.get(3),
                parseInt(dataList.get(4)), dataList.get(5), insert, parseInt(dataList.get(6)), dataList.get(7));
        db.close();
    }

    /*
    Increments index of each entry by value 1.
     */
    private void shiftEntriesUp(SQLiteDatabase db, Cursor queryResult, int insert, String table){
        int i = insert;
        while (queryResult.moveToNext()) {
            String orderNum = queryResult.getString(COL_ORDERNUMBER);
            ContentValues cv = new ContentValues();
            cv.put("idx", ++i);
            db.update(table, cv, "OrderNumber = ?", new String[] { orderNum });
        }
    }

    /*
    Decrements index of each entry by 1 value.
     */
    private void shiftEntriesDown(SQLiteDatabase db, Cursor queryResult, int remove, String table){
        int i = remove;
        while (queryResult.moveToNext()) {
            String orderNumber = queryResult.getString(COL_ORDERNUMBER);
            ContentValues cv = new ContentValues();
            cv.put("idx", i++);
            db.update(table, cv, "OrderNumber = ?", new String[] { orderNumber });
        }
    }

    /*
    Returns the size of a given table
     */
    public int returnSize(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+table+" order by idx",null);
        int size = 0;
        while(res.moveToNext()){ size++; }
        db.close();
        return size;
    }

    /*
    Moves an order from the order_table to the closed_table.
     */
    public void closeOrder(String id, int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = queryOrder(id);
        ArrayList<String> item_data = dataToList(cursor);

        insertClosed(item_data.get(0),item_data.get(1),item_data.get(2),item_data.get(3),
                parseInt(item_data.get(4)),item_data.get(5), returnSize(CLOSED_TABLE), parseInt(item_data.get(6)), item_data.get(7));
        popFromList(id, index);
        db.close();
    }

    /*
    Moves an order from the closed_table to the order_table.
     */
    public void reopenOrder(String id, int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = queryClosedOrder(id);
        ArrayList<String> item_data = dataToList(cursor);
        insertOrder(item_data.get(0),item_data.get(1),item_data.get(2),item_data.get(3),
                parseInt(item_data.get(4)),item_data.get(5), returnSize(ORDER_TABLE), parseInt(item_data.get(6)), item_data.get(7));
        popFromClosed(id, index);
        db.close();
    }

    /*
    Deletes order from closed table. Updates indices accordingly.
     */
    private void popFromClosed(String id, int remove){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor queryResult = db.rawQuery("select * from "+CLOSED_TABLE+" where IDX > "+remove +" order by idx", null);
        if(queryResult.getCount() > 0){
            shiftEntriesDown(db, queryResult, remove, CLOSED_TABLE);
        }
        db.delete(CLOSED_TABLE, "OrderNumber = ?", new String[] {id});
        db.close();
    }
}
