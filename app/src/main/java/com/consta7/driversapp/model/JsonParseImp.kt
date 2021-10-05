package com.consta7.driversapp.model

import com.consta7.driversapp.model.interfaces.JsonParse
import org.json.JSONArray

class JsonParseImp : JsonParse {

    private var jsonArray : JSONArray? = null
    private var state = 0

    override fun setJson(jsonArray: JSONArray?) {
        this.jsonArray = jsonArray
        jsonParser(jsonArray)
    }

    override fun jsonParser(jsonArray: JSONArray?) : Int {
        if (jsonArray == null) return state

        var count = 0
        //parse main info: driver name; number waybill; start point
        driverName = jsonArray
            .getJSONObject(0).getString("driver")

        taskNumber = jsonArray
            .getJSONObject(0).getString("waybillNo")

        startLat = (jsonArray
            .getJSONObject(0).getString("latStart")).toDouble()

        startLon = (jsonArray
            .getJSONObject(0).getString("lonStart")).toDouble()
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
            jsonLat += (jsonArray
                .getJSONObject(count)?.getString("lat"))!!.toDouble()

            jsonLon += (jsonArray
                .getJSONObject(count)?.getString("lon"))!!.toDouble()

            strDigits += (jsonArray
                .getJSONObject(count)!!.getString("StrNo")).toInt()

            //names, addresses, numbers and other
            clients = clients + jsonArray
                .getJSONObject(count)!!.getString("Client")

            addressCl = addressCl + jsonArray
                .getJSONObject(count)!!
                .getString("Address")
                .replace(",,", ",")
                .substring(1)
                .substringBeforeLast(",")

            numberAp = numberAp + jsonArray
                .getJSONObject(count)!!.getString("invoiceIDDOC")

            numbersCl = numbersCl + jsonArray
                .getJSONObject(count)!!.getString("Tel")

            contactFace = contactFace + jsonArray
                .getJSONObject(count)!!.getString("StrNo")

            notes = notes + jsonArray
                .getJSONObject(count)!!.getString("Prim")

            settingCash = settingCash + jsonArray
                .getJSONObject(count)!!.getString("Settings")

            pko = pko + jsonArray
                .getJSONObject(count)!!.getString("Pko")

            doneClients += 0

            count++
        }
        countClients = clients.count()
        state = 1

        return state
    }

    override fun getParseValue(tag : String) : String {
        return when (tag) {
            "driverName" -> driverName
            "taskNumber" -> taskNumber
            else -> "Error : Tag not found"
        }
    }

    override fun getArrayParseValue(tag : String, index : Int) : String {
        return when (tag) {
            "jsonLat" -> jsonLat[index].toString()
            "jsonLon" -> jsonLon[index].toString()
            "strDigits" -> strDigits[index].toString()
            "doneClients" -> doneClients[index].toString()
            else -> "Error : Tag not found"
        }
    }

    override fun getListParseValue(tag : String, index: Int) : String {
        return when (tag) {
            "numberAp" -> numberAp[index]
            "clients" -> clients[index]
            "addressCl" -> addressCl[index]
            "numbersCl" -> numbersCl[index]
            "contactFace" -> contactFace[index]
            "notes" -> notes[index]
            "settingCash" -> settingCash[index]
            "pko" -> pko[index]
            else -> "Error : Tag not found"
        }
    }

    override fun getParseNumber(tag : String) : String {
        return when (tag) {
            "countClients" -> countClients.toString()
            "startLat" -> startLat.toString()
            "startLon" -> startLon.toString()
            else -> "Error : Tag not found"
        }
    }

    override fun setDoneClient(index : Int, value : Int) {
        doneClients[index] = value
    }

    fun getCoordinatesArray(type : Int) : Array<Double> {
        return when (type) {
            0 -> jsonLat
            else -> jsonLon
        }
    }

    companion object {
        var countClients : Int = 0
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
    }

}