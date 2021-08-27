package com.consta7.driversapp.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.consta7.driversapp.presenter.interfaces.Login

class LoginPresenter : Login {

    private var permCamera : Int = 71
    private var permLocation : Int = 72
    private var permAlert : Int = 75
    private var permBackground : Int = 34

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun checkPermission(context: Context, activity: Activity) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                permBackground
            )
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.CAMERA), permCamera)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permLocation)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW), permAlert)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray, activity: Activity) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permBackground -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission it was provided
                } else {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), permBackground)
                }
            }
            permCamera -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission it was provided
                } else {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.CAMERA), permCamera)
                }
                return
            }
            permLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission it was provided
                } else {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permLocation)
                }
                return
            }
            permAlert -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission it was provided
                } else {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW), permAlert)
                }
                return
            }
        }
    }
}