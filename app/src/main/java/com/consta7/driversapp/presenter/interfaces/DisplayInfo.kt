package com.consta7.driversapp.presenter.interfaces

import android.app.PendingIntent
import android.content.Context
import android.view.LayoutInflater
import android.widget.TableLayout

interface DisplayInfo {
    val channelName : String
        get() = "android_channel"
    val channelId : String
        get() = "com.intek.inteksar.driversapp"

    fun showTable(context: Context, table : TableLayout, lay: LayoutInflater)
    fun showCard(context: Context, needLine : Int, lay: LayoutInflater)
    fun showMapSelector(context: Context, index : Int) : String
    fun pushNotify(context: Context, pendingIntent : PendingIntent)
}