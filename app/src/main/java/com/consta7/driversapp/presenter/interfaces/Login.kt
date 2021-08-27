package com.consta7.driversapp.presenter.interfaces

import android.app.Activity
import android.content.Context

interface Login {

    fun checkPermission(context: Context, activity: Activity)
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
        activity: Activity
    )

}