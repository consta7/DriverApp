package com.consta7.driversapp.presenter.interfaces

import android.content.Context
import android.os.Handler
import android.os.Looper

interface ListAbstract {
    val handler : Handler
        get() = Handler(Looper.getMainLooper())

    fun successResult(code: Int, barcode : String, context: Context)
    fun successParse(code: Int)
    fun progressBarVisible(code: Int)
    fun informationVisible(code : Int)
    fun successWrite(fileName : String)

}