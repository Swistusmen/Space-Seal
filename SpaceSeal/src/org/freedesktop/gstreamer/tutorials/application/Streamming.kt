package org.freedesktop.gstreamer.tutorials.application

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.nio.file.Files.exists
import java.text.SimpleDateFormat
import java.util.*
import android.view.WindowManager





//in the future this should be migrated into camera2

//TODO
/*
0. Refactor the code
1.Make camera scan video at the portrait layout DONE
2.Button update- set color, etc DONE
3.Add new button- it will scan and send a video using gstreamer DONE
4.Make an RTSP Server work -3
5.Improve concents
6.Improve video quality
7.Add storing lasttly recordered vieo within DB -1
8.Investigate how to stream already recordered video to the server -2
*/


class Streamming() : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreviewer? = null
    private var mediaRecorder: MediaRecorder?= null
    private var isRecording=false
    private var isReadyToStream=false
    private var saveLocation: String=""
    private var dbHandler: DBHandler= DBHandler(this)

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

        var recordButton=findViewById<Button>(R.id.RecordButton)
        var streamButton=findViewById<Button>(R.id.StartStreammingButton)

        mCamera = getCameraInstance()
        mCamera?.setDisplayOrientation(90)
        mPreview = mCamera?.let {
            CameraPreviewer(this, it)
        }
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }

        recordButton.setOnClickListener{
            if (isRecording) {
                // stop recording and release camera
                mediaRecorder?.stop() // stop the recording
                releaseMediaRecorder() // release the MediaRecorder object
                mCamera?.lock() // take camera access back from MediaRecorder
                dbHandler.updateVideo(saveLocation)
                // inform the user that recording has stopped
                recordButton.setText("Record")
                recordButton.setBackgroundColor(Color.parseColor("#66DE93"))
                streamButton.setBackgroundColor(Color.parseColor("#66DE93"))
                isReadyToStream=true
                isRecording = false
            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mediaRecorder?.start()
                    // inform the user that recording has started
                    recordButton.setText("Stop recording")
                    recordButton.setBackgroundColor(Color.parseColor("#FF616D"))
                    isRecording = true
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder()
                    // inform user
                }
            }
        }

        streamButton.setOnClickListener{
            if(isReadyToStream){
                //TODO implement some actions here
            }
        }
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

    private fun getFileToSaveVideo(): String{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath.toString()+"/"
        var dir: File=File(path)
        if(!dir.exists())
            dir.mkdirs()
        var fileToSave=path+timeStamp+".mp4"
        return fileToSave
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

    fun getDeviceOrientation(context: Context): Int {
        var degrees = 0
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        return degrees
    }

    fun getPreviewOrientation(context: Context?, cameraId: Int): Int {
        var temp = 0
        var previewOrientation = 0
        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        val deviceOrientation = getDeviceOrientation(context!!)
        temp = cameraInfo.orientation - deviceOrientation + 360
        previewOrientation = temp % 360
        return previewOrientation
    }

    private fun getCamaraBackId():Int{

        val numberOfCameras = Camera.getNumberOfCameras();
        val cameraInfo = CameraInfo();
        for (i in 0..numberOfCameras) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return -1 // Device do not have back camera !!!!???
    }

    private fun prepareVideoRecorder(): Boolean {
        mediaRecorder = MediaRecorder()
        val rotation = getPreviewOrientation(this, getCamaraBackId())
        Log.d(TAG, "Rotation ${rotation}")

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

                saveLocation=getFileToSaveVideo()
                setOutputFile(saveLocation)

                // Step 5: Set the preview output
                setOrientationHint(rotation)

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
}