package org.freedesktop.gstreamer.tutorials.application

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Settings : AppCompatActivity() {
    var dbHandler: DBHandler= DBHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ssettings)

        val saveSettingsButton=findViewById<Button>(R.id.SaveSettingsButton)
        val IpAddressText= findViewById<EditText>(R.id.addressIP)
        val portText=findViewById<EditText>(R.id.PortText)
        val pathText=findViewById<EditText>(R.id.adrdressPathText)


        //pathText.setText(path)
        //portText.setText(dbHandler.port)
        //IpAddressText.setText(dbHandler.ipAddress)

        saveSettingsButton.setOnClickListener{
            dbHandler.updateSettings(IpAddressText.text.toString(),portText.text.toString(),pathText.text.toString())
        }
    }
}