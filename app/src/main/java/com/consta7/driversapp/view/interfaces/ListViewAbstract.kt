package com.consta7.driversapp.view.interfaces

import android.content.Context

interface ListViewAbstract {

    fun onSuccessResult(result : Int, code : Int)
    fun onSuccessParse(result: Int, code: Int)
    fun onSuccessCreateFile(context: Context, result: Int)
    fun onSetProgressBarVisible(vis : Int)
    fun onSetInformationVisible(vis : Int)
    fun openMaps(map : String, uri : String)
    fun showNumber(num : String) : String
    fun onClickLayout()
    fun writeInFile(context: Context, number : String, dateTime : String, flag : Int)
}