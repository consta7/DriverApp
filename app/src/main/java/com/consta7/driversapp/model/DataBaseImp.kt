package com.consta7.driversapp.model

import android.annotation.SuppressLint
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import kotlinx.coroutines.*
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

class DataBaseImp : DataBase {

    companion object {
        var jsonArray : JSONArray? = null
        var localJson : JSONObject? = null
        var barCode = ""
    }
    var code = 0

    override fun getJSON() : JSONArray? = jsonArray

    override fun requestBarcode(barcode: String) : Int {
        if (barcode.length < 12) return 0
        val idd: String = "99990" + barcode.substring(2, 4) + "00" + barcode.substring(4, 12)
        val url = "http://dev.com/localPut.json"
        val asyncHttpClient = AsyncHttpClient()
        val requestParams = RequestParams()
        asyncHttpClient.apply {
            responseTimeout = 9 * 100000       //such a large amount of time
            connectTimeout = 9 * 100000        //because getting data from 1S is too long
            setTimeout(9 * 100000)             //and because quality connect very different in route
        }
        requestParams.put("order_id", idd)
        asyncHttpClient.post(url, requestParams, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                response: JSONObject) {
                super.onSuccess(statusCode, headers, response)
                localJson = response
                jsonArray = localJson?.getJSONArray("data")
                barCode = barcode
                code = 1
                Log.i("dataBase", "$response")
            }
            override fun onFailure(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                throwable: Throwable?,
                response: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, response)
                if (response != null) {
                    try {
                        localJson = response
                    } catch (e: Exception) {
                        Log.i("dataBase", "--->>onFailure:" + throwable.toString())
                    }
                }
            }
        })
        return code
    }

    //private val client = OkHttpClient()

    override fun requestData(file : String) {
        Log.d("dataBase", "ftp on")
        GlobalScope.launch {
            connectFtp(file)
        }

        /*val file = File("files/dataDriver.txt")
        Log.d("dataBase", "okhttp start")

        val request = Request.Builder()
            .url("")
            .post(file.asRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            Log.d("dataBase", response.body!!.string())
        }*/
    }

    @SuppressLint("SdCardPath")
    private suspend fun connectFtp(fileName : String): Boolean {
        val yourFilePath: String = ("/data/user/0/com.consta7.driversapp/files/${fileName}.txt")
        Log.d("dataBase", "filepath $yourFilePath")

        val mFTPClient = FTPClient()
        // connecting to the host
        mFTPClient.autodetectUTF8 = true
        mFTPClient.connect("hostname",0)
        mFTPClient.login("username","password")
        mFTPClient.setFileType(FTP.BINARY_FILE_TYPE)
        mFTPClient.enterLocalPassiveMode()

        val pathname = "/drivers/data/"
        mFTPClient.changeWorkingDirectory(pathname)
        mFTPClient.printWorkingDirectory()
        Log.d("ftp-client", mFTPClient.printWorkingDirectory())
        val `in` = FileInputStream(File(yourFilePath))
        val result: Boolean = mFTPClient.storeFile("/${fileName}.txt", `in`)
        if (result) Log.d("ftp-response", "upload success")
        `in`.close()

        mFTPClient.logout()
        mFTPClient.disconnect()
        delay(1 * 1000L)
        return true
    }
}
