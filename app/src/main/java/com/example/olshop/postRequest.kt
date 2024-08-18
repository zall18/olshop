package com.example.olshop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

suspend fun postRequest( connectionString: String, jsonObject: JSONObject, token : String?): Result<String> {
    return withContext(Dispatchers.IO)
    {
        try {

            var url = URL(connectionString)
            var redirect = false
            var response = StringBuilder()

            do {

                var connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                if(token != null)
                {
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }
                connection.doOutput = true

                var outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonObject.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

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