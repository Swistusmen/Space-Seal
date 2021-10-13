package org.freedesktop.gstreamer.tutorials.application

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


//in the future this should be migrated into camera2

class Streamming() : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreviewer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streamming)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1 );

        var button=findViewById<Button>(R.id.RecordButton)

        if(!CheckCamerHardware(this))
            button.text="Not possible"

        mCamera = getCameraInstance()
        if(mCamera!=null)
            button.setText("Hi")
        mPreview = mCamera?.let {
            CameraPreviewer(this, it)
        }

        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }
    }
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open()
        } catch (e: Exception) {
            null
        }
    }

    private fun CheckCamerHardware(context: Context): Boolean{
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            return true
        return false }

}