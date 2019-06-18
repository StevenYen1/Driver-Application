package com.example.refresh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Order.db";
    public static final String TABLE_NAME = "order_table";
    public static final String COL_1 = "OrderNumber";
    public static final String COL_2 = "Address";
    public static final String COL_3 = "Recipient";
    public static final String COL_4 = "Item";
    public static final String COL_5 = "Status";
    public static final String COL_6 = "Signature";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (OrderNumber TEXT PRIMARY KEY, ADDRESS TEXT, RECIPIENT TEXT, ITEM TEXT, STATUS TEXT, SIGNATURE TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //MIGHT WANT TO DELETE ALL DATABASE INFO BEFOREHAND. IF NOT, IT MAKE CAUSE PROBLEMS
    public boolean insertData(String num, String addr, String recip, String item, String status, String sign){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("replace into order_table VALUES ('"+num+"', '"+addr+"', '"+recip+"', '"+item+"', '"+status+"', '"+sign+"')");
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Cursor getInstance(String ORDER_NUMBER){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE OrderNumber = "+ORDER_NUMBER,null);
        return res;
    }

    public boolean updateData(String num, String status, String sign){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_5, status);
        cv.put(COL_6, sign);

        db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { num });
        return true;
    }




}
