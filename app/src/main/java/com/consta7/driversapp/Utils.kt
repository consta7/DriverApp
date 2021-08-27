package com.consta7.driversapp

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.consta7.driversapp.presenter.ListPresenter
import com.consta7.driversapp.view.ListViewImp
import java.text.DateFormat
import java.util.*

internal object Utils {

    private const val KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested"
    private const val KEY_LOCATION_UPDATES_RESULT = "location-update-result"
    private val list : ListPresenter = ListPresenter()
    private val viewList : ListViewImp = ListViewImp()

    fun setRequestingLocationUpdates(context: Context?, value: Boolean) {
        getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
            .apply()
    }

    private fun getLocationResultTitle(context: Context, locations: List<Location>): String {
        val numLocationsReported = context.resources.getQuantityString(
            R.plurals.num_locations_reported, locations.size, locations.size
        )
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(Date())
    }

    private fun getLocationResultText(context: Context, locations: List<Location>): String {
        if (locations.isEmpty()) {
            return context.getString(R.string.unknown_location)
        }
        val sb = StringBuilder()
        for (location in locations) {
            sb.append("(")
            sb.append(location.latitude)
            sb.append(", ")
            sb.append(location.longitude)
            sb.append(")")
            sb.append("\n")

           setValues(location.latitude, location.longitude)
        }
        return sb.toString()
    }

    private fun setValues(lat : Double, lon : Double) {
        //add coordinates for send logFile in finish
        list.addLogInfo("${lat};${lon}")
        //set coordinates for send file
        list.setLat(lat)
        list.setLon(lon)
        //set coordinates for send file 5-minutes interval
        viewList.setGeoLat(lat)
        viewList.setGeoLon(lon)
    }

    fun setLocationUpdatesResult(context: Context, locations: List<Location>) {
        getDefaultSharedPreferences(context)
            .edit()
            .putString(
                KEY_LOCATION_UPDATES_RESULT,
                """
                ${getLocationResultTitle(context, locations)}
                ${getLocationResultText(context, locations)}
                """.trimIndent()
            )
            .apply()
    }

    fun getLocationUpdatesResult(context: Context?): String? {
        return getDefaultSharedPreferences(context)
            .getString(KEY_LOCATION_UPDATES_RESULT, "")
    }
}
