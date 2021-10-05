package com.consta7.driversapp.model.interfaces

import org.json.JSONArray

interface DataBase {
    /** DataBase.kt
     * This class need for get information about task for driver!
     * */
    //request for API to get data driver task
    fun requestBarcode(barcode: String) : Int
    //request for API to sent info about driver
    fun requestData(file : String)
    //get json object "data" for next usage
    fun getJSON() : JSONArray?
}