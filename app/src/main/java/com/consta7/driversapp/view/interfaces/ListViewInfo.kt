package com.consta7.driversapp.view.interfaces

interface ListViewInfo {

    fun onSuccessResult(result : Int, code : Int)
    fun onSuccessParse(result: Int, code: Int)
    fun onSuccessCreateFile(result: Int)
    fun onSetProgressBarVisible(vis : Int)
    fun onSetInformationVisible(vis : Int)
    fun openMaps(map : String, uri : String)
    fun showNumber(num : String) : String
}