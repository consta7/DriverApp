package com.consta7.driversapp.model.interfaces

import android.content.Context

interface JsonFileConstructor {

    fun writeInFile(context: Context, number : String, flag : Int, text : String)
    fun createJsonFile()
}