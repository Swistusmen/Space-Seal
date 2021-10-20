package org.freedesktop.gstreamer.tutorials.application

import SyncResponse
import retrofit2.Call

class SyncRepository constructor(private val server: SyncService){

    suspend fun checkSynchro(): Call<SyncResponse> {
        return  server.hello();
    }

}
