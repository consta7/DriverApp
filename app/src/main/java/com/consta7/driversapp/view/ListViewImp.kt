package com.consta7.driversapp.view

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.consta7.driversapp.Global
import com.consta7.driversapp.LocationUpdatesBroadcastReceiver
import com.consta7.driversapp.R
import com.consta7.driversapp.Utils
import com.consta7.driversapp.presenter.ListPresenter
import com.consta7.driversapp.view.interfaces.ListViewAbstract
import kotlinx.android.synthetic.main.activity_main_list.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*

class ListViewImp : Global(), ListViewAbstract {
    var barcode = LoginViewImp.barcode.toString()
    private var list : ListPresenter = ListPresenter()
    private lateinit var backIntent : PendingIntent
    private var currentTime = Date().time

    private lateinit var locationRequest: LocationRequest
    private var fusedLocationClient : FusedLocationProviderClient? = null

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        phoneID = list.setPhoneID(phoneID)

        val intentSt = Intent(this, ListViewImp::class.java)
        backIntent = PendingIntent.getActivity(this, 0, intentSt, 0)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createLocationRequest()
        removeLocationUpdates()

        btnMapReadOnly.setOnClickListener {
            val readOnly = Intent(this, FullRoute::class.java)
            startActivity(readOnly)
        }   //check full route
        btnMap.setOnClickListener {
            requestLocationUpdates()
            onSuccessCreateFile(this,1)
            list.pushNotify(this, backIntent)
            list.selectedDoneClient(this, 0, list.getIndex())
            list.showTable(this, table, layoutInflater)
        }   //open maps

        successResult()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       // onClickLayout()
       // if (list.confirmResult(0, true)) {
        writeInFile(this, list.getNumberTask(), currentTime.toString(), 1)
        requestLocationUpdates()
        list.showMapSelector(this, -1)
        list.pushNotify(this, backIntent)
      //  } else Toast.makeText(this, "Ошибка получения разрешения!", Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    private fun successResult() {
        list.listPresenter(this)
        list.progressBarVisible(View.VISIBLE)
        list.informationVisible(View.INVISIBLE)
        list.successResult(code = 1, barcode)
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
            else -> list.successResult(1, barcode)
        }
    }

    override fun onSuccessParse(result: Int, code: Int) {
        if (result == 1) {
            list.showTable(this, table, layoutInflater)
            list.progressBarVisible(View.INVISIBLE)
            list.informationVisible(View.VISIBLE)
            title = list.getDriverName()
        }
        else list.successParse(1)
    }

    override fun showNumber(num : String) : String {
        val phoneNo : String = num
        val dial = "tel:$phoneNo"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
        return num
    }

    override fun onClickLayout() {
        //TODO:("Not yet implemented")
     //   supportFragmentManager.beginTransaction().add(R.id.frame, list.openFragment()).commit()
    }

    override fun onSuccessCreateFile(context: Context, result: Int) {
        if (result == 1) {
            list.createJsonFile()
            Log.w("consta7///", "successCreate")
            writeInFile(context, list.getNumberTask(), currentTime.toString(), 0)
        }
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

    private val pendingIntent: PendingIntent
        @SuppressLint("UnspecifiedImmutableFlag")
        get() {
            val intent = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
            intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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

    override fun writeInFile(context: Context, number : String, dateTime : String, flag : Int) {
        val fileName : String
        var docID = number
        if (number.isEmpty()) docID = phoneID.toString()
        fileName = when (flag) {
            0 -> "${geoLatBackground}-${geoLonBackground}_#${docID}_${dateTime}"
            1 -> "logRouteInfo_#${docID}_${dateTime}"
            else -> "intermediateLog_#${docID}_${dateTime}"
        }
        when (flag) {
            0 -> {
                val json = ListPresenter.report
                try {
                    val fileOutput : FileOutputStream = context.openFileOutput(
                        "$fileName.txt"
                        , MODE_PRIVATE)
                    fileOutput.write(json.toByteArray())
                    fileOutput.close()
                    list.successWrite(fileName)
                } catch (e : FileNotFoundException) { e.printStackTrace() }
                catch (e : IOException) { e.printStackTrace() }
            }
            1 -> {
                val logs = ListPresenter.logReport
                try {
                    val fileOutput : FileOutputStream = context.openFileOutput(
                        "$fileName.txt"
                        , MODE_PRIVATE)
                    fileOutput.write(logs.toByteArray())
                    fileOutput.close()
                    list.successWrite(fileName)
                } catch (e : FileNotFoundException) { e.printStackTrace() }
                catch (e : IOException) { e.printStackTrace() }
            }
            else -> {
                val logInterim = ListPresenter.interimLog
                try {
                    val fileOutput : FileOutputStream = context.openFileOutput(
                        "$fileName.txt"
                        , MODE_PRIVATE)
                    fileOutput.write(logInterim.toByteArray())
                    fileOutput.close()
                    list.successWrite(fileName)
                } catch (e : FileNotFoundException) { e.printStackTrace() }
                catch (e : IOException) { e.printStackTrace() }
            }
        }
    }

    fun setGeoLat(newLat : Double) {
        geoLatBackground = newLat
    }

    fun setGeoLon(newLon : Double) {
        geoLonBackground = newLon
    }

    companion object {
        private var geoLatBackground : Double = 0.0
        private var geoLonBackground : Double = 0.0
        private var phoneID : Int = 0
        private const val UPDATE_INTERVAL: Long = 60000 * 10 // Every 10 minutes.
        private const val FASTEST_UPDATE_INTERVAL: Long = 60000 * 5 // Every 5 minutes.
        private const val MAX_WAIT_TIME = UPDATE_INTERVAL + FASTEST_UPDATE_INTERVAL // Every 15 minutes.
    }
}