package org.freedesktop.gstreamer.tutorials.application

import retrofit2.http.GET
import retrofit2.http.Query
import SyncResponse
import retrofit2.Call

interface SyncService{
    @GET("/welcome")
    fun hello():Call<SyncResponse>
}