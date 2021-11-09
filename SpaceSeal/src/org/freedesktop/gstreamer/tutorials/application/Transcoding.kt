package org.freedesktop.gstreamer.tutorials.application

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.arthenica.mobileffmpeg.Config
import android.provider.OpenableColumns
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS

class Transcoding : AppCompatActivity() {
    val transcoder:Transcoder= Transcoder()
    val dbHandler: DBHandler= DBHandler(this)
    lateinit var choosenVideo:TextView
    var fileToTranscode=""
    var videoCodec=VideoCodecs.H264

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transcoding)

        val chooseFileButton=findViewById<Button>(R.id.chooseFileToTranscode)
        val av1Button=findViewById<Button>(R.id.AV1Format)
        val h264Button=findViewById<Button>(R.id.H264Format)
        val requestTranscode=findViewById<Button>(R.id.TranscodeVideoButton)
        val infoLabel=findViewById<TextView>(R.id.informationTranscodig)
        choosenVideo=findViewById<TextView>(R.id.fileToTranscode)
        fileToTranscode=dbHandler.recentlyRecordedVideoPath
        choosenVideo.text=dbHandler.videoName

        chooseFileButton.setOnClickListener {
            var fileIntent = Intent(Intent.ACTION_GET_CONTENT)
            fileIntent.setType("video/*")
            startActivityForResult(fileIntent, 0)
        }

        av1Button.setOnClickListener{ videoCodec=VideoCodecs.AV1}
        h264Button.setOnClickListener{ videoCodec=VideoCodecs.H264}

        requestTranscode.setOnClickListener{
            infoLabel.text="Transcoding"
            var result=transcoder.transcodeVideo(fileToTranscode,videoCodec)
            var myString=""
            if(result.first==RETURN_CODE_SUCCESS)
                myString="Success "
            else
                myString="Failure "
            myString+=", elapse time: "+result.second;
            infoLabel.text=myString;
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri= data?.data
        choosenVideo.text=uri.toString()

        val c = contentResolver.query(uri!!, null, null, null, null)
        c!!.moveToFirst()
        fileToTranscode=c!!.getString(c!!.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        choosenVideo.text=fileToTranscode
        fileToTranscode=dbHandler.videoPath+"/"+fileToTranscode
    }
}