package com.consta7.driversapp.presenter.interfaces

import android.app.PendingIntent
import android.content.Context
import android.view.LayoutInflater
import android.widget.TableLayout
import org.json.JSONArray
import java.io.File

interface ListAbstract {

    fun successResult(code: Int, barcode : String)
    fun successParse(code: Int)
    fun progressBarVisible(code: Int)
    fun informationVisible(code : Int)
    fun jsonParser(jsonArray: JSONArray?)
    fun showTable(context: Context, table : TableLayout, lay: LayoutInflater)
    fun showCard(context: Context, needLine : Int, lay: LayoutInflater)
    fun selectedDoneClient(context: Context, flag : Int, index: Int)
    fun successWrite(fileName : String)
    fun pushNotify(context: Context, pendingIntent : PendingIntent)
    //fun createJsonFile(lat : Double, lon : Double)
    fun createJsonFile()

    fun setLat(lat : Double)
    fun setLon(lon : Double)

    //get coordinates for next usage in openStreet maps
    fun getLat() : Array<Double>
    fun getLon() : Array<Double>
    fun getReport() : String
}