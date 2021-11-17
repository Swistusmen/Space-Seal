package org.freedesktop.gstreamer.tutorials.application

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.*
import android.hardware.Camera.CameraInfo
import android.hardware.Sensor
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
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
import android.widget.Switch

class Streamming() : AppCompatActivity(), SensorEventListener {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreviewer? = null
    private var mediaRecorder: MediaRecorder?= null
    private var isRecording=false
    private var isReadyToStream=false
    private var saveLocation: String=""
    private var coordinatesFileLocation: String=""
    private var dbHandler: DBHandler= DBHandler(this)
    private var recordOrientation:Boolean=false
    /*stuffs for sensors*/
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    var handler: Handler = Handler()
    /*ends here*/
    var coordinates=ArrayList<Array<Float>>()

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
        var orientationSwitcher=findViewById<Switch>(R.id.OrientationRecordingSwitcher)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mCamera = getCameraInstance()
        mCamera?.setDisplayOrientation(90)
        mPreview = mCamera?.let {
            CameraPreviewer(this, it)
        }
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }

        orientationSwitcher.setOnClickListener{
            recordOrientation=orientationSwitcher.isChecked()
        }

        var myFun= object : Runnable {
            override fun run() {
                updateOrientationAngles()
                handler.postDelayed(this,1000)
            }
        }

        recordButton.setOnClickListener{
            if (isRecording) {
                // stop recording and release camera
                mediaRecorder?.stop() // stop the recording
                if(recordOrientation){
                    handler.removeCallbacks(myFun)
                }
                releaseMediaRecorder() // release the MediaRecorder object
                mCamera?.lock() // take camera access back from MediaRecorder
                dbHandler.updateVideo(saveLocation)
                // inform the user that recording has stopped
                recordButton.setText("Record")
                recordButton.setBackgroundColor(Color.parseColor("#66DE93"))

                saveCoordinates()

                isReadyToStream=true
                isRecording = false
            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mediaRecorder?.start()
                    if(recordOrientation){
                        handler.post(myFun)
                    }
                    // inform the user that recording has started
                    recordButton.setText("Stop recording")
                    recordButton.setBackgroundColor(Color.parseColor("#FF616D"))
                    isRecording = true
                } else {
                    releaseMediaRecorder()
                }
            }
        }


    }

    private fun saveCoordinates() {
        var destination=File(coordinatesFileLocation)
        var noCoordinates=coordinates.size-1
        var text:String=""

        for(i in 0..noCoordinates){
            var tab=coordinates[i]
            text+=tab[0].toString()+" "+tab[1]+" "+tab[2]+"\n"
        }
        destination.writeText(text)
        coordinates.clear()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }


    override fun onPause() {
        super.onPause()
        releaseMediaRecorder() // if you are using MediaRecorder, release it first
        releaseCamera() // release the camera immediately on pause event
        sensorManager.unregisterListener(this)
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
        var fileToSave=path+timeStamp
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
                coordinatesFileLocation=saveLocation+".txt"
                saveLocation+=".mp4"
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

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!=null)
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(ContentValues.TAG,"Nothing")
    }

    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        val back=SensorManager.getOrientation(rotationMatrix, orientationAngles)
        Log.d(ContentValues.TAG, "Back: ${back[0]} ${back[1]} ${back[2]}")
        coordinates.add(arrayOf(back[0],back[1],back[2]))
    }
}