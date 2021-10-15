package org.freedesktop.gstreamer.tutorials.application

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.nio.file.Files.exists
import java.text.SimpleDateFormat
import java.util.*


//in the future this should be migrated into camera2

class Streamming() : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreviewer? = null
    private var mediaRecorder: MediaRecorder?= null
    private var isRecording=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streamming)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1 );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1 );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1 );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1 );

        var button=findViewById<Button>(R.id.RecordButton)


        mCamera = getCameraInstance()
        mPreview = mCamera?.let {
            CameraPreviewer(this, it)
        }

        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }

        button.setOnClickListener{
            if (isRecording) {
                // stop recording and release camera
                mediaRecorder?.stop() // stop the recording
                releaseMediaRecorder() // release the MediaRecorder object
                mCamera?.lock() // take camera access back from MediaRecorder

                // inform the user that recording has stopped
                button.setText("Recording")
                isRecording = false
            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mediaRecorder?.start()
                    // inform the user that recording has started
                    button.setText("Stop")
                    isRecording = true
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder()
                    // inform user
                }
            }
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

    private fun prepareVideoRecorder(): Boolean {
        mediaRecorder = MediaRecorder()

        mCamera?.let { camera ->
            // Step 1: Unlock and set camera to MediaRecorder
            camera?.unlock()

            mediaRecorder?.run {
                setCamera(camera)

                // Step 2: Set sources
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)

                // Step 4: Set output file
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

                var path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath.toString()+"/"

                var dir: File=File(path)
                if(!dir.exists())
                    dir.mkdirs()
                var fileToSave=path+timeStamp+".mp4"

                setOutputFile(fileToSave)
                
                // Step 5: Set the preview output
                setPreviewDisplay(mPreview?.holder?.surface)

                // Step 6: Prepare configured MediaRecorder
                return try {
                    prepare()
                    true
                } catch (e: IllegalStateException) {
                    Log.d(TAG, "IllegalStateException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                } catch (e: IOException) {
                    Log.d(TAG, "IOException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                }
            }

        }
        return false
    }

    override fun onPause() {
        super.onPause()
        releaseMediaRecorder() // if you are using MediaRecorder, release it first
        releaseCamera() // release the camera immediately on pause event
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.reset() // clear recorder configuration
        mediaRecorder?.release() // release the recorder object
        mediaRecorder = null
        mCamera?.lock() // lock camera for later use
    }

    private fun releaseCamera() {
        mCamera?.release() // release the camera for other applications
        mCamera = null
    }
}