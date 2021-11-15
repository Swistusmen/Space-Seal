package org.freedesktop.gstreamer.tutorials.application

import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.lang.Thread.sleep

class Sensor : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    //every 1 second
    var handler:Handler=Handler()

    var displayData: TextView? =null

    var myList= ArrayList<Array<Float>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        val grabData=findViewById<Button>(R.id.GrabData)
        displayData=findViewById<TextView>(R.id.sensorDataDisplayed)

        var doIGrabbingData:Boolean=false

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        var myFun= object : Runnable {
            override fun run() {
                updateOrientationAngles()
                handler.postDelayed(this,1000)
            }
        }

        grabData.setOnClickListener{
            //updateOrientationAngles()
            doIGrabbingData=!doIGrabbingData
            if(doIGrabbingData){
                handler.post(myFun)
            }else{
                handler.removeCallbacks(myFun)
            }
        }



    }

    override fun onResume() {
        super.onResume()

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
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

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        Log.d(ContentValues.TAG,"measurement")
        Log.d(ContentValues.TAG, "Rotation: ${rotationMatrix[0]} ${rotationMatrix[1]} ${rotationMatrix[2]} ${rotationMatrix[3]} ${rotationMatrix[4]} ${rotationMatrix[5]} ${rotationMatrix[6]} ${rotationMatrix[7]} ${rotationMatrix[8]}")
        Log.d(ContentValues.TAG, "accelerometerReading: ${accelerometerReading[0]} ${accelerometerReading[1]} ${accelerometerReading[2]}")
        Log.d(ContentValues.TAG, "magnetometerReading: ${magnetometerReading[0]} ${magnetometerReading[1]} ${magnetometerReading[2]}")
        Log.d(ContentValues.TAG, "orientation: ${orientationAngles[0]} ${orientationAngles[1]} ${orientationAngles[2]}")

        // "rotationMatrix" now has up-to-date information.

        val back=SensorManager.getOrientation(rotationMatrix, orientationAngles)
        Log.d(ContentValues.TAG, "Back: ${back[0]} ${back[1]} ${back[2]}")
        displayData?.text =back.toString()
        myList.add(arrayOf(back[0],back[1],back[2]))
        // "orientationAngles" now has up-to-date information.
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
        Log.d(ContentValues.TAG,"measurement")
    }

    fun everySecond(){
        updateOrientationAngles()
    }
}