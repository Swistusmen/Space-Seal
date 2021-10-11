package org.freedesktop.gstreamer.tutorials.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DbName="InternalDataBase";
    private static final Integer DbVersion=1;

    private static final String SettingsTable="SettingsTable";
    private static final String FirstColumn="Ip";
    private static final String SecondColumn="Port";
    private static final String ThirdColumn="Path";
    //maybe in the future protocol

    public DBHandler(Context context){
        super(context, DbName, null, DbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String query="CREATE TABLE "+ SettingsTable+ "( "+ FirstColumn+ " STRING NOT NULL PRIMARY KEY, "+ SecondColumn+
                " TEXT NOT NULL, "+ ThirdColumn+" TEXT NOT NULL) ";
        db.execSQL(query);

        db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(FirstColumn,"127.0.0.1");
        values.put(SecondColumn,"8554");
        values.put(ThirdColumn,"/test");
        db.insert(SettingsTable,null,values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getIpAddress(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+SettingsTable+ ";",null);
        String desc= cursor.getString(0);
        /*if(cursor.moveToFirst()){
            desc=cursor.getString(0);
        }*/
        cursor.close();
        db.close();
        return desc;
    }

    public String getIpAddressPath(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select Path from "+SettingsTable+ ";",null);
        String desc="";
        if(cursor.moveToFirst()){
            desc=cursor.getString(2);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public String getPort(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select Port from "+SettingsTable+ ";",null);
        String desc="";
        if(cursor.moveToFirst()){
            desc=cursor.getString(1);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public String getFullAddress(){
        return "rtsp://"+getIpAddress()+":"+getPort()+getIpAddressPath();
    }

    public void updateSettings(String IpAddress, String Port, String AddressPath){
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(FirstColumn,IpAddress);
        values.put(SecondColumn,Port);
        values.put(ThirdColumn,AddressPath);
        db.insert(SettingsTable,null,values);
        db.close();
    }

}
