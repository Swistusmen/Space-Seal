package org.freedesktop.gstreamer.tutorials.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.freedesktop.gstreamer.GStreamer;

public class About extends AppCompatActivity {
    private native String nativeGetGStreamerInfo();

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        TextView aboutText=(TextView) findViewById(R.id.AboutText);
        aboutText.setText("Welcome to " + nativeGetGStreamerInfo() + " !");
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("tutorial-1");
    }
}