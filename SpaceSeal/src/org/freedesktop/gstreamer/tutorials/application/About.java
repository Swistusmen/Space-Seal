package org.freedesktop.gstreamer.tutorials.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import org.freedesktop.gstreamer.GStreamer;

import java.lang.reflect.Array;

public class About extends AppCompatActivity {
    private native String nativeGetGStreamerInfo();
    private native void nativeInit(String pipelineDescription,String ip, String port,String path);
    private static native void nativeEnableCRunJava();
    private native String nativeRandom(String ip, String port,String path);
    private DBHandler dbHandler;
    private String ipAddress;
    private String path;
    private String port;
    private long native_custom_data;
    private long nativeStreammingObject;
    private Integer testInteger=999;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );

        initDataFromDatabase();

        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        nativeInit(PipelineBuilder.buildPipeline(dbHandler.getRecentlyRecordedVideoPath()),ipAddress,port,path);

        TextView aboutText=(TextView) findViewById(R.id.AboutText);
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
}