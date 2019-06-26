package com.example.refresh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Order.db";
    public static final String TABLE_NAME = "order_table";
    public static final String COL_1 = "OrderNumber";
    public static final String COL_2 = "Address";
    public static final String COL_3 = "Recipient";
    public static final String COL_4 = "Item";
    public static final String COL_5 = "Status";
    public static final String COL_6 = "Signature";
    public static final String COL_7 = "idx";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (OrderNumber TEXT PRIMARY KEY, ADDRESS TEXT, RECIPIENT TEXT, ITEM TEXT, STATUS INT, SIGNATURE TEXT, IDX INTEGER) ");

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


    public boolean insertData(String num, String addr, String recip, String item, int status, String sign, int i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("replace into order_table VALUES ('"+num+"', '"+addr+"', '"+recip+"', '"+item+"', '"+status+"', '"+sign+"', '"+i+"')");
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by idx",null);
        return res;
    }

    public Cursor getInstance(String ORDER_NUMBER){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE OrderNumber = ?",new String[] { ORDER_NUMBER });
        return res;
    }

    public int getStatus(String num){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where OrderNumber = ?", new String[] { num });
        while(res.moveToNext()){
            return res.getInt(4);
        }
        return -1;
    }

    public void updateStatus(String num, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_5, status);
        db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { num });
    }


    public boolean updateData(String num, int status, String sign){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_5, status);
        cv.put(COL_6, sign);

        db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { num });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "OrderNumber = ?", new String[] {id});
    }

    public ArrayList<String> returnData(Cursor data){
        ArrayList<String> dataList = new ArrayList<>();
        while (data.moveToNext()) {
            dataList.add(data.getString(0));
            dataList.add(data.getString(1));
            dataList.add(data.getString(2));
            dataList.add(data.getString(3));
            dataList.add(""+data.getInt(4));
            dataList.add(data.getString(5));
        }
        return dataList;
    }

    public ArrayList<String> removeIndex(String id, int remove){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where IDX > "+remove +" order by idx", null);
        if(res.getCount() == 0){

            //Wrote this code instead of calling getInstance and returnData, because kept causing a crash for some reason.
            Cursor res1 = db.rawQuery("select * from "+TABLE_NAME+" where OrderNumber = ?", new String[] { id });
            ArrayList<String> returnString = new ArrayList<>();
            while(res1.moveToNext()){
                returnString.add(res1.getString(0));
                returnString.add(res1.getString(1));
                returnString.add(res1.getString(2));
                returnString.add(res1.getString(3));
                returnString.add(res1.getString(4));
                returnString.add(res1.getString(5));
            }

            deleteData(id);
            return returnString;
        }
        int i = remove;
        while (res.moveToNext()) {
            String key = res.getString(0);
            ContentValues cv = new ContentValues();
            cv.put(COL_7, i++);
            db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { key });
        }
        Cursor data = getInstance(id);
        ArrayList<String> returnString = returnData(data);
        deleteData(id);
        return returnString;

    }

    public boolean pushIndex(String id, int insert){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where idx >= "+insert+" order by idx", null);
        if(res.getCount() != 0){
            int i = insert;
            while (res.moveToNext()) {
                String key = res.getString(0);
                ContentValues cv = new ContentValues();
                cv.put(COL_7, ++i);
                db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { key });
            }
            return true;
        }
        return true;
    }



    public void updateIndex(String id, int remove, int insert){
        ArrayList<String> info = removeIndex(id, remove);
        pushIndex(id, insert);
        insertData(id, info.get(1), info.get(2), info.get(3), parseInt(info.get(4)), info.get(5), insert);
    }

    public int returnSize(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by idx",null);
        int size = 0;
        while(res.moveToNext()){ size++; }
        return size;
    }

}
