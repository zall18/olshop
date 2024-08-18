package com.example.olshop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ProcessBuilder.Redirect
import java.net.HttpURLConnection
import java.net.URL

suspend fun getImageFromUrl(connectionString: String): Bitmap? {
    return withContext(Dispatchers.IO){
        try {
            var url = URL(connectionString)
            var connection = url.openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.requestMethod = "GET"
            connection.connect()

            var inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        }catch (e: Exception)
        {
            e.printStackTrace()
            null
        }
    }
}