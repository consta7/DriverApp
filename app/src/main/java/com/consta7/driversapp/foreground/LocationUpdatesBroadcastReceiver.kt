package com.consta7.driversapp.foreground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationResult
import com.consta7.driversapp.view.ListViewImp

class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    private val listFile : ListViewImp = ListViewImp()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (ACTION_PROCESS_UPDATES == action) {
            try {
                val result = LocationResult.extractResult(intent)
                val locations = result.locations
                Utils.setLocationUpdatesResult(context, locations)
                listFile.onSuccessCreateFile(1)
                Log.i(TAG, Utils.getLocationUpdatesResult(context)!!)
            } catch (e : Exception) {
                Log.i("broadcast error", "Can't get location or fatal error in library")
            }
        }
    }

    companion object {
        private const val TAG = "LUBroadcastReceiver"
        const val ACTION_PROCESS_UPDATES =
            "com.consta7.driversapp.action.PROCESS_UPDATES"
    }
}
