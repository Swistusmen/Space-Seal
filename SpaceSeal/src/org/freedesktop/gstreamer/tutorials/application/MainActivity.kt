package org.freedesktop.gstreamer.tutorials.application

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button



class MainActivity : Activity() {
    //private external fun nativeGetGStreamerInfo(): String

    // Called when the activity is first created.
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val settingsButton=findViewById<Button>(R.id.SettingsButton)
        val recordButton=findViewById<Button>(R.id.RecordButton)
        val aboutButton=findViewById<Button>(R.id.AboutButton)
        val requestButton=findViewById<Button>(R.id.HTTPButton)
        val requestTranscoding=findViewById<Button>(R.id.RequestTranscoding)
        val sensorData=findViewById<Button>(R.id.SensorData)

        requestTranscoding.setOnClickListener{
            val settingsActivity=Intent(this, Transcoding::class.java)
            println(settingsActivity.action.toString())
            startActivity(settingsActivity)
        }

        settingsButton.setOnClickListener{
            val settingsActivity=Intent(this, Settings::class.java)
            println(settingsActivity.action.toString())
            startActivity(settingsActivity)
        }

        recordButton.setOnClickListener{
            val streammingActivity=Intent(this, Streamming::class.java)
            startActivity(streammingActivity)
        }

        aboutButton.setOnClickListener{
            val aboutActivity= Intent(this, StreamSavedVideo::class.java)
            startActivity(aboutActivity)
        }

        requestButton.setOnClickListener{
            val httpActivity= Intent(this,Request::class.java)
            startActivity(httpActivity)
        }

        sensorData.setOnClickListener{
            val sensorActivity= Intent(this, Sensor::class.java)
            startActivity(sensorActivity)
        }


    }

    companion object {
        init {
            System.loadLibrary("gstreamer_android")
            System.loadLibrary("tutorial-1")
        }
    }
}