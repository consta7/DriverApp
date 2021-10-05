package com.consta7.driversapp.model

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.consta7.driversapp.data.DriverReport
import com.consta7.driversapp.model.interfaces.JsonFileConstructor
import com.google.gson.Gson
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class JsonFileConstructorImp : JsonFileConstructor {

    private val data : DataBaseImp = DataBaseImp()

    override fun writeInFile(
        context: Context,
        number : String,
        flag : Int,
        text : String) {

        var docID = number
        if (number.isEmpty()) docID = phoneID.toString()
        val fileName : String = when (flag) {
            0 -> "${geoLatBackground}-${geoLonBackground}_#${docID}_${getNowTime()}"
            1 -> "logRouteInfo_#${docID}_${getNowTime()}"
            else -> "intermediateLog_#${docID}_${getNowTime()}"
        }
        try {
            val fileOutput : FileOutputStream = context.openFileOutput(
                "$fileName.txt", AppCompatActivity.MODE_PRIVATE
            )
            fileOutput.write(text.toByteArray())
            fileOutput.close()
            data.requestData(fileName)
        } catch (e : FileNotFoundException) { e.printStackTrace() }
        catch (e : IOException) { e.printStackTrace() }
        interimLog = ""
    }

    override fun createJsonFile() {
        if (taskNumber != "") {
            val reportFile = DriverReport(
                driverName = driverName,
                taskNumber = taskNumber,
                geoLat = geoLat.toString(),
                geoLon = geoLon.toString(),
                idDocument = numbersCl[selectIndex],
                clients = clients[selectIndex],
                addressCl = addressCl[selectIndex],
                notes = notes[selectIndex],
            )
            val gson = Gson()
            report = gson.toJson(reportFile)
        }
    }

    fun setPhoneID(id : Int) : Int {
        if (assignmentID) {
            phoneID = id + Random.nextInt(1, 100)
            assignmentID = true
        }
        return phoneID
    }

    fun writeFile(context: Context, taskNumber : String) {
        writeInFile(context, taskNumber, 2, interimLog)
        interimLog = ""
    }

    fun addLogInfo(newLog : String) {
        logReport += "\n ${newLog};${phoneID};${getNowTime()}"
        if (interimLog == "") {
            interimLog += "${clients[selectIndex]} : done; \n"
        }
        interimLog += "\n ${newLog};${phoneID};${getNowTime()}"
    }

    fun getReport(flag : Int) : String {
        return when (flag) {
            0 -> report
            1 -> interimLog
            else -> logReport
        }
    }

    fun setGeoLat(newLat : Double) {
        geoLatBackground = newLat
    }

    fun setGeoLon(newLon : Double) {
        geoLonBackground = newLon
    }

    fun setLat(lat : Double) {
        geoLat = lat
    }

    fun setLon(lon : Double) {
        geoLon = lon
    }

    fun setSelectIndex(value : Int) {
        selectIndex = value
    }

    fun getIndex() = selectIndex

    private fun getNowTime() : String {
        val sdf = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.US)
        val currentDate = sdf.format(Date()).substring(0, 8)
        val currentTime = timeStrToSeconds(sdf.format(Date()).substring(9, 17))
        val needDate = currentDate.substring(6,8) + "." + currentDate.substring(4,6) + "." + currentDate.substring(2,4)
        val hours = ((currentTime / 60) / 60)
        val minutes = (currentTime / 60) - (hours * 60)
        val nowTime = if (minutes < 10) "${hours + 4}:0$minutes" else "${hours + 4}:$minutes"
        return "${nowTime}_${needDate}"
    }

    private fun timeStrToSeconds(str: String): Int {
        val parts = str.split(":")
        var result = 0
        for (part in parts) {
            val number = part.toInt()
            result = result * 60 + number
        }
        return result
    }

    companion object {
        var driverName = ""
        var taskNumber = ""
        var clients : List<String> = listOf()
        var addressCl : List<String> = listOf()
        var numbersCl : List<String> = listOf()
        var notes : List<String> = listOf()
        var report = ""
        var logReport = ""
        var interimLog = ""
        var geoLat = 0.0
        var geoLon = 0.0
        var selectIndex = 0

        private var geoLatBackground : Double = 0.0
        private var geoLonBackground : Double = 0.0
        private var assignmentID : Boolean = false
        var phoneID : Int = 0
    }
}