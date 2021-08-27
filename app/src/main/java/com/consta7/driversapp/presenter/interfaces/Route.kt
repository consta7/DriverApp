package com.consta7.driversapp.presenter.interfaces

import android.webkit.WebView

interface Route {
    val site : String
    val settingMap: String
        get() = "&b=0&c=0&k1=ru&k2=km"
    //hide menu
    val script : String
        get() = "javascript:(function() { " +
                "document.getElementsByClassName('ors-left')[0].style.display='none';" +
                "})()"
    fun getValues()
    fun urlConstruct(): String
    fun showWebView(web : WebView)
}