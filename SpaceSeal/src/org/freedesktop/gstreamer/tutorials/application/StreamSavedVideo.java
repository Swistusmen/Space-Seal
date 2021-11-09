package org.freedesktop.gstreamer.tutorials.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.Config;

import org.freedesktop.gstreamer.GStreamer;

import java.io.File;
import java.lang.reflect.Array;

public class StreamSavedVideo extends AppCompatActivity implements View.OnClickListener {
    private native String nativeGetGStreamerInfo();
    private native void nativeInit(String pipelineDescription,String ip, String port,String path);
    private static native void nativeEnableCRunJava();
    private native String nativeRandom(String ip, String port,String path);
    private DBHandler dbHandler;
    private String ipAddress;
    private String path;
    private String port;
    private Integer testInteger=999;
    private long native_custom_data;
    private long nativeStreammingObject;
    private String prot;
    TextView fileToStream=null;
    String fileToStreamString="";
    StreammingProtocol protocol=StreammingProtocol.HLS;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );

        Button stream=(Button)findViewById(R.id.StreamSaveVideoButton);
        Button choseFile=(Button)findViewById(R.id.chooseFileToStream);
        RadioButton hls=(RadioButton)findViewById(R.id.HLSStream);
        RadioButton rtsp=(RadioButton)findViewById(R.id.RTSPStream);
        fileToStream=(TextView)findViewById(R.id.fileToStream);

        stream.setOnClickListener(this);
        FilePicker picker=new FilePicker();
        choseFile.setOnClickListener(picker);
        hls.setOnClickListener(new HLSChosen());
        rtsp.setOnClickListener(new RTSPChosen());
        prot="HLS";

        initDataFromDatabase();

        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //nativeInit(PipelineBuilder.buildPipeline(dbHandler.getRecentlyRecordedVideoPath()),ipAddress,port,path);

        TextView aboutText=(TextView) findViewById(R.id.AboutText);
        fileToStreamString=dbHandler.getRecentlyRecordedVideoPath();
        fileToStream.setText(fileToStreamString);
        aboutText.setText("Welcome to " + nativeRandom(ipAddress,port,path)+" "+testInteger+" !");//nativeGetGStreamerInfo() + " !");
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("tutorial-1");
        nativeEnableCRunJava();
    }

    private void initDataFromDatabase(){
        dbHandler=new DBHandler(this);
        path=dbHandler.getIpAddressPath();
        ipAddress=dbHandler.getIpAddress();
        port=dbHandler.getPort();
    }

    @Override
    public void onClick(View v) {
        if (protocol==StreammingProtocol.RTSP)
            prot="RTSP";
        nativeInit(PipelineBuilder.buildPipeline(fileToStreamString,protocol),prot,port,path);
    }

    private class FilePicker implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.setType("video/*");
            startActivityForResult(fileIntent, 0);
        }
    }

    private class HLSChosen implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            protocol=StreammingProtocol.HLS;
        }
    }

    private class RTSPChosen implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            protocol=StreammingProtocol.RTSP;
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri= data.getData();
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        fileToStreamString=dbHandler.getVideoPath()+c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));

        Log.i(Config.TAG,fileToStreamString);
        fileToStream.setText(fileToStreamString);
    }
}

