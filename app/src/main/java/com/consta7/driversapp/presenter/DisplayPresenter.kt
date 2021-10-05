package com.consta7.driversapp.presenter

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.consta7.driversapp.R
import com.consta7.driversapp.model.JsonFileConstructorImp
import com.consta7.driversapp.model.JsonParseImp
import com.consta7.driversapp.presenter.interfaces.DisplayInfo
import com.consta7.driversapp.view.interfaces.ListViewInfo

/** ALL LOGIC FOR DISPLAY INFORMATION HERE
 *  (TABLE, ALERT DIALOG, NOTIFICATION) //ALL THAT USER SEE
 * */

class DisplayPresenter : DisplayInfo {

    private var listView : ListViewInfo? = null
    private val construct : JsonFileConstructorImp = JsonFileConstructorImp()
    private val json : JsonParseImp = JsonParseImp()

    //initialization our view in presenter
    fun displayPresenter(listView : ListViewInfo) {
        this.listView = listView
    }

    //show list with information about clients on view
    @SuppressLint("InflateParams", "ResourceAsColor")
    override fun showTable(context: Context, table: TableLayout, lay: LayoutInflater) {
        table.removeAllViewsInLayout()

        for (i in 0 until json.getParseNumber("countClients").toInt()) {

            val customView = lay.inflate(R.layout.custom_view, null)
            val clientArray = arrayOf(
                json.getListParseValue("clients", i)
                    .substringBefore("Дог")
                    .substringBefore("дог")
                    .substringBeforeLast(".").trim(),
                json.getListParseValue("addressCl", i)
            )
            val numberApp: TextView = customView.findViewById(R.id.numberApplication)
            val clientInfo: ListView = customView.findViewById(R.id.clientInfo)
            val btnChoose: Button = customView.findViewById(R.id.btnChoose)
            val btnDialog : Button = customView.findViewById(R.id.btnDialog)
            val warning : ImageView = customView.findViewById(R.id.noteFlag)
            val cash : TextView = customView.findViewById(R.id.cashCollect)
            val pkoView : TextView = customView.findViewById(R.id.pko)
            val successPlace : LinearLayout = customView.findViewById(R.id.success)
            val btnSuccess : Button = customView.findViewById(R.id.btnSuccess)

            when (json.getArrayParseValue("doneClients", i).toInt()) {
                1 -> {
                    successPlace.visibility = View.VISIBLE
                    Color.parseColor("#32cd32").also {
                        numberApp.setTextColor(it)
                        cash.setTextColor(it)
                        pkoView.setTextColor(it)
                        btnChoose.setBackgroundColor(it)
                        btnDialog.setBackgroundColor(it)
                        btnSuccess.setBackgroundColor(it)
                    }
                }
                2 -> continue
                else -> {
                    successPlace.visibility = View.GONE
                    Color.parseColor("#023e8a").also {
                        numberApp.setTextColor(it)
                        cash.setTextColor(it)
                        pkoView.setTextColor(it)
                        btnChoose.setBackgroundColor(it)
                        btnDialog.setBackgroundColor(it)
                    }
                }
            }

            numberApp.text = ("#${i + 1}")
            clientInfo.adapter = ArrayAdapter(
                context, android.R.layout.simple_list_item_1, clientArray)
            warning.visibility = if (json.getListParseValue("notes", i) == "") View.INVISIBLE
            else View.VISIBLE
            cash.visibility =
                if (json.getListParseValue("settingCash", i) == "" ||
                    json.getListParseValue("settingCash", i)
                        .substring(
                            json.getListParseValue("settingCash", i).length - 3,
                            json.getListParseValue("settingCash", i).length - 2
                        ) == "0"
                ) View.INVISIBLE else View.VISIBLE
            pkoView.visibility = if (json.getListParseValue("pko", i) == "0") View.INVISIBLE else {
                pkoView.text = ("ПКО ${json.getListParseValue("pko", i)}")
                View.VISIBLE
            }
            btnChoose.text = ("Выбрать")
            btnChoose.setOnClickListener {
                selectedDoneClient(context, 2, 0)
                json.setDoneClient(i, 1)
                construct.setSelectIndex(i)
                showTable(context, table, lay)
            }
            btnDialog.text = ("Подробнее")
            btnDialog.setOnClickListener {
                showCard(context, i, lay)
            }
            btnSuccess.text = ("Доставлено")
            btnSuccess.setOnClickListener {
                json.setDoneClient(i, 2)
                construct.setSelectIndex(i)
                construct.writeFile(context, json.getParseValue("taskNumber"))
                showTable(context, table, lay)
            }
            table.addView(customView)
        }
    }

    //show info in dialog window about client
    @SuppressLint("InflateParams")
    override fun showCard(context: Context, needLine: Int, lay: LayoutInflater) {
        //custom fragment in dialog view
        val inflateView = lay.inflate(R.layout.custom_client_info, null)
        //elements in fragment
        val addressClient: TextView = inflateView.findViewById(R.id.addClient)
        val nameClient: TextView = inflateView.findViewById(R.id.nameClient)
        val numClient: TextView = inflateView.findViewById(R.id.numClient)
        val note : TextView = inflateView.findViewById(R.id.note)
        addressClient.text = json.getListParseValue("addressCl", needLine) //addressCl[needLine]
        nameClient.text = json.getListParseValue("clients", needLine)      //clients[needLine]
        numClient.text = json.getListParseValue("numbersCl", needLine)     //numbersCl[needLine]
        note.text = json.getListParseValue("notes", needLine)               //notes[needLine]
        //if note have a number client, we can a call
        note.setOnClickListener {
            listView?.showNumber(note.text.toString())
        }
        numClient.setOnClickListener {
            getNumber(context, numClient.text.toString())
        }        //transfer to the dialer with the transfer of the number there

        //display dialog window
        val alertCard = AlertDialog.Builder(context)
        alertCard.apply {
            //set attribute
            setTitle("Путевой лист ${json.getParseValue("taskNumber")}")    //hat dialog view
            setIcon(R.drawable.ico)                  //picture in hat
            setView(inflateView)                    //other content
            setCancelable(false)
            setPositiveButton("Назад") { _, _ -> }
        }
        val showInfo = alertCard.create()
        showInfo.show()
    }

    //if client have more then 1 number, we can choose the number
    private fun showNumberSelector(context: Context, first : String, second : String) {
        var checkedItem = 2
        val items = arrayOf(first, second)
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.apply {
            setTitle("Выберите номер")
            setSingleChoiceItems(items, checkedItem) {_, which ->
                when (which) {
                    0 -> checkedItem = 0
                    1 -> checkedItem = 1
                }
            }
            setPositiveButton("Подтвердить") { _, _ ->
                listView?.showNumber(items[checkedItem])
            }
            setNeutralButton("Назад") { _,_ ->}
        }
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()
    }

    //show dialog view for select more comfortable map app
    override fun showMapSelector(context: Context, index : Int) : String {
        //default settings
        var needMap = "Google"
        var uri = if (index == -1) {
            "${json.getParseNumber("startLat")}," +
                    json.getParseNumber("startLon")
        }
        else {
            "${json.getArrayParseValue("jsonLat", index)}," +
                    json.getArrayParseValue("jsonLon", index)
        }
        val checkedItem = 2
        val items = arrayOf("2-GIS","Google Maps") //variants for choose
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.apply {
            setTitle("Выберите карту")
            setSingleChoiceItems(items, checkedItem) { _, which ->
                when (which) {
                    0 -> {
                        needMap = "2-GIS"
                        uri = if (index == -1) {
                            "${json.getParseNumber("startLon")}," +
                                    json.getParseNumber("startLat")
                        }
                        else {
                            "${json.getArrayParseValue("jsonLon", index)}," +
                                    json.getArrayParseValue("jsonLat", index)
                        }
                    }
                    1 -> {
                        needMap = "Google"
                        uri = if (index == -1) {
                            "${json.getParseNumber("startLat")}," +
                                    json.getParseNumber("startLon")
                        }
                        else {
                            "${json.getArrayParseValue("jsonLat", index)}," +
                                    json.getArrayParseValue("jsonLon", index)
                        }
                    }
                }
            }
            setPositiveButton("Подтвердить") { _, _ ->
                if (index != -1) json.setDoneClient(index, 0)
                listView?.openMaps(needMap, uri)
            }
            setNeutralButton("Назад") { _,_ ->}
        }
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)

        alert.show()
        return needMap
    }

    //display push-notification with "back-event" on pressing
    override fun pushNotify(context: Context, pendingIntent: PendingIntent) {
        //get notify manager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //set attribute for our notify
        val push = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ico)
            .setContentIntent(pendingIntent)
            .setContentTitle("DriversApp")
            .setContentText("Нажмите, чтобы вернуться в приложение")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(Notification.DEFAULT_ALL)
        //if android api lvl > 26 we must select channel-parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val imp = NotificationManager.IMPORTANCE_HIGH
            val notifyChannel = NotificationChannel(channelId, channelName, imp)
            notificationManager.createNotificationChannel(notifyChannel)
            notificationManager.notify(0, push.build())
        } else notificationManager.notify(0, push.build())
    }

    fun selectedDoneClient(context: Context, flag : Int, index: Int) {  //or paint in green color
        val param = json.getArrayParseValue("doneClients", index).toInt()
        val count = json.getParseNumber("countClients").toInt()
        when (flag) {
            0 -> {
                if (param == 0 || param  == 2) return
                else { json.setDoneClient(index, 1); showMapSelector(context, index) }
            }  //select client
            1 -> {
                for (i in 0 until count) {
                    if (param == 0) continue
                    else json.setDoneClient(index, 0)
                }
            } //delete selected client
            else -> {
                for (i in 0 until count) {
                    if (json.getArrayParseValue("doneClients", i).toInt() == 0 ||
                        json.getArrayParseValue("doneClients", i).toInt() == 2) continue
                    else json.setDoneClient(i, 0)
                }
            } //clean all checked client
        }
    }

    private fun getNumber(context: Context, number : String) {
        val num = number.replace("-", "")
        var first = ""; var second = ""
        if (num.length >= 12) {
            first = num.substring(0,11)
            second = num.substring(12)
        }
        when {
            num.length == 10 -> listView?.showNumber("8$num")
            num.length == 11 -> listView?.showNumber(num)
            num.length >= 12 -> showNumberSelector(context, first, second)
        }
    }

    fun getDriverName() : String = json.getParseValue("driverName")
}