package org.freedesktop.gstreamer.tutorials.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DbName="InternalDataBase";
    private static final Integer DbVersion=2;

    //storing info about protocol
    private static final String SettingsTable="SettingsTable";
    private static final String NullColumn="Id";
    private static final String FirstColumn="Ip";
    private static final String SecondColumn="Port";
    private static final String ThirdColumn="Path";
    //maybe in the future protocol

    private static final String VideosTable="VideoTable";
    private static final String IdColumn="Id";
    private static final String videoLocation="Location";

    public DBHandler(Context context){
        super(context, DbName, null, DbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String query="CREATE TABLE "+ SettingsTable+ "( "+NullColumn+" INTEGER PRIMARY KEY,"+ FirstColumn+ " STRING NOT NULL, "+ SecondColumn+
                " TEXT NOT NULL, "+ ThirdColumn+" TEXT NOT NULL) ";
        db.execSQL(query);
        ContentValues values=new ContentValues();
        values.put(NullColumn,0);
        values.put(FirstColumn,"127.0.0.1");
        values.put(SecondColumn,"8554");
        values.put(ThirdColumn,"/test");
        db.insert(SettingsTable,null,values);

        //db.close();
        //db=getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '"+VideosTable+"'");
        query="CREATE TABLE "+ VideosTable+ "( "+IdColumn+" INTEGER PRIMARY KEY,"+ videoLocation+ " STRING NOT NULL) ";
        db.execSQL(query);
        values=new ContentValues();
        values.put(IdColumn,0);
        values.put(videoLocation," ");

        db.insert(VideosTable,null,values);

        //should be db.close, but it carshes an app
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getIpAddress(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+SettingsTable+ ";",null);
        String desc= "";
        if(cursor.moveToFirst()){
            desc=cursor.getString(1);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public String getIpAddressPath(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+SettingsTable+ ";",null);
        String desc="";
        if(cursor.moveToFirst()){
            desc=cursor.getString(3);
        }
        cursor.close();

        return desc;
    }

    public String getPort(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+SettingsTable+ ";",null);
        String desc="";
        if(cursor.moveToFirst()){
            desc=cursor.getString(2);
        }
        cursor.close();
        //db.close();
        return desc;
    }

    public String getFullAddress(){
        return "rtsp://"+getIpAddress()+":"+getPort()+getIpAddressPath();
    }

    public void updateSettings(String IpAddress, String Port, String AddressPath){
        SQLiteDatabase write=this.getWritableDatabase();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+SettingsTable+ " where( Id=0 );",null);
        if(cursor.moveToFirst()){
            do{
                ContentValues values=new ContentValues();
                values.put(FirstColumn,IpAddress);
                values.put(SecondColumn,Port);
                values.put(ThirdColumn,AddressPath);
                write.update(SettingsTable,values,"Id=?",new String[]{"0"});
            }while(cursor.moveToNext());
        }
        write.close();
        cursor.close();
    }

    public void updateVideo(String path){
        SQLiteDatabase write=this.getWritableDatabase();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+VideosTable+ " where( Id=0 );",null);
        if(cursor.moveToFirst()){
            do{
                ContentValues values=new ContentValues();
                values.put(videoLocation,path);
                write.update(VideosTable,values,"Id=?",new String[]{"0"});
            }while(cursor.moveToNext());
        }
        write.close();
        cursor.close();
    }

    public String getRecentlyRecordedVideoPath(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+VideosTable+ " where( Id=0);",null);
        String desc="";
        if(cursor.moveToFirst()){
            desc=cursor.getString(1);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public String getVideoName(){
        String videoName=getRecentlyRecordedVideoPath();
        String[] result=videoName.split("/");
        return result[result.length-1];
    }

    public String getVideoPath(){
        String videoName=getRecentlyRecordedVideoPath();
        String[] result=videoName.split("/");
        String path="";
        int size= result.length-1;
        for(int i=0;i<size;i++){
            path+=result[i]+"/";
        }
        return path;
    }

}
