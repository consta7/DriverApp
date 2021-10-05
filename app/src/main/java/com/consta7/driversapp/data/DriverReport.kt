package com.consta7.driversapp.data

import com.google.gson.annotations.SerializedName

/** based name variable for work in project
 *  serialized name variable is key in json
 */

data class DriverReport(

    @SerializedName("driver")
    var driverName : String,

    @SerializedName("numDoc")
    var taskNumber : String,

    @SerializedName("locateLat")
    var geoLat : String,

    @SerializedName("locateLon")
    var geoLon : String,

    @SerializedName("id_doc")
    var idDocument : String,

    @SerializedName("client")
    var clients : String,

    @SerializedName("address")
    var addressCl : String,

    @SerializedName("note")
    var notes : String,
)
