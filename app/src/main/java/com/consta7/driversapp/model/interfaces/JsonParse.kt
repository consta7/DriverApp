package com.consta7.driversapp.model.interfaces

import org.json.JSONArray

interface JsonParse {

    fun setJson(jsonArray: JSONArray?)
    fun setDoneClient(index : Int, value : Int)

    fun jsonParser(jsonArray: JSONArray?) : Int

    fun getParseValue(tag : String) : String
    fun getArrayParseValue(tag : String, index : Int) : String
    fun getListParseValue(tag : String, index: Int) : String
    fun getParseNumber(tag : String) : String
}