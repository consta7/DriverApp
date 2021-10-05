package com.consta7.driversapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class Global: AppCompatActivity() {

    val tag : String = "project_by_const7"
    private val sdkVersion = Build.VERSION.SDK_INT
    private val actionBarcodeData = "com.honeywell.sample.action.BARCODE_DATA"
    private val actionClaimScanner = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER"
    private val actionReleaseScanner = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER"
    private val extraScanner = "com.honeywell.aidc.extra.EXTRA_SCANNER"
    private val extraProfile = "com.honeywell.aidc.extra.EXTRA_PROFILE"
    private val extraProperties = "com.honeywell.aidc.extra.EXTRA_PROPERTIES"

    @SuppressLint("QueryPermissionsNeeded")
    fun sendImplicitBroadcast(ctxt: Context, i: Intent) {
        val pm = ctxt.packageManager
        val matches = pm.queryBroadcastReceivers(i, 0)

        for (resolveInfo in matches) {
            val explicit = Intent(i)
            val cn = ComponentName(
                resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name)
            explicit.component = cn
            ctxt.sendBroadcast(explicit)
        }
    }

    private fun mySendBroadcast(intent: Intent) {
        if (sdkVersion < 26) sendBroadcast(intent)
        else sendImplicitBroadcast(applicationContext, intent)
    }

    fun releaseScanner() {
        Log.d(tag, "releaseScanner")
        mySendBroadcast(Intent(actionReleaseScanner))
    }

    fun claimScanner() {
        Log.d(tag, "claimScanner")
        val properties = Bundle()
        properties.putBoolean("DPR_DATA_INTENT", true)
        properties.putString("DPR_DATA_INTENT_ACTION", actionBarcodeData)
        properties.putInt("TRIG_AUTO_MODE_TIMEOUT", 2)
        properties.putString(
            "TRIG_SCAN_MODE",
            "readOnRelease"
        )
        mySendBroadcast(
            Intent(actionClaimScanner)
                .putExtra(extraScanner, "dcs.scanner.imager")
                .putExtra(extraProfile, "DEFAULT")
                .putExtra(extraProperties, properties)
        )
    }
}