package org.freedesktop.gstreamer.tutorials.application

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView


class MainActivity : Activity() {
    private external fun nativeGetGStreamerInfo(): String

    // Called when the activity is first created.
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val settingsButton=findViewById<Button>(R.id.SettingsButton)
        val streamButton=findViewById<Button>(R.id.StreamButton)
        val aboutButton=findViewById<Button>(R.id.AboutButton)

        settingsButton.setOnClickListener{
            val settingsActivity=Intent(this, Settings::class.java)
            startActivity(settingsActivity)
        }

        streamButton.setOnClickListener{
            val streamActivity=Intent(this, Streamming::class.java)
            startActivity(streamActivity)
        }

        aboutButton.setOnClickListener{

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

        setContentView(R.layout.main)
        val tv = findViewById<View>(R.id.textview_info) as TextView
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