package com.consta7.driversapp.presenter

import android.webkit.WebView
import android.webkit.WebViewClient
import com.consta7.driversapp.model.JsonParseImp
import com.consta7.driversapp.presenter.interfaces.Route

class RoutePresenter : Route {

    private val json : JsonParseImp = JsonParseImp()
    private var lat : Array<Double> = arrayOf()
    private var lon : Array<Double> = arrayOf()
    private var fstCor : Double = 0.0
    private var sndCor : Double = 0.0
    private var count : Int = 0

    override fun getValues() {
        //get count points
        count = json.getParseNumber("countClients").toInt()
        //get start point
        fstCor = json.getParseNumber("startLat").toDouble()
        sndCor = json.getParseNumber("startLon").toDouble()
        //get clients coordinates
        lat = json.getCoordinatesArray(0)
        lon = json.getCoordinatesArray(1)
    }

    override val site: String
        get() = "https://classic-maps.openrouteservice.org/directions?" +
                "n1=55.565922&n2=49.108887&n3=5&a=$fstCor,$sndCor,"

    override fun urlConstruct(): String {
        var coordinates = ""
        for (i in 0 until count) {
            coordinates += ("${lat[i].toString().trim()},${lon[i].toString().trim()},")
        }
        return ("$site$coordinates$settingMap") //get finished url-address
    }

    override fun showWebView(web: WebView) {
        getValues()
        val webSettings = web.settings
        true.also { webSettings.javaScriptEnabled = it }
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                web.loadUrl(script)
            }
        }
        web.loadUrl(urlConstruct())
    }



}