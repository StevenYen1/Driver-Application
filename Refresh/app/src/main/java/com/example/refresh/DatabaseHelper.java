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
    public static final String CLOSED_TABLE = "closed_orders_table";
    public static final String COL_1 = "OrderNumber";
    public static final String COL_2 = "Address";
    public static final String COL_3 = "Recipient";
    public static final String COL_4 = "Item";
    public static final String COL_5 = "Status";
    public static final String COL_6 = "Signature";
    public static final String COL_7 = "idx";
    public static final String COL_8 = "quantity";
    public static final String COL_9 = "cartonnumber";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (OrderNumber TEXT PRIMARY KEY, ADDRESS TEXT, RECIPIENT TEXT, ITEM TEXT, STATUS INT, SIGNATURE TEXT, IDX INTEGER, QUANTITY INTEGER, cartonnumber INTEGER) ");
        db.execSQL("create table if not exists " + CLOSED_TABLE + "(OrderNumber TEXT PRIMARY KEY, ADDRESS TEXT, RECIPIENT TEXT, ITEM TEXT, STATUS INT, SIGNATURE TEXT, IDX INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CLOSED_TABLE);
        onCreate(db);
    }

    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CLOSED_TABLE);
        onCreate(db);
    }


    public boolean insertData(String num, String addr, String recip, String item, int status, String sign, int i, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("replace into order_table VALUES ('"+num+"', '"+addr+"', '"+recip+"', '"+item+"', '"+status+"', '"+sign+"', '"+i+"', '"+quantity+"', 0)");
        db.close();
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by idx",null);
        return res;
    }

    public Cursor getInstance(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where OrderNumber = ?",new String[] { id });
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
        db.close();
    }


    public boolean updateData(String num, int status, String sign){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_5, status);
        cv.put(COL_6, sign);

        db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { num });
        db.close();
        return true;
    }

    public void updateQuantity(String num, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_8, quantity);
        db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { num });
        db.close();
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer result = db.delete(TABLE_NAME, "OrderNumber = ?", new String[] {id});
        db.close();
        return result;
    }

    public ArrayList<String> returnData(Cursor data){
        ArrayList<String> dataList = new ArrayList<>();
        while (data.moveToNext()) {
            dataList.add(data.getString(0));
            dataList.add(data.getString(1));
            dataList.add(data.getString(2));
            dataList.add(data.getString(3));
            dataList.add(data.getString(4));
            dataList.add(data.getString(5));
            dataList.add(data.getString(7));
            dataList.add(data.getString(8));
        }
        return dataList;
    }

    public ArrayList<String> removeIndex(String id, int remove){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where IDX > "+remove +" order by idx", null);

        if(res.getCount() == 0){
            ArrayList<String> returnString;

            Cursor res1 = getInstance(id);
            returnString = returnData(res1);
            deleteData(id);
            db.close();
            return returnString;
        }
        int i = remove;
        while (res.moveToNext()) {
            String key = res.getString(0);
            ContentValues cv = new ContentValues();
            cv.put(COL_7, i++);
            db.update(TABLE_NAME, cv, "OrderNumber = ?", new String[] { key });
        }
        db.close();
        Cursor data = getInstance(id);
        ArrayList<String> returnString = returnData(data);
        deleteData(id);
        return returnString;

    }

    public void pushIndex(String id, int insert){
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
        }
        db.close();
    }



    public void updateIndex(String id, int remove, int insert){
        ArrayList<String> info = removeIndex(id, remove);
        pushIndex(id, insert);
        insertData(id, info.get(1), info.get(2), info.get(3), parseInt(info.get(4)), info.get(5), insert, parseInt(info.get(7)));
    }

    public int returnSize(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+table+" order by idx",null);
        int size = 0;
        while(res.moveToNext()){ size++; }
        db.close();
        return size;
    }


    //closed orders methods
    public boolean insert_closed(String num, String addr, String recip, String item, int status, String sign, int i, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("replace into closed_orders_table VALUES ('"+num+"', '"+addr+"', '"+recip+"', '"+item+"', '"+status+"', '"+sign+"', '"+i+"', '"+quantity+"', 0)");
        return true;
    }

    public void close_order(String id, int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = getInstance(id);
        ArrayList<String> item_data = new ArrayList<>();
        item_data = returnData(cursor);
        insert_closed(item_data.get(0),item_data.get(1),item_data.get(2),item_data.get(3),
                parseInt(item_data.get(4)),item_data.get(5), returnSize(CLOSED_TABLE), parseInt(item_data.get(7)));
        removeIndex(id, index);
        db.close();
    }

    public void reopen_order(String id, int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = get_closed_instance(id);
        ArrayList<String> item_data = new ArrayList<>();
        item_data = returnData(cursor);
        insertData(item_data.get(0),item_data.get(1),item_data.get(2),item_data.get(3),
                parseInt(item_data.get(4)),item_data.get(5), returnSize(TABLE_NAME), parseInt(item_data.get(7)));
        remove_index_closed(id, index);
        db.close();

    }

    public Cursor get_closed_all(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+CLOSED_TABLE+" order by idx",null);
        return res;
    }

    public Cursor get_closed_instance(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+CLOSED_TABLE+" where OrderNumber = ?",new String[] { id });
        return res;
    }

    public ArrayList<String> remove_index_closed(String id, int remove){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+CLOSED_TABLE+" where IDX > "+remove +" order by idx", null);

        if(res.getCount() == 0){
            ArrayList<String> returnString;

            Cursor res1 = get_closed_instance(id);
            returnString = returnData(res1);
            db.delete(CLOSED_TABLE, "OrderNumber = ?", new String[] {id});
            db.close();
            return returnString;
        }
        int i = remove;
        while (res.moveToNext()) {
            String key = res.getString(0);
            ContentValues cv = new ContentValues();
            cv.put(COL_7, i++);
            db.update(CLOSED_TABLE, cv, "OrderNumber = ?", new String[] { key });
        }
        Cursor data = get_closed_instance(id);
        ArrayList<String> returnString = returnData(data);
        db.delete(CLOSED_TABLE, "OrderNumber = ?", new String[] {id});
        db.close();
        return returnString;
    }

}
