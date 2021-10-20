package org.freedesktop.gstreamer.tutorials.application

import SyncResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Request : AppCompatActivity() {
    var dbHandler: DBHandler= DBHandler(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        var whereAmIHitting="http://"+dbHandler.ipAddress+":"+dbHandler.port

        val enterText=findViewById<EditText>(R.id.EnterMessage)
        val acceptanceButton=findViewById<Button>(R.id.SendMessageButton)
        val displayMessage=findViewById<TextView>(R.id.responseView)
        val requestInfo=findViewById<TextView>(R.id.RequestInfo)

        requestInfo.setText("hiting to "+whereAmIHitting)

        acceptanceButton.setOnClickListener{
            val buffer=enterText.text
            val retrofit= Retrofit.Builder().baseUrl(whereAmIHitting).addConverterFactory(GsonConverterFactory.create()).build()
            val service=retrofit.create(SyncService::class.java)
            val call=service.hello()
            call.enqueue(object: Callback<SyncResponse>{
                override fun onResponse(
                    call: Call<SyncResponse>?,
                    response: Response<SyncResponse>?){
                    if(response!=null)
                        if(response.code()==200){
                            val body=response.body()
                            displayMessage.text=body.welcomeMessage
                        }
                }
                override fun onFailure(call: Call<SyncResponse>?, t: Throwable?) {
                    displayMessage.text="Failure"
                }
            })
            enterText.setText(" ")
        }
    }
}