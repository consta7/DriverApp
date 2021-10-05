package com.consta7.driversapp.view

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import com.consta7.driversapp.Global
import com.consta7.driversapp.R
import com.consta7.driversapp.foreground.LocationUpdatesBroadcastReceiver
import com.consta7.driversapp.foreground.Utils
import com.consta7.driversapp.presenter.DisplayPresenter
import com.consta7.driversapp.presenter.ListPresenter
import com.consta7.driversapp.view.interfaces.ListViewInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main_list.*
import java.lang.Thread.sleep

class ListViewImp : Global(), ListViewInfo {

    var barcode = LoginViewImp.barcode.toString()
    private var list : ListPresenter = ListPresenter()
    private var display : DisplayPresenter = DisplayPresenter()

    private lateinit var backIntent : PendingIntent
    private lateinit var locationRequest: LocationRequest
    private var fusedLocationClient : FusedLocationProviderClient? = null

    private val pendingIntent: PendingIntent
        @SuppressLint("UnspecifiedImmutableFlag")
        get() {
            val intent = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
            intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        phoneID = list.setPhoneID(phoneID)

        val intentSt = Intent(this, ListViewImp::class.java)
        backIntent = PendingIntent.getActivity(this, 0, intentSt, 0)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createLocationRequest()   //create request with params
        removeLocationUpdates()   //updates is null-object, create empty string

        btnMapReadOnly.setOnClickListener {
            val readOnly = Intent(this, FullRoute::class.java)
            startActivity(readOnly)
        }   //check full route
        btnMap.setOnClickListener {
            requestLocationUpdates()
            onSuccessCreateFile(1)
            display.pushNotify(this, backIntent)
            display.selectedDoneClient(this, 0, list.getIndex())
            display.showTable(this, table, layoutInflater)
        }   //open maps

        successResult()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        list.transferValues(this, 2)
        requestLocationUpdates()
        display.showMapSelector(this, -1)
        display.pushNotify(this, backIntent)
        return super.onOptionsItemSelected(item)
    }

    private fun successResult() {
        list.listPresenter(this)
        display.displayPresenter(this)
        list.progressBarVisible(View.VISIBLE)
        list.informationVisible(View.INVISIBLE)
        list.successResult(code = 1, barcode, this)
    }

    override fun openMaps(map : String, uri : String) {
        when (map) {
            "2-GIS" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("dgis://2gis.ru/routeSearch/rsType/car/to/$uri"))
                intent.setPackage("ru.dublgis.dgismobile")
                startActivity(intent)
            }
            "Google" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$uri"))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
        }
    }

    override fun onSetProgressBarVisible(vis : Int) {
        progressBar2.visibility = vis
        information.visibility = vis
    }

    override fun onSetInformationVisible(vis : Int) {
        scrollTab.visibility = vis
        btnPanel.visibility = vis
    }

    override fun onSuccessResult(result : Int, code : Int) {
        when(result) {
            1 -> list.successParse(1)
            else -> list.successResult(1, barcode, this)
        }
    }

    override fun onSuccessParse(result: Int, code: Int) {
        if (result == 1) {
            display.showTable(this, table, layoutInflater)
            list.progressBarVisible(View.INVISIBLE)
            list.informationVisible(View.VISIBLE)
            title = display.getDriverName()
            requestLocationUpdates()
        }
        else list.successParse(1)
    }

    override fun showNumber(num : String) : String {
        val phoneNo : String = num
        val dial = "tel:$phoneNo"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
        return num
    }

    override fun onSuccessCreateFile(result: Int) {
        if (result == 1) list.transferValues(this, 0)
        else sleep(1 * 1000L)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val back = Intent(this, StartView::class.java)
        startActivity(back)
        finish()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.maxWaitTime = MAX_WAIT_TIME
    }

    private fun requestLocationUpdates() {
        try {
            Log.i(tag, "Starting location updates")
            Utils.setRequestingLocationUpdates(this, true)
            fusedLocationClient!!.requestLocationUpdates(locationRequest, pendingIntent)
        } catch (e: SecurityException) {
            Utils.setRequestingLocationUpdates(this, false)
            e.printStackTrace()
        }
    }

    private fun removeLocationUpdates() {
        Log.i(tag, "Removing location updates")
        Utils.setRequestingLocationUpdates(this, false)
        fusedLocationClient!!.removeLocationUpdates(pendingIntent)
    }

    companion object {
        private var phoneID : Int = 0
        private const val UPDATE_INTERVAL: Long = 10 * 60000L // Every 10 minutes.
        private const val FASTEST_UPDATE_INTERVAL: Long = 6 * 50000L // Every 5 minutes.
        private const val MAX_WAIT_TIME = UPDATE_INTERVAL + FASTEST_UPDATE_INTERVAL // Every 15 minutes.
    }
}