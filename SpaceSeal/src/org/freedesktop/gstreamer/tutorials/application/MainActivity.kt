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


        /*
        try {
            GStreamer.init(this)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        */

        //setContentView(R.layout.main)
        //val tv = findViewById<View>(R.id.textview_info) as TextView
        /*
        tv.text = "Welcome to " + nativeGetGStreamerInfo() + " !"
        */

    }

    companion object {
        init {
            System.loadLibrary("gstreamer_android")
            System.loadLibrary("tutorial-1")
        }
    }
}