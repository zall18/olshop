package com.example.olshop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

suspend fun fileRequest( connectionString: String, dataName: MutableList<String>, dataValue: MutableList<String>, columnfile: String, file: File, token : String?): Result<String> {
    return withContext(Dispatchers.IO)
    {
        try {

            var url = URL(connectionString)
            var redirect = false
            var response = StringBuilder()
            var LINE_FEED = "\r\n"
            var boundary = "Boundary- " + System.currentTimeMillis()
            var maxBufferedReader = 1024 * 1024

            do {

                var connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary = $boundary")
                connection.setRequestProperty("Accept", "application/json")
                if(token != null)
                {
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }
                connection.doOutput = true

                var outputStream = connection.outputStream
                var writer = PrintWriter(OutputStreamWriter(outputStream, "UTF-8"), true)



                var responsecode = connection.responseCode

                if(responsecode == HttpURLConnection.HTTP_MOVED_TEMP || responsecode == HttpURLConnection.HTTP_MOVED_PERM)
                {
                    redirect = true
                    url = URL(connection.getHeaderField("Location"))
                }else{
                    redirect =  false

                    var inputStream = if(responsecode in 200 .. 299)
                    {
                        connection.inputStream
                    }else{
                        connection.errorStream
                    }

                    var line: String?
                    var reader = BufferedReader(InputStreamReader(inputStream))
                    while (reader.readLine().also { line = it } != null){
                        response.append(line)
                    }

                    if(responsecode !in 200 .. 299){
                        return@withContext Result.failure(Exception("Failed with  response code $responsecode \n $response"))
                    }
                }
                connection.disconnect()
            }while (redirect)

            Result.success(response.toString())

        }catch (e: Exception){
            Result.failure(e)
        }
    }
}