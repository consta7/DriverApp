package com.consta7.driversapp.presenter

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.consta7.driversapp.R
import com.consta7.driversapp.data.DriverReport
import com.consta7.driversapp.model.DataBaseImp
import com.consta7.driversapp.presenter.interfaces.ListAbstract
import com.consta7.driversapp.view.interfaces.ListViewAbstract
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class ListPresenter : ListAbstract {

    private var listView : ListViewAbstract? = null
    private var data : DataBaseImp = DataBaseImp()
    private val handler : Handler = Handler(Looper.getMainLooper())

    fun listPresenter(listView: ListViewAbstract) {
        this.listView = listView
    }

    /** GETTING INFORMATION AND DISPLAYING OBJECT
     * /////////////////////////////////////////////////////////////////////////////////////////////
     * */

    //waiting information
    override fun successResult(code: Int, barcode : String) {
        //send request for data
        val result : Int = if (data.requestBarcode(barcode) == 1) 1 else 0
        Thread.sleep(1 * 1000L)
        //wait
        handler.postDelayed({ listView?.onSuccessResult(result, code) }, 3 * 1000L)
    }

    //wow, we can begin
    override fun successParse(code: Int) {
        val result = 1
        jsonArray = data.getJSON()
        listView?.onSuccessParse(result, code)
    }

    //if u don't know what is it, it is circle on view
    override fun progressBarVisible(code: Int) {
        listView?.onSetProgressBarVisible(code)
    }

    //it is table and button panel on view
    override fun informationVisible(code : Int) {
        listView?.onSetInformationVisible(code)
    }

    override fun successWrite(fileName : String) {
        data.requestData(fileName)
    }

    //parse json object "data" to information about driver task
    override fun jsonParser(jsonArray: JSONArray?) {
        var count = 0
        //parse main info: driver name; number waybill; start point
        driverName = jsonArray!!.getJSONObject(0).getString("driver")
        taskNumber = jsonArray.getJSONObject(0).getString("waybillNo")
        startLat = (jsonArray.getJSONObject(0).getString("latStart")).toDouble()
        startLon = (jsonArray.getJSONObject(0).getString("lonStart")).toDouble()
        //while "data" have no-null info, parse information
        while (true) {
            try {
                if (jsonArray.getJSONObject(count).getString("StrNo") == "") break
            } catch (e : Exception) { break }

            if (jsonArray.getJSONObject(count)!!.getString("Client") == "ТОЧКИ КАРТЫ") {
                count++
                continue
            }
            //serial number client
            jsonLat += (jsonArray.getJSONObject(count)?.getString("lat"))!!.toDouble()
            jsonLon += (jsonArray.getJSONObject(count)?.getString("lon"))!!.toDouble()
            strDigits += (jsonArray.getJSONObject(count)!!.getString("StrNo")).toInt()
            //names, addresses, numbers and other
            clients = clients + jsonArray.getJSONObject(count)!!.getString("Client")
            addressCl = addressCl + jsonArray.getJSONObject(count)!!.getString("Address")
                .replace(",,", ",")
                .substring(1)
                .substringBeforeLast(",")
            numberAp = numberAp + jsonArray.getJSONObject(count)!!.getString("invoiceIDDOC")
            numbersCl = numbersCl + jsonArray.getJSONObject(count)!!.getString("Tel")
            contactFace = contactFace + jsonArray.getJSONObject(count)!!.getString("StrNo")
            notes = notes + jsonArray.getJSONObject(count)!!.getString("Prim")
            settingCash = settingCash + jsonArray.getJSONObject(count)!!
                .getString("Settings")
            pko = pko + jsonArray.getJSONObject(count)!!.getString("Pko")
            doneClients += 0

            count++
        }
        countClients = clients.count()
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

    /**SHOW INFORMATION IN VIEW
     * /////////////////////////////////////////////////////////////////////////////////////////////
     * */

    //show info in dialog window about client
    @SuppressLint("InflateParams")
    override fun showCard(context: Context, needLine : Int, lay: LayoutInflater) {
        //custom fragment in dialog view
        val inflateView = lay.inflate(R.layout.custom_client_info, null)
        //elements in fragment
        val addClient: TextView = inflateView.findViewById(R.id.addClient)
        val nameClient: TextView = inflateView.findViewById(R.id.nameClient)
        val numClient: TextView = inflateView.findViewById(R.id.numClient)
        val note : TextView = inflateView.findViewById(R.id.note)
        addClient.text = addressCl[needLine]
        nameClient.text = clients[needLine]
        numClient.text = numbersCl[needLine]
        note.text = notes[needLine]
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
            setTitle("Путевой лист $taskNumber")    //hat dialog view
            setIcon(R.drawable.ico)                  //picture in hat
            setView(inflateView)                    //other content
            setCancelable(false)
            setPositiveButton("Назад") { _, _ -> }
        }
        val showInfo = alertCard.create()
        showInfo.show()
    }

    //show dialog view for select more comfortable map app
    fun showMapSelector(context: Context, index : Int) : String {
        //default settings
        var needMap = "Google"
        var uri = if (index == -1) "$startLat,$startLon"
                    else "${jsonLat[index]},${jsonLon[index]}"
        val checkedItem = 2
        val items = arrayOf("2-GIS","Google Maps") //variants for choose
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.apply {
            setTitle("Выберите карту")
            setSingleChoiceItems(items, checkedItem) { _, which ->
                when (which) {
                    0 -> {
                        needMap = "2-GIS"
                        uri = if (index == -1) "$startLon,$startLat"
                                else "${jsonLon[index]},${jsonLat[index]}"
                    }
                    1 -> {
                        needMap = "Google"
                        uri = if (index == -1) "$startLat,$startLon"
                                else "${jsonLat[index]},${jsonLon[index]}"
                    }
                }
            }
            setPositiveButton("Подтвердить") { _, _ ->
                if (index != -1) doneClients[index] = 0
                listView?.openMaps(needMap, uri)
           }
           setNeutralButton("Назад") { _,_ ->}
        }
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()
        return needMap
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

    //show list with information about clients on view
    @SuppressLint("InflateParams", "ResourceAsColor")
    override fun showTable(context: Context, table : TableLayout, lay: LayoutInflater) {
        //we have info in "data", but don't have info in other lists
        if (clients.isEmpty()) jsonParser(jsonArray)

        table.removeAllViewsInLayout()

        for (i in 0 until countClients) {

            val customView = lay.inflate(R.layout.custom_view, null)
            val clientArray = arrayOf(
                clients[i]
                    .substringBefore("Дог")
                    .substringBefore("дог")
                    .substringBeforeLast(".").trim(),
                addressCl[i]
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

            when (doneClients[i]) {
                1 -> {
                    successPlace.visibility = View.VISIBLE
                    Color.parseColor("#32cd32").also {
                        numberApp.setTextColor(it)
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
            warning.visibility = if (notes[i] == "") View.INVISIBLE else View.VISIBLE
            cash.visibility =
                if (settingCash[i] == "" || settingCash[i]
                        .substring(settingCash[i].length - 3, settingCash[i].length - 2) == "0"
                ) View.INVISIBLE else View.VISIBLE
            pkoView.visibility = if (pko[i] == "0") View.INVISIBLE else {
                pkoView.text = ("ПКО ${pko[i]}")
                View.VISIBLE
            }
            btnChoose.text = ("Выбрать")
            btnChoose.setOnClickListener {
                selectedDoneClient(context, 2, 0)
                doneClients[i] = 1
                selectIndex = i
                showTable(context, table, lay)
            }
            btnDialog.text = ("Подробнее")
            btnDialog.setOnClickListener {
                showCard(context, i, lay)
            }
            btnSuccess.text = ("Доставлено")
            btnSuccess.setOnClickListener {
                //listView?.onClickLayout()
                //if (confirmResult(i, true)) {
                    doneClients[i] = 2
                    selectIndex = i
                    listView?.writeInFile(context, taskNumber, getNowTime(), 2)
                    interimLog = ""
                    showTable(context, table, lay)
                //} else Toast.makeText(context, "Ошибка получения разрешения!", Toast.LENGTH_SHORT).show()
            }
            table.addView(customView)
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

    @SuppressLint("InflateParams")
    override fun selectedDoneClient(context: Context, flag : Int, index: Int) {  //or paint in green color
        when (flag) {
            0 -> {
                if (doneClients[index] == 0 || doneClients[index] == 2) return
                else { doneClients[index] = 1; showMapSelector(context, index) }
            }  //select client
            1 -> {
                for (i in doneClients) {
                    if (doneClients[index] == 0) continue
                    else doneClients[index] = 0
                }
            } //delete selected client
            else -> {
                for (i in 0 until doneClients.count()) {
                    if (doneClients[i] == 0 || doneClients[i] == 2) continue
                    else doneClients[i] = 0
                }
            } //clean all checked client
        }
    }

    override fun createJsonFile() {
        if (taskNumber != "") {
            val reportFile = DriverReport(
                driverName = driverName,
                taskNumber = taskNumber,
                geoLat = geoLat.toString(),
                geoLon = geoLon.toString(),
                idDocument = numbersCl[selectIndex],
                clients = clients[selectIndex],
                addressCl = addressCl[selectIndex],
                numbersCl = numbersCl[selectIndex],
                contactFace = contactFace[selectIndex],
                notes = notes[selectIndex],
                settingCash = settingCash[selectIndex],
                pko = pko[selectIndex]
            )
            val gson = Gson()
            report = gson.toJson(reportFile)
        }
    }

    fun setPhoneID(id : Int) : Int {
        if (assignmentID) {
            phoneID = id + Random.nextInt(1, 100)
            assignmentID = true
        }
        return phoneID
    }

    private fun getNowTime() : String {
        val sdf = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.US)
        val currentDate = sdf.format(Date()).substring(0, 8)
        val currentTime = timeStrToSeconds(sdf.format(Date()).substring(9, 17))
        val needDate = currentDate.substring(6,8) + "." + currentDate.substring(4,6) + "." + currentDate.substring(2,4)
        val hours = ((currentTime / 60) / 60)
        val minutes = (currentTime / 60) - (hours * 60)
        val nowTime = if (minutes < 10) "${hours + 4}:0$minutes" else "${hours + 4}:$minutes"
        return "${nowTime}_${needDate}"
    }

    private fun timeStrToSeconds(str: String): Int {
        val parts = str.split(":")
        var result = 0
        for (part in parts) {
            val number = part.toInt()
            result = result * 60 + number
        }
        return result
    }

    fun addLogInfo(newLog : String) {
        logReport += "\n ${newLog};${phoneID};${getNowTime()}"
        if (interimLog == "") interimLog += "${clients[selectIndex]} : done; \n"
        interimLog += "\n ${newLog};${phoneID};${getNowTime()}"

    }

    /**GET AND SET COORDINATES AND CLIENTS COUNT FOR THE NEXT USAGE
     * /////////////////////////////////////////////////////////////////////////////////////////////
     * */
    fun getNumberTask() : String = if (taskNumber != "") taskNumber.substring(1) else ""

    override fun getLat() : Array<Double> = jsonLat
    override fun getLon() : Array<Double> = jsonLon
    override fun getReport() : String = report

    fun getDriverName() : String = driverName
    fun getCount() : Int = countClients
    fun getIndex() : Int = selectIndex

    override fun setLat(lat : Double) {
        geoLat = lat
    }
    override fun setLon(lon : Double) {
        geoLon = lon
    }

    /** NOW ALL INFORMATION ABOUT CLIENTS STORED HERE (COMPANION OBJECT)
     * /////////////////////////////////////////////////////////////////////////////////////////////
     * */
    companion object {
        var countClients : Int = 0
        var jsonArray : JSONArray? = null
        var driverName = ""
        var taskNumber = ""
        var jsonLat = emptyArray<Double>()
        var jsonLon = emptyArray<Double>()
        var strDigits = emptyArray<Int>()
        var numberAp : List<String> = listOf()
        var clients : List<String> = listOf()
        var addressCl : List<String> = listOf()
        var numbersCl : List<String> = listOf()
        var contactFace : List<String> = listOf()
        var notes : List<String> = listOf()
        var doneClients : Array<Int> = arrayOf()
        var settingCash : List<String> = listOf()
        var pko : List<String> = listOf()
        var startLat = 0.0
        var startLon = 0.0

        var report = ""
        var logReport = ""
        var interimLog = ""

        var geoLat = 0.0
        var geoLon = 0.0
        var selectIndex = 0

        const val channelName = "android_channel"
        const val channelId = "com.consta7.driversapp"
        private var assignmentID : Boolean = false
        var phoneID : Int = 0
    }
}