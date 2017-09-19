package com.example.gobang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class StoreInfo extends SQLiteOpenHelper {
    public static final String KEY_ID = "id";//主键
    public static final String KEY_INFO="info";//待存储信息
    public static final String DATABASE_NAME = "HistoryDatabase.db";//数据库名字
    private static final int DATABASE_VERSION = 1;//版本号
    private static final String DATABASE_TABLE = "HistoryTable";//表名
    public Context context = null;

    private static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + "(" + KEY_ID +
            " integer primary key autoincrement, " +
            KEY_INFO + " text not null);"; //建表指令

    public StoreInfo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }//删除库的方法

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }//创建数据库

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }//更新方法


    public void insert(String str){
        ContentValues values= new ContentValues();
        values.put(KEY_INFO,str);
        try{
            getWritableDatabase().insert(DATABASE_TABLE, KEY_ID, values);
        }catch(Exception e){
        }
    }

    public String read(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        try{
            cursor = db.query(DATABASE_TABLE, new String[] {KEY_INFO}, null, null, null, null, null);
        }catch (Exception e){
            cursor=null;
        }
        if(cursor==null) return "没有历史记录";//表空的情况

        cursor.moveToFirst();//移到开头
        int size=cursor.getCount();
        if (size<1) return "没有历史记录";
        StringBuilder his_info = new StringBuilder("");
        do {
            his_info.append(cursor.getString(cursor.getColumnIndex(KEY_INFO)));
            his_info.append("\r\n");
        }while(cursor.moveToNext());//获取数据库信息

        return his_info.toString();
    }

    public void DelectAllData(){
        getWritableDatabase().delete(DATABASE_TABLE, null, null);
    }//删表的方法

}
