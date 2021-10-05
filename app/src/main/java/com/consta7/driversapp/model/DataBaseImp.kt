package com.consta7.driversapp.model

import android.annotation.SuppressLint
import android.util.Log
import com.consta7.driversapp.model.interfaces.DataBase
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
        var isParse = true
        var jsonArray : JSONArray? = null
        var localJson : JSONObject? = null
        var barCode = ""
    }
    var code = 0
    private val parseImp : JsonParseImp = JsonParseImp()

    override fun getJSON() : JSONArray? = jsonArray

    override fun requestBarcode(barcode: String) : Int {
        //check value
        if (barcode.length < 12) return 0

        val idd: String = "99990" + barcode.substring(2, 4) + "00" + barcode.substring(4, 12)
        val url = "http://dev.com/localPut.json"
        val asyncHttpClient = AsyncHttpClient()
        val requestParams = RequestParams()
        Log.i("dataBase", "$barcode; $idd")
        requestParams.put("order_id", idd)
        asyncHttpClient.post(url, requestParams, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                response: JSONObject) {
                super.onSuccess(statusCode, headers, response)
                localJson = response
                if (localJson?.getString("data")?.substring(0, 9) == "Exception") {
                    code = 0
                    return
                }
                jsonArray = localJson?.getJSONArray("data")
                if (isParse) {
                    parseImp.setJson(jsonArray)
                    isParse = false
                }
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

    override fun requestData(file : String) {
        Log.d("dataBase", "ftp on")
        GlobalScope.launch {
            connectFtp(file)
        }
    }

    @SuppressLint("SdCardPath")
    private suspend fun connectFtp(fileName : String): Boolean {
        val yourFilePath: String = ("/data/user/0/com.consta7.driversapp/files/${fileName}.txt")
        Log.d("dataBase", "filepath $yourFilePath")

        delay(1 * 1000L)
        return true
    }
}
